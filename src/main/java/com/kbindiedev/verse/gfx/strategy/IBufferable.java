package com.kbindiedev.verse.gfx.strategy;

import java.nio.ByteBuffer;

public interface IBufferable {

    /**
     * Buffer data to the IBufferable.
     * @throws IndexOutOfBoundsException - If offset + numBytes exceeds the currently allocated memory.
     * @param offset - The offset into the buffer to start writing data to.
     * @param data - The data to write. Note: position shall not be changed before writing, though limit may change.
     * @param numBytes - The number of bytes to write.
     */
    void buffer(long offset, ByteBuffer data, long numBytes) throws IndexOutOfBoundsException;

    /** @return the size of the buffer in bytes. Returns -1 if buffer is unlimited. */
    long bufferByteSize();

}
