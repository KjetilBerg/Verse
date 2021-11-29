package com.kbindiedev.verse.gfx.strategy.attributes;

/** Stores data for a single Vertex attribute. Note: only used internally by VertexAttributes */
public class VertexAttribute {

    int baseOffset;

    private int index, count, type;
    private boolean normalized;

    VertexAttribute(int index, int count, int type, boolean normalized, int baseOffset) {
        this.index = index; this.count = count; this.type = type; this.normalized = normalized; this.baseOffset = baseOffset;
    }

    public int getIndex() { return index; }
    public int getCount() { return count; }
    public int getType() { return type; }
    public int getBaseOffset() { return baseOffset; }
    public boolean isNormalized() { return normalized; }

}