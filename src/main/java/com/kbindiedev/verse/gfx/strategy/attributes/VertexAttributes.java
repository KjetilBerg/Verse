package com.kbindiedev.verse.gfx.strategy.attributes;

import java.util.List;

/**
 * A collection of attributes describing a strategy of storing information on the GPU.
 * Generally useful for VBO allocation methods and VAO desgination.
 *
 * This collection stores a set of 'attributes', which are furthered attributed to VBOReferences.
 * This means every 'attribute' defines: its VAO index, its size, its type (GL_TYPE) and a VBOReference to be stored on.
 * A VBOReference describes a logical local allocation of a VBO (VertexBufferObject) that certain attributes can be stored on.
 *
 * Example, we have attribute 1 (position) and attribute 2 (color).
 * One strategy is to have these 'chained' in a VBO.
 * An alternative strategy is to put these in separate VBOs for the sake of buffering efficiency (assume that position is updated often, and color is updated less often).
 *      In this case, we would define VBOReference 0 to contain attribute 1 (position) and VBOReference 1 to contain attribute 2 (color).
 *      Again, these VBOReferences only describe logical local allocations of VBO memory.
 * */


/**
 * A simple representation of VertexAttributes. Does not allow fragmentation @see FragmentedVertexAttributes
 * MUST be created via RawVertexAttributes, See { @Link RawVertexAttributes#bake() }
 * */
public class VertexAttributes {

    private List<VertexAttribute> attributes;
    private int usage;
    private int stride;

    /**
     * MUST be created via RawVertexAttributes.
     * @see RawVertexAttributes#bake() bake.
     * @param attributes - Data from RawVertexAttributes.
     * @param usage - From RawVertexAttributes, see usage.
     * @param stride - From RawVertexAttributes, see stride.
     */
    VertexAttributes(List<VertexAttribute> attributes, int usage, int stride) {
        this.attributes = attributes;
        this.usage = usage;
        this.stride = stride;
    }

    public List<VertexAttribute> getAttributes() { return attributes; }
    public int getUsage() { return usage; }
    public int getStride() { return stride; }

}


