package com.kbindiedev.verse.gfx.strategy.providers;

import com.kbindiedev.verse.gfx.IndexDataBuffer;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.Shader;
import com.kbindiedev.verse.gfx.strategy.index.IndexEntriesGenerator;
import com.kbindiedev.verse.gfx.strategy.index.LineMode;
import com.kbindiedev.verse.gfx.strategy.index.PointMode;
import com.kbindiedev.verse.gfx.strategy.index.TriangleMode;
import com.kbindiedev.verse.profiling.Assertions;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** A MeshDataProvider that provides POS_3D_AND_COLOR vertices, that are connected as triangles. */
public class ColoredVertexProvider extends MeshDataProvider {

    private int numVertices;
    private byte[] byteVertexData;
    private ByteBuffer wrapper;

    private short[] indexEntriesTriangles;
    private short[] indexEntriesLines;
    private short[] indexEntriesPoints;

    public ColoredVertexProvider(int numVertices) { this(numVertices, TriangleMode.ZERO_IS_CENTER, LineMode.ZERO_IS_CENTER_OUTLINE_ONLY, PointMode.DISREGARD_FIRST); }
    public ColoredVertexProvider(int numVertices, TriangleMode triangleMode, LineMode lineMode, PointMode pointMode) {
        super(Shader.PredefinedAttributes.POS_3D_AND_COLOR);
        this.numVertices = numVertices;

        byteVertexData = new byte[numVertices * attributes.getStride()];
        wrapper = ByteBuffer.wrap(byteVertexData);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);

        indexEntriesTriangles = IndexEntriesGenerator.generateIndicesTriangles(numVertices, triangleMode);
        indexEntriesLines = IndexEntriesGenerator.generateIndicesLines(numVertices, lineMode);
        indexEntriesPoints = IndexEntriesGenerator.generateIndicesPoints(numVertices, pointMode);
    }

    @Override
    public long getNumVertices() { return numVertices; }
    @Override
    public long getNumIndexEntries(Mesh.RenderMode mode) { return getIndexEntries(mode).length; }

    public short[] getIndexEntries(Mesh.RenderMode mode) {
        switch (mode) {
            case TRIANGLES: return indexEntriesTriangles;
            case LINES: return indexEntriesLines;
            case POINTS: return indexEntriesPoints;
            default: Assertions.warn("unknown RenderMode: %s, assuming triangles", mode.name()); return indexEntriesTriangles;
        }
    }

    @Override
    public void feed(ByteBuffer vertexData, IndexDataBuffer indexDataBuffer, Mesh.RenderMode mode) {
        vertexData.put(byteVertexData);

        short[] indexEntries = getIndexEntries(mode);
        for (short s : indexEntries) indexDataBuffer.addLocalIndexEntry(s);
    }

    public void setPosition(int vertex, float x, float y, float z) {
        int offset = vertex * attributes.getStride();
        wrapper.putFloat(offset, x);
        wrapper.putFloat(offset + Float.BYTES, y);
        wrapper.putFloat(offset + Float.BYTES * 2, z);
    }

    public void setColor(int vertex, int colorData) {
        int offset = vertex * attributes.getStride();
        wrapper.putInt(offset + Float.BYTES * 3, colorData);
    }

}
