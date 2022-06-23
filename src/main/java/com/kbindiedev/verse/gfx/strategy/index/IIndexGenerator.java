package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;

/** Generates indices on the fly in some pattern. */
public interface IIndexGenerator {

    /** @return the number of index entries that this generator will make for the given number of vertices. numVertices >= 1. */
    int getNumIndexEntries(int numVertices);

    /** Feed the given buffer an amount of index entries, equal to the number gotten by {@link #getNumIndexEntries(int)}. numVertices >= 1. */
    void feed(IndexDataBuffer buffer, int numVertices);

}