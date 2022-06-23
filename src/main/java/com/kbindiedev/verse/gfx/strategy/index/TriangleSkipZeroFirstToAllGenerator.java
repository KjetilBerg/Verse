package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/** A {@link TriangleZeroToAllGenerator} that shifts everything to the left (so, first to all, and vertex 0 is completely ignored). */
public class TriangleSkipZeroFirstToAllGenerator implements IIndexGenerator {

    public static final TriangleSkipZeroFirstToAllGenerator INSTANCE = new TriangleSkipZeroFirstToAllGenerator();

    private TriangleZeroToAllGenerator core = TriangleZeroToAllGenerator.INSTANCE;

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