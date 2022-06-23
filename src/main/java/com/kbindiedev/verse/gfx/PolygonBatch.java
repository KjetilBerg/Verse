package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.gfx.strategy.providers.IMeshDataProvider;
import com.kbindiedev.verse.profiling.Assertions;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// TODO: considering IMeshDataProvider, could SpriteBatch extend PolygonBatch ?

/** Batches polygons as triangles for rendering. */
public class PolygonBatch {

    private Mesh mesh;
    private ByteBuffer vertexData;
    private IndexDataBuffer indexDataBuffer;

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    private boolean drawing;
    private boolean globalFlipX, globalFlipY; // TODO remove?
    private long drawsTotal, drawsCycle, rendercallsTotal, rendercallsCycle;

    private int maxVertices, maxIndices;
    private short currentVertex;


    public PolygonBatch(GraphicsEngine implementation, int maxVertices, int maxIndices, Mesh.RenderMode renderMode) {
        if (maxVertices > 32767) throw new IllegalArgumentException(String.format("maximum vertex index is 32767. PolygonBatch requested max %d vertices", maxVertices));
        System.out.printf("Creating new PolygonBatch of size: vertices: %d, indices: %d\n", maxVertices, maxIndices); //TODO: Verse needs some notification system

        this.maxVertices = maxVertices;
        this.maxIndices = maxIndices;
        currentVertex = 0;

        Material material = MaterialTemplate.PredefinedTemplates.POS_3D_AND_COLOR.createMaterial(); // TODO: custom material (Shader)

        mesh = implementation.createMesh(material, maxVertices);
        mesh.setRenderMode(renderMode);
        vertexData = BufferUtils.createByteBuffer(Shader.PredefinedAttributes.POS_3D_AND_COLOR.getStride() * maxVertices);    //TODO: material.getVertexAttributes instead, though MUST be identical
        indexDataBuffer = new IndexDataBuffer(maxIndices, true);

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();

        drawing = false;
        globalFlipX = false; globalFlipY = false;
        drawsTotal = drawsCycle = rendercallsTotal = rendercallsCycle = 0;

        mesh.setIndices(indexDataBuffer);
    }

    public Mesh.RenderMode getRenderMode() { return mesh.getRenderMode(); }
    public void setRenderMode(Mesh.RenderMode renderMode) { mesh.setRenderMode(renderMode); }

    public void setProjectionMatrix(Matrix4f projection) { projectionMatrix = projection; }
    public void setViewMatrix(Matrix4f view) { viewMatrix = view; }

    public void begin() {
        if (drawing) Assertions.warn("already drawing, call .end() before .begin() again");
        drawing = true;

        drawsCycle = 0;
        rendercallsCycle = 0;
    }

    public void end() {
        if (!drawing) Assertions.warn("not drawing, call .begin() before .end()");
        if (vertexData.position() != 0) flush();
        drawing = false;
    }

    public void drawConvexPolygon(IMeshDataProvider provider) {

        if (provider.getNumVertices() > maxVertices) Assertions.warn("vertexCount (n=%d) > maxVertices (%d). will cause crash.", provider.getNumVertices(), maxVertices); // TODO: allocate more memory
        if (provider.getNumIndexEntries(mesh.getRenderMode()) > maxIndices) Assertions.warn("polygon indices (%d) > maxIndices (%d). render-mode: %s, will cause crash", provider.getNumIndexEntries(mesh.getRenderMode()), maxIndices, mesh.getRenderMode().name()); // TODO: allocate more memory

        if (!drawing) Assertions.warn("not drawing, call .begin() first");

        boolean willOverflowVertices = (currentVertex + provider.getNumVertices() > maxVertices);
        boolean willOverflowIndices = (indexDataBuffer.getPosition() + provider.getNumIndexEntries(mesh.getRenderMode()) > maxIndices);
        if (willOverflowVertices || willOverflowIndices) flush();

        indexDataBuffer.setBaseVertex(currentVertex);
        provider.feed(vertexData, indexDataBuffer, mesh.getRenderMode());

        currentVertex += provider.getNumVertices();

        drawsCycle++;
        drawsTotal++;
    }

    /** Draw my contents to the screen. */
    public void flush() {
        int vertexIndex = vertexData.position();
        if (vertexIndex == 0) return;

        rendercallsCycle++;
        rendercallsTotal++;

        vertexData.position(0);
        mesh.bufferVertices(vertexData, 0, 0, vertexIndex);

        mesh.getMaterial().setUniformValue("uProjection", projectionMatrix);
        mesh.getMaterial().setUniformValue("uView", viewMatrix);

        indexDataBuffer.setToReadMode();
        mesh.render();
        indexDataBuffer.setToWriteMode();

        vertexData.position(0);
        currentVertex = 0;
    }

}