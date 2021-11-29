package com.kbindiedev.verse.gfx.strategy.attributes;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.*;

import static com.kbindiedev.verse.gfx.impl.opengl_33.GL33.*;
import static com.kbindiedev.verse.gfx.impl.opengl_33.GL33.GL_DOUBLE;

/** A list of Vertex attributes in its raw state. Use .addAttribute to set details, and then .bake to turn it into a usable VertexAttributes object. */
public class RawVertexAttributes {

    private HashMap<Integer, VertexAttribute> attributes;   //attributes by their index.
    private int usage;

    public RawVertexAttributes(int usage) {
        attributes = new HashMap<>();
        this.usage = usage;
    }

    /**
     * Add a new attribute to this collection by provided data.
     * Note: Assertions.errpr if the provided data would create a duplicate (by index).
     * @throws IllegalArgumentException - If the provided index exceeds the maximum number of vertex attributes (ex for opengl: GL_MAX_VERTEX_ATTRIBS)
     * @param index - The attribute index.
     * @param count - The number of 'type' provided (not necessarily bytes).
     * @param type - The type.
     * @param normalized - Normalized.
     */
    public void addAttribute(int index, int count, int type, boolean normalized) {

        //TODO: GL_MAX_VERTEX_ATTRIBS should be queried from gl, using it like this is wrong
        //TODO: remove opengl dependency
        if (index > GL_MAX_VERTEX_ATTRIBS-1) throw new IllegalArgumentException("ERR: VAO addAttribute index cannot exceed GL_MAX_VERTEX_ATTRIBS. Index: '" + index + "', Max: '" + GL_MAX_VERTEX_ATTRIBS + "'");
        if (attributes.containsKey(index)) Assertions.error("index already added. Index: '%d'", index);

        VertexAttribute a = new VertexAttribute(index, count, type, normalized, 0); //baseOffset is unknown, and is calculated in .bake()
        attributes.put(index, a);

    }

    /** Get the currently attributed indices for this collection. */
    public Set<Integer> getCurrentAttributeIndices() { return attributes.keySet(); }

    /**
     * Bake into a VertexAttributes.
     * @throws IllegalStateException if there are 'holes' in the current configuration (missing indices; must start at 0 and follow consecutively).
     * @returns The baked VertexAttributes. If bake again, will create a new VertexAttributes.
     */
    public VertexAttributes bake() throws IllegalStateException {

        //check that there are no holes in attributes
        checkProperBuilt(); //IllegalStateException

        return bakeUnsafe();
    }

    /**
     * Bake, without safety-checks, into a VertexAttributes.
     * @returns The baked VertexAttributes. If bake again, will create a new VertexAttributes.
     */
    VertexAttributes bakeUnsafe() {
        Collection<VertexAttribute> list = attributes.values();

        int offset = 0;
        for (VertexAttribute a : list) {
            a.baseOffset = offset;
            offset += a.getCount() * _size(a.getType());    //TODO: custom types, for now see _size (from old VertexArrayObject)
        }

        int stride = offset;

        return new VertexAttributes(new ArrayList<>(list), usage, stride);
    }

    /**
     * Checks if attributes are built properly. 1. There are no holes between attribute indices, 2. The minimum index is 0.
     * @throws IllegalStateException - If there is a hole or the minimum index is not 0 in attributes.
     * If no exception is thrown, the attributes are properly built.
     */
    private void checkProperBuilt() {
        Integer[] indices;

        //check attribute holes
        indices = attributes.keySet().toArray(new Integer[0]);
        Arrays.sort(indices);
        for (int i = 0; i < indices.length; ++i) { if (indices[i] != i) throw new IllegalStateException("found improper attribute-building. missing index: '"+i+"'"); }
    }



    //TODO: REPLACE (custom types and mapper)
    private static int _size(int type) {
        switch (type) {
            case GL_BYTE:
            case GL_UNSIGNED_BYTE:
                return Byte.BYTES;
            case GL_SHORT:
            case GL_UNSIGNED_SHORT:
                return Short.BYTES;
            case GL_INT:
            case GL_UNSIGNED_INT:
                return Integer.BYTES;
            case GL_FLOAT:
                return Float.BYTES;
            case GL_2_BYTES:
                return 2;
            case GL_3_BYTES:
                return 3;
            case GL_4_BYTES:
                return 4;
            case GL_DOUBLE:
                return Double.BYTES;
            default:
                Assertions.warn("unexpected type: '%d'", type);
                return 1;
        }
    }

}
