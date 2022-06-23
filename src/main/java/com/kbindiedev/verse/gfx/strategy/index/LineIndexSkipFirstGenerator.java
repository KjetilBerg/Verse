package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/** A {@link LineIndexGenerator} but skips the first point. */
public class LineIndexSkipFirstGenerator implements IIndexGenerator {

    public static final LineIndexSkipFirstGenerator INSTANCE = new LineIndexSkipFirstGenerator();

    private LineIndexGenerator core = LineIndexGenerator.INSTANCE;

    @Override
    public int getNumIndexEntries(int numVertices) {
        return core.getNumIndexEntries(numVertices - 1);
    }

    @Override
    public void feed(IndexDataBuffer buffer, int numVertices) {
        buffer.setBaseVertex(buffer.getBaseVertex() + 1);
        core.feed(buffer, numVertices - 1);
        buffer.setBaseVertex(buffer.getBaseVertex() - 1);
    }
}
