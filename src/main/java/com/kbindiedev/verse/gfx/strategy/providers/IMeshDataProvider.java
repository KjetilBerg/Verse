package com.kbindiedev.verse.gfx.strategy.providers;

import com.kbindiedev.verse.gfx.IndexDataBuffer;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.ByteBuffer;

/** Feeds vertices and indices to ByteBuffers. */
public interface IMeshDataProvider {

    /** @return the number of vertices in a single set of this object. */
    int getNumVertices();

    /**
     * @return the number of entries that will be made by {@link #feed(ByteBuffer, IndexDataBuffer, Mesh.RenderMode)}
     *      into the IndexDataBuffer for a set of this object by the given mode. */
    int getNumIndexEntries(Mesh.RenderMode mode);

    /** @return the attributes that this provides vertices according to. */
    VertexAttributes getVertexAttributes();

    /** Apply one set of my data onto the given mesh (vertices and indices). */
    default void feed(Mesh mesh) {
        throw new NotImplementedException(); // TODO: buffer method onto mesh, also check mesh attributes match mine
    }

    /** Apply one set of my data onto the given parameters (vertices and indices). Mode describes index type. */
    void feed(ByteBuffer vertexData, IndexDataBuffer indexDataBuffer, Mesh.RenderMode mode);

}