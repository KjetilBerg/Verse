package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/** A {@link LineIndexSkipFirstGenerator} but with a line from the first to second vertex. */
public class LineIndexSkipFirstWithDirectionLineGenerator implements IIndexGenerator {

    public static final LineIndexSkipFirstWithDirectionLineGenerator INSTANCE = new LineIndexSkipFirstWithDirectionLineGenerator();

    private LineIndexSkipFirstGenerator core = LineIndexSkipFirstGenerator.INSTANCE;

    @Override
    public int getNumIndexEntries(int numVertices) {
        int iAdd = (numVertices >= 2 ? 2 : 0);
        return core.getNumIndexEntries(numVertices) + iAdd;
    }

    @Override
    public void feed(IndexDataBuffer buffer, int numVertices) {
        core.feed(buffer, numVertices);
        if (numVertices >= 2) {
            buffer.addLocalIndexEntry(0);
            buffer.addLocalIndexEntry(1);
        }
    }

}