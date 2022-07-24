package com.kbindiedev.verse.util;

import java.io.IOException;
import java.io.InputStream;

/** An InputStream that can be prefixed with some header value. */
public class InsertionStream extends InputStream {

    private byte[] header;
    private int headerIndex;
    private InputStream stream;

    public InsertionStream(byte header, InputStream stream) { this(new byte[]{ header }, stream); }
    public InsertionStream(byte[] header, InputStream stream) {
        this.header = header;
        headerIndex = 0;
        this.stream = stream;
    }

    @Override
    public int read() throws IOException {
        if (headerIndex < header.length) return header[headerIndex++];
        return stream.read();
    }

}