package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/**
 * Assumes vertex 0 as center, and that other vertices form a fan around it.
 * Concludes to being a {@link TriangleZeroToAllGenerator} but with an additional triangle from (0, n-1, 1).
 */
public class TriangleZeroAsFanGenerator implements IIndexGenerator {

    public static final TriangleZeroAsFanGenerator INSTANCE = new TriangleZeroAsFanGenerator();

    private TriangleZeroToAllGenerator core = TriangleZeroToAllGenerator.INSTANCE;

    @Override
    public int getNumIndexEntries(int numVertices) {
        int iAdd = (numVertices >= 4 ? 3 : 0);
        return core.getNumIndexEntries(numVertices) + iAdd;
    }

    @Override
    public void feed(IndexDataBuffer buffer, int numVertices) {
        core.feed(buffer, numVertices);
        if (numVertices >= 4) {
            buffer.addLocalIndexEntry(0);
            buffer.addLocalIndexEntry(numVertices - 1);
            buffer.addLocalIndexEntry(1);
        }
    }

}