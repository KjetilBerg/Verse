package com.kbindiedev.verse.gfx.strategy.attributes;

import com.kbindiedev.verse.profiling.Assertions;

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
public class FragmentedVertexAttributes {

    private List<VertexAttributes> fragments;    //a consecutive map of VertexAttributes, where list index corresponds to fragment-index.

    /**
     * MUST be created via RawVertexAttributes, See { @Link RawVertexAttributes#bake() }
     */

    /**
     * MUST be created via RawVertexAttributes.
     * @see RawFragmentedVertexAttributes#bake() bake.
     * @param fragments - Data from RawFragmentedVertexAttributes.
     */
    FragmentedVertexAttributes(List<VertexAttributes> fragments) {
        this.fragments = fragments;
    }

    /** Get the number of fragments this collection holds. */
    public int count() { return fragments.size(); }

    /**
     * Get a fragment from this collection, generally to use with editing.
     * If a fragment by the provided index does not exist, will assert warn. Will return null.
     * @param index - The index of the fragment. Said fragment must have been created before with .addFragment
     * @return RawVertexAttributes or null if no fragment has been created for the provided index.
     */
    public VertexAttributes getFragment(int index) {
        if (index < 0 || index >= fragments.size()) {
            Assertions.warn("fragment missing for index: %d. list of stored indices: %s", index, String.join(",", fragments.toString()));
            return null;
        }
        return fragments.get(index);
    }

}
