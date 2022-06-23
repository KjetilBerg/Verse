package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/** Generates indices for points. */
public class PointIndexGenerator implements IIndexGenerator {

    public static final PointIndexGenerator INSTANCE = new PointIndexGenerator();

    @Override
    public int getNumIndexEntries(int numVertices) { return numVertices; }

    @Override
    public void feed(IndexDataBuffer buffer, int numVertices) {
        for (int i = 0; i < numVertices; ++i) buffer.addLocalIndexEntry(i);
    }
}