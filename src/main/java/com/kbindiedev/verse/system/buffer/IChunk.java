package com.kbindiedev.verse.system.buffer;

import java.io.Closeable;
import java.io.IOException;

/**
 * A structure that can be written to and later read from, that is blocking.
 *
 * Blocking means that upon a {@link #read()} or {@link #write(int)}, that if data cannot be read/written, that the current thread will halt and wait until it is able to do so.
 * This has to follow the contract of {@link #available()} and {@link #availableWrite()}.
 */
public interface IChunk extends Closeable {

    /** @return the minimum number of bytes that I can write to this chunk now before {@link #write(int)} becomes blocking. */
    int availableWrite();
    /** @return the minimum number of available bytes that can be read before {@link #read()} becomes blocking. */
    int available();

    /**
     * Write a byte (value 0 - 255). must be blocking if it is possible to ever write the given data.
     * @throws IOException if an I/O error occurs (or if the chunk is "closed").
     */
    void write(int b) throws IOException;

    /**
     * Read a byte (value 0 - 255). must be blocking if it is possible to ever read some data. value will be -1 if the end of the chunk is reached.
     * @throws IOException if an I/O error occurs.
     */
    int read() throws IOException;

    /** Close this chunk so that it can no longer be written to nor read from. */
    void close();

}