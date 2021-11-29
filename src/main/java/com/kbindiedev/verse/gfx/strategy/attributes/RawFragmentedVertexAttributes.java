package com.kbindiedev.verse.gfx.strategy.attributes;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.*;
import java.util.stream.Collectors;

//TODO: Fragmented attributes are currently not supported by graphics implementations.
/** A list of Vertex attributes in its raw state. Use .addVBOReference and .addAttribute to set details, and then .bake to turn it into a usable FragmentedVertexAttributes object. */
public class RawFragmentedVertexAttributes {

    private HashMap<Integer, RawVertexAttributes> fragments;        //maps "fragment index" to collection of VertexAttribute

    public RawFragmentedVertexAttributes() {
        fragments = new HashMap<>();
    }

    /**
     * Add a new fragment to this collection.
     * Note: Assertions.error if a fragment already exists for the provided index.
     * @param index - The index for this fragment. Used as unique identifier locally. Every fragment must, on .bake(), exist such that no holes form between indices (and start at 0).
     * @param usage - The buffering / allocation strategy. See example glBuffer*Data. Ex. GL_DYNAMIC_DRAW or GL_STATIC_DRAW. Allocations will take this into account. Static = buffer once, dynamic = any number of times.
     */
    public void addFragment(int index, int usage) {
        if (fragments.containsKey(index)) { Assertions.error("reference already exists. index: '%d'", index); return; }

        RawVertexAttributes fragment = new RawVertexAttributes(usage);
        fragments.put(index, fragment);
    }

    /**
     * Get a fragment from this collection, generally to use with editing.
     * If a fragment by the provided index does not exist, will assert warn. Will return null.
     * @param index - The index of the fragment. Said fragment must have been created before with .addFragment
     * @return RawVertexAttributes or null if no fragment has been created for the provided index.
     */
    public RawVertexAttributes fragment(int index) {
        if (!fragments.containsKey(index)) Assertions.warn("fragment missing for index: %d. list of stored indices: %s", index, String.join(",", fragments.keySet().stream().map(Object::toString).collect(Collectors.toSet())));
        return fragments.getOrDefault(index, null);
    }

    /**
     * Bake this object into a FragmentedVertexAttributes that can be used for various things.
     * @throws IllegalStateException if there are 'holes' in the current configuration (missing indices or fragments; all must start at 0 and follow consecutively).
     * @returns The baked FragmentedVertexAttributes. If bake again, will create a new FragmentedVertexAttributes.
     */
    public FragmentedVertexAttributes bake() throws IllegalStateException {

        //check that there are no holes in attributes and fragments
        checkProperBuilt(); //IllegalStateException

        List<VertexAttributes> baked = new ArrayList<>(fragments.size());
        for (int i = 0, size = fragments.size(); i < size; ++i) {
            baked.add(fragments.get(i).bakeUnsafe());
        }

        return new FragmentedVertexAttributes(baked);
    }

    /**
     * Checks that no duplicate attribute-indices exist.
     * Checks that attributes are built properly. 1. There are no holes between attribute indices, 2. The minimum index is 0.
     * Checks that fragments are built properly. 1. There are no holes between fragment indices, 2. The minimum index is 0.
     * @throws IllegalStateException - If there is a hole or the minimum index is not 0 in either attributes or fragments indices, or there exist multiple attributes with the same index.
     * If no exception is thrown, the attributes are properly built.
     */
    private void checkProperBuilt() {

        //check fragments holes
        Integer[] indices = fragments.keySet().toArray(new Integer[0]);
        Arrays.sort(indices);
        for (int i = 0; i < indices.length; ++i) { if (indices[i] != i) throw new IllegalStateException("found improper fragment-indexing. missing index: '"+i+"'"); }

        //check attributes duplicates
        HashSet<Integer> allAttributesIndices = new HashSet<>();
        for (RawVertexAttributes a : fragments.values()) {
            Set<Integer> list = a.getCurrentAttributeIndices();
            for (Integer i : list) {
                if (allAttributesIndices.contains(i)) throw new IllegalStateException("found duplicate attribute-index across different fragments. duplicate index: '"+i+"'");
                allAttributesIndices.add(i);
            }
        }

        //check attribute holes
        indices = allAttributesIndices.toArray(new Integer[0]);
        Arrays.sort(indices);
        for (int i = 0; i < indices.length; ++i) { if (indices[i] != i) throw new IllegalStateException("found improper attribute-building. missing index: '"+i+"'"); }

    }

}
