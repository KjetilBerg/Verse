package com.kbindiedev.verse.gfx.strategy.providers;

import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import com.kbindiedev.verse.profiling.exceptions.NotEnoughVerticesException;

import java.nio.ByteBuffer;

/** Feeds vertex data to ByteBuffers. */
public interface IVertexProvider {

    /** @return the attributes that this provides vertices according to. */
    VertexAttributes getVertexAttributes();

    /** Feed a bunch of vertex data to a buffer. */
    default void feed(ByteBuffer buffer, long numVertices) throws NotEnoughVerticesException {
        while (numVertices-- > 0) feedSingle(buffer);
    }

    /** Feed vertex data corresponding to a single vertex to a buffer. */
    void feedSingle(ByteBuffer buffer) throws NotEnoughVerticesException;

}