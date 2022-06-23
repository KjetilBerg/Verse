package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/** An empty index generator. */
public class NoIndexGenerator implements IIndexGenerator {
    public static final NoIndexGenerator INSTANCE = new NoIndexGenerator();
    @Override
    public int getNumIndexEntries(int numVertices) { return 0; }
    @Override
    public void feed(IndexDataBuffer buffer, int numVertices) {}
}