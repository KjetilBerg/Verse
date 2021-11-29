package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.gfx.strategy.IMemoryOccupant;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttribute;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.NotEnoughMemoryException;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.kbindiedev.verse.gfx.impl.opengl_33.GL33.*;


/** Vertex Array Object structure for OpenGL */
/** As per local standard, a VAO may only setAttributePointers ONCE. If new buffers need to be bound, create a new VAO (or duplicate) */
public class VertexArrayObject implements IMemoryOccupant {

    private int vaoID;                                      //my id
    private VertexAttributes attributes;                    //attributes describing me
    private VBO.VBOSlice memory;                            //where all my graphics data is stored
    private List<VBO.VBOSlice> allocations;                 //my allocations, see .allocate()

    protected VertexArrayObject(int id) { vaoID = id; } //TODO: change

    public VertexArrayObject(VertexAttributes attributes, long numVertices) { this(attributes, numVertices, null); }
    public VertexArrayObject(VertexAttributes attributes, long numVertices, VBO.VBOSlice memory) {

        if (memory == null)
            memory = ResourceManager.allocateGlBuffer(attributes.getStride() * numVertices, attributes.getUsage(), this);

        if (memory.bufferByteSize() != attributes.getStride() * numVertices)
            throw new IllegalArgumentException(String.format("memory provided does not align with provided attributes. stride: %d, #vertices: %d, bufferByteSize: %d", attributes.getStride(), numVertices, memory.bufferByteSize()));


        vaoID = GEOpenGL33.gl33.glGenVertexArrays();
        this.attributes = attributes;
        this.memory = memory;
        allocations = new ArrayList<>();

        StateTracker.pushVertexArrayObject(this);
        setAttributePointers(memory, attributes);
        enableAllAttributes(attributes);
        StateTracker.popVertexArrayObject();
    }

    /** Sets the glAttribArrayPointer values for this VAO, defined by attributes, about the provided VBO-slice. */
    protected void setAttributePointers(VBO.VBOSlice slice, VertexAttributes attributes) {
        StateTracker.pushVertexArrayObject(this);
        StateTracker.pushVertexBufferObject(slice.getRoot());

        int stride = attributes.getStride();
        long offset = slice.getAbsoluteOffset();
        for (VertexAttribute a : attributes.getAttributes()) {
            System.out.printf("SETTING: index: %d, count: %d, type: %d, normalized: %b, stride: %d, offset: %d\n", a.getIndex(), a.getCount(), a.getType(), a.isNormalized(), stride, a.getBaseOffset() + offset); //TODO: temp
            GEOpenGL33.gl33.glVertexAttribPointer(a.getIndex(), a.getCount(), a.getType(), a.isNormalized(), stride, a.getBaseOffset() + offset);
        }

        StateTracker.popVertexBufferObject();
        StateTracker.popVertexArrayObject();
    }

    protected void enableAllAttributes(VertexAttributes attributes) {
        StateTracker.pushVertexArrayObject(this);
        for (VertexAttribute a : attributes.getAttributes()) {
            GEOpenGL33.gl33.glEnableVertexAttribArray(a.getIndex());
        }
        StateTracker.popVertexArrayObject();
    }

    public VBO.VBOSlice malloc(long numVertices, @Nullable IMemoryOccupant occupant) throws NotEnoughMemoryException {
        if (numVertices > maxAllocSizeInVertices()) throw new NotEnoughMemoryException(String.format("malloc request too large: cannot fit in memory. #verts wanted: %d, max: %d", numVertices, maxAllocSizeInVertices()));

        System.out.printf("VAO malloc: stride: %d, numVertices: %d, stride: %d\n", attributes.getStride(), numVertices, attributes.getStride() * numVertices);
        return memory.slice(attributes.getStride() * numVertices, occupant);
    }

    public long maxAllocSizeInVertices() {
        return (memory.bufferByteSize() / attributes.getStride());
    }

    public int getID() { return vaoID; }

    //TODO: move. unused?
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VertexArrayObject)) return false;
        VertexArrayObject v = (VertexArrayObject)o;
        return v.getID() == getID();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(vaoID);
    }

}

