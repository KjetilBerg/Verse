package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/** Generates indices for a continuous line. */
public class LineIndexGenerator implements IIndexGenerator {

    public static final LineIndexGenerator INSTANCE = new LineIndexGenerator();

    @Override
    public int getNumIndexEntries(int numVertices) {
        if (numVertices == 1) return 0;
        if (numVertices == 2) return 1;
        return numVertices * 2;
    }

    @Override
    public void feed(IndexDataBuffer buffer, int numVertices) {
        if (numVertices == 1) return;

        for (int i = 0; i < numVertices - 1; ++i) {
            buffer.addLocalIndexEntry(i);
            buffer.addLocalIndexEntry(i + 1);
        }

        if (numVertices == 2) return;

        buffer.addLocalIndexEntry(numVertices - 1);
        buffer.addLocalIndexEntry(0);
    }
}