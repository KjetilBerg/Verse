package com.kbindiedev.verse.gfx.strategy.providers;

import com.kbindiedev.verse.gfx.IndexDataBuffer;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.ByteBuffer;

/** Feeds vertices and indices to ByteBuffers. */
public abstract class MeshDataProvider {

    protected VertexAttributes attributes;

    public MeshDataProvider(VertexAttributes attributes) {
        this.attributes = attributes;
    }

    /** @return the number of vertices in a single set of this object. */
    public abstract long getNumVertices();

    /**
     * @return the number of entries that will be made by {@link #feed(ByteBuffer, IndexDataBuffer, Mesh.RenderMode)}
     *      into the IndexDataBuffer for a set of this object by the given mode. */
    public abstract long getNumIndexEntries(Mesh.RenderMode mode);

    /** @return the attributes that this provides vertices according to. */
    public VertexAttributes getVertexAttributes() { return attributes; }

    /** Apply one set of my data onto the given mesh (vertices and indices). */
    public void feed(Mesh mesh) {
        throw new NotImplementedException(); // TODO: buffer method onto mesh, also check mesh attributes match mine
    }

    /** Apply one set of my data onto the given parameters (vertices and indices). Mode describes index type. */
    public abstract void feed(ByteBuffer vertexData, IndexDataBuffer indexDataBuffer, Mesh.RenderMode mode);

}