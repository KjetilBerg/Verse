package com.kbindiedev.verse.system.buffer;

import com.kbindiedev.verse.profiling.exceptions.NotEnoughMemoryException;
import com.kbindiedev.verse.system.memory.RecycleFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * An IChunk implementation that utilizes multiple chunks for storing information.
 * Thread safe for reading and writing simultaneously (though single reader and single writer).
 */
public class ChunkMaster implements IChunk {

    private final RecycleFactory<ByteArrayChunk> factory;
    private final ConcurrentLinkedDeque<ByteArrayChunk> chunks;
    private final int chunkSize;

    private boolean closed;
    private long bytesRead, bytesWritten;

    public ChunkMaster(int chunkSize, int maxChunks) {
        factory = new RecycleFactory<>(() -> new ByteArrayChunk(chunkSize), maxChunks);
        chunks = new ConcurrentLinkedDeque<>();
        this.chunkSize = chunkSize;

        closed = false;
        bytesRead = 0;
        bytesWritten = 0;
    }

    @Override
    public int availableWrite() {
        int maxRemaining = factory.maxInstancesRemaining();
        if (maxRemaining == Integer.MAX_VALUE) return Integer.MAX_VALUE;

        int availableWriteCurrentChunk = 0;
        IChunk activeWrite = chunks.getLast();
        if (activeWrite != null) availableWriteCurrentChunk = activeWrite.availableWrite();

        return availableWriteCurrentChunk + maxRemaining * chunkSize;
    }

    @Override
    public int available() { return (int)(bytesWritten - bytesRead); }

    /** Write, theoretically non-blocking. */
    @Override
    public void write(int b) throws IOException {
        if (closed) throw new IOException("chunk is closed");

        try {
            ByteArrayChunk activeWrite = getLastChunk();
            if (activeWrite.remainingWrites() == 0) chunks.add(factory.retrieve());
        } catch (NotEnoughMemoryException e) { throw new IOException(e); }

        getLastChunk().write(b);
        bytesWritten++;
    }

    /** Read, blocking. */
    @Override
    public int read() throws IOException {
        if (closed) throw new IOException("trying to read from a closed chunk");

        ByteArrayChunk activeRead = getFirstChunk();

        bytesRead++;
        int d = activeRead.read();
        if (d >= 0) return d;

        // d is negative = "end of chunk"
        bytesRead--; // undo recursive read
        chunks.removeFirst();
        factory.recycle(activeRead);
        return read();

    }

    /** Get last chunk, or create one if none exist. */
    private ByteArrayChunk getLastChunk() throws NotEnoughMemoryException {
        ByteArrayChunk chunk = chunks.getLast();
        if (chunk != null) return chunk;
        synchronized (chunks) {
            ByteArrayChunk newChunk = factory.retrieve();
            chunks.add(newChunk);
            chunks.notify();
            return newChunk;
        }
    }

    /** Get first chunk, or block until it is available. */
    private ByteArrayChunk getFirstChunk() throws IOException {
        ByteArrayChunk chunk = chunks.getFirst();
        if (chunk != null) return chunk;
        synchronized (chunks) {
            try { while (chunks.getFirst() == null) chunks.wait(); } catch (InterruptedException e) {
                throw new IOException("unexpected interrupt while acquiring first chunk. exception: " + e.getMessage());
            }
            return chunks.getFirst();
        }
    }

    @Override
    public void close() {
        for (IChunk chunk : chunks) chunk.close();
        chunks.clear();
        factory.erase();
        closed = true;
    }

}