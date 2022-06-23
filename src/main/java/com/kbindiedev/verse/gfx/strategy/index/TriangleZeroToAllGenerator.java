package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/** Assumes that vertex 0 is core, and that indices should be connected in such order (0 1 2, 0 2 3, 0 3 4). */
public class TriangleZeroToAllGenerator implements IIndexGenerator {

    public static final TriangleZeroToAllGenerator INSTANCE = new TriangleZeroToAllGenerator();

    @Override
    public int getNumIndexEntries(int numVertices) {
        if (numVertices < 3) return 0;
        return (numVertices - 2) * 3;
    }

    @Override
    public void feed(IndexDataBuffer buffer, int numVertices) {
        for (int i = 2; i < numVertices; ++i) {
            buffer.addLocalIndexEntry(0);
            buffer.addLocalIndexEntry(i - 1);
            buffer.addLocalIndexEntry(i);
        }
    }

}