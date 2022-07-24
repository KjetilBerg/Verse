package com.kbindiedev.verse.system.buffer;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.NotEnoughMemoryException;
import com.kbindiedev.verse.system.memory.IRecyclable;

import java.io.IOException;

/**
 * An IChunk revolving around local memory.
 * Thread safe for reading and writing simultaneously (though single reader and single writer).
 */
public class ByteArrayChunk implements IChunk, IRecyclable {

    private final byte[] data;
    private int writeIndex, readIndex;
    private boolean closed;

    public ByteArrayChunk(int size) {
        data = new byte[size];
        writeIndex = 0;
        readIndex = 0;
        closed = false;
    }

    /** @return the number of bytes that may be written to this chunk before it is out of memory. */
    public int remainingWrites() { return data.length - writeIndex; }

    @Override public int availableWrite() { return remainingWrites(); }
    @Override public int available() { return writeIndex - readIndex; }

    /** Write a byte. theoretically non-blocking. */
    @Override
    public void write(int b) throws IOException {
        if (closed) throw new IOException("chunk is closed");
        if (writeIndex >= data.length) throw new NotEnoughMemoryException("not enough memory for the chunk");

        // TODO: testing performance: synchronized vs non-sync (throw IOException in read() instead)
        synchronized (data) {
            data[writeIndex++] = (byte)b;
            data.notify();
        }

    }

    /** Read a byte. blocking. */
    @Override
    public int read() throws IOException {
        if (readIndex >= data.length) return -1;
        if (closed) throw new IOException("trying to read from a closed chunk");

        if (available() > 0) return data[readIndex++] & 0xFF;

        synchronized (data) {
            try {
                while (available() == 0 && !closed) data.wait();
                if (closed) throw new IOException("trying to read from a closed chunk");
                return data[readIndex++] & 0xFF;
            } catch (InterruptedException e) {
                Assertions.warn("unexpected interrupt, assuming end of stream. exception:");
                e.printStackTrace();
                close();
                return -1;
            }
        }
    }

    @Override
    public void close() {
        synchronized (data) {
            closed = true;
            data.notifyAll();
        }
    }

    // note: recycle while writing may cause buggy behaviour if "synchronized" implementation by JDK is not FIFO.
    //          it is assumed that no writing will happen when the chunk is being recycled.
    @Override
    public void recycle() {
        close();
        synchronized (data) {
            writeIndex = 0;
            readIndex = 0;
            closed = false;
        }
    }
}