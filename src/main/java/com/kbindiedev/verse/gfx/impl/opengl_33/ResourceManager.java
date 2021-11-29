package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.gfx.strategy.IMemoryOccupant;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.util.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//TODO: this class feels messy. needs cleanup.
public class ResourceManager {

    private static final long DEFAULT_ALLOC_SIZE = Unit.fromMiB(10);    //10 MiB
    private static final long DEFAULT_NUM_VERTICES = 10 * 1024;         //10240 vertices per VAO (TODO: lazy, since vertexSize can vary a lot)

    private static HashMap<Integer, ArrayList<VBO>> buffers = new HashMap<>();     //maps usage to array of buffers. last is always "current"
    private static HashMap<VertexAttributes, ArrayList<VertexArrayObject>> vertexArrays = new HashMap<>();


    //TODO: change name, something "getVAOThatFitsXNumberOfVerticesGivenAttributesY"
    static VertexArrayObject mallocVAO(VertexAttributes attributes, long numVertices) {
        ensureExistsVertexArrays(attributes);

        List<VertexArrayObject> vaos = vertexArrays.get(attributes);
        VertexArrayObject vao = (vaos.size() != 0 ? vaos.get(vaos.size()-1) : null);

        if (vao == null || vao.maxAllocSizeInVertices() < numVertices) {
            //TODO: Engine.notify or something
            System.out.println("Creating new VertexArrayObject...");
            makeNewVAO(attributes, Math.max(numVertices, DEFAULT_NUM_VERTICES));
            vao = vaos.get(vaos.size()-1);
            if (vao.maxAllocSizeInVertices() < numVertices) Assertions.error("STATEERR: makeNewVAO did not provide said VAO to mallocVAO");
        }

        return vao;
    }

    /**
     * Allocate a set number of bytes of a VertexBufferObject.
     * If dynamic is true, the allocated memory may be buffered as many times as you want.
     * If dynamic is false, the allocated memory can only be buffered once.
     * Note: will assert if usage is invalid in relation to glBufferData.
     * @param sizeInBytes - The number of bytes to allocate.
     * @param usage - The defined usage per glBufferData.
     * @param occupant - The resource that will be occupying this GlBuffer.
     */
    public static VBO.VBOSlice allocateGlBuffer(long sizeInBytes, int usage, IMemoryOccupant occupant) {
        if (!isValidUsage(usage)) throw new IllegalArgumentException("ERR: ResourceManager allocateGlBuffer unknown usage: '"+usage+"'");

        //TODO: change
        if (sizeInBytes > DEFAULT_ALLOC_SIZE) throw new IllegalArgumentException("sizeInBytes > DEFAULT_ALLOC_SIZE (10MiB). sizeInBytes: "+sizeInBytes+" ("+Unit.toMiB(sizeInBytes)+" MiB)");

        if (!buffers.containsKey(usage) || buffers.get(usage).size() == 0) makeNewGlBuffer(DEFAULT_ALLOC_SIZE, usage);
        ArrayList<VBO> list = buffers.get(usage);
        VBO vbo = list.get(list.size() - 1);    //no better way. consider using stack?

        if (vbo.maxSliceSizeBytes() < sizeInBytes) {
            System.out.printf("NOTICE: ResourceManager allocateGlBuffer: buffer for usage: '%d' has too little remaining memory: %d (%d KiB), wanted: %d (%d KiB). Allocating new...\n", usage, vbo.maxSliceSizeBytes(), Unit.toKiB(vbo.maxSliceSizeBytes()), sizeInBytes, Unit.toKiB(sizeInBytes));
            makeNewGlBuffer(DEFAULT_ALLOC_SIZE, usage);
            vbo = list.get(list.size() - 1);
        }

        return vbo.slice(sizeInBytes, occupant);
    }

    private static void makeNewGlBuffer(long sizeInBytes, int usage) {
        ensureExistsBuffers(usage);
        VBO newVBO = new VBO(sizeInBytes, usage);
        buffers.get(usage).add(newVBO);
    }

    private static void makeNewVAO(VertexAttributes attributes, long numVertices) {
        ensureExistsVertexArrays(attributes);
        VertexArrayObject newVAO = new VertexArrayObject(attributes, numVertices);
        vertexArrays.get(attributes).add(newVAO);
    }

    //TODO: rework?
    private static void ensureExistsBuffers(int usage) {
        if (!buffers.containsKey(usage)) buffers.put(usage, new ArrayList<>()); //putIfAbsent would always create ArrayList
    }

    private static void ensureExistsVertexArrays(VertexAttributes attributes) {
        if (!vertexArrays.containsKey(attributes)) vertexArrays.put(attributes, new ArrayList<>());
    }

    /** Check whether the provided usage is valid according to glBufferData */
    private static boolean isValidUsage(int usage) {
        GL33 gl = GEOpenGL33.gl33;
        return (usage == gl.GL_STREAM_DRAW || usage == gl.GL_STREAM_READ || usage == gl.GL_STREAM_COPY ||
                usage == gl.GL_STATIC_DRAW || usage == gl.GL_STATIC_READ || usage == gl.GL_STATIC_COPY ||
                usage == gl.GL_DYNAMIC_DRAW || usage == gl.GL_DYNAMIC_READ || usage == gl.GL_DYNAMIC_COPY);
    }


    /*
    //TODO: looking for something else, but need a similar structure to this for logging statistics.
    public static void printStatistics() {
        String slicesString = "";
        for (ArrayList<VBO> vbos : buffers.values()) {
            for (VBO vbo : vbos) {
                List<VBO.VBOSlice> slices = vbo.getAllocations();
                for (VBO.VBOSlice slice : slices) slicesString += countSlices(slice, "");
            }
        }
        System.out.println("Total number of slices: ");
        System.out.println(slicesString);
    }

    private static String countSlices(VBO.VBOSlice slice, String tabs) {
        StringBuilder sb = new StringBuilder();
        sb.append(tabs);
        sb.append(slice.toString());
        sb.append("\t\t");
        sb.append(slice.getOccupant().toString());
        sb.append("\n");

        for (VBO.VBOSlice c : slice.getChildren()) sb.append(countSlices(c, tabs + "\t"));

        return sb.toString();
    }
    */

}
