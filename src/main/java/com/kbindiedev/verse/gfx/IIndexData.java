package com.kbindiedev.verse.gfx;

import java.nio.ByteBuffer;

public interface IIndexData {

    /** Set the number of indices. */
    void setNumIndices(int indices);

    /**
     * Get the number of indices currently stored in this buffer.
     * Excess data from the underlying buffer should be ignored by users.
     * @return the number of stored indices.
     */
    int getNumIndices();

    /**
     * Get the underlying bytebuffer. Must be direct.
     * @return the underlying bytebuffer.
     */
    ByteBuffer getBuffer();

}
