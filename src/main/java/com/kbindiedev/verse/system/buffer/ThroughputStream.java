package com.kbindiedev.verse.system.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class that has an OutputStream that can be written to, cached, and later read from its InputStream.
 * Thread safe for reading and writing simultaneously (though single reader and single writer).
 */
public class ThroughputStream {

    private ChunkMaster chunkMaster;
    private InputStream input;
    private OutputStream output;

    public ThroughputStream() { this(16384); }
    public ThroughputStream(int chunkSize) { this(chunkSize, 0); }
    public ThroughputStream(int chunkSize, int maxChunks) {
        chunkMaster = new ChunkMaster(chunkSize, maxChunks);

        input = new InputStream() {
            @Override public int read() throws IOException { return chunkMaster.read(); }
            @Override public int available() { return chunkMaster.available(); }
        };

        output = new OutputStream() {
            @Override public void write(int b) throws IOException { chunkMaster.write((byte)b); }
        };
    }

    public InputStream getInputStream() { return input; }
    public OutputStream getOutputStream() { return output; }

    public void close() {
        chunkMaster.close();
    }

}