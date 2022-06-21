package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import com.kbindiedev.verse.gfx.strategy.providers.IVertexProvider;
import com.kbindiedev.verse.profiling.Assertions;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

// TODO: considering IVertexProvider, could SpriteBatch extend PolygonTriangleBatch ?

/** Batches polygons as triangles for rendering. */
public class PolygonTriangleBatch {

    private Mesh mesh;
    private ByteBuffer vertexData;
    private IIndexData indexData;

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;

    private boolean drawing;
    private boolean globalFlipX, globalFlipY; // TODO remove?
    private long drawsTotal, drawsCycle, rendercallsTotal, rendercallsCycle;

    private int maxVertices, maxIndices;
    private short currentVertex;


    public PolygonTriangleBatch(GraphicsEngine implementation, int maxVertices, int maxIndices) {
        if (maxVertices > 32767) throw new IllegalArgumentException(String.format("maximum vertex index is 32767. PolygonTriangleBatch requested max %d vertices", maxVertices));
        System.out.printf("Creating new PolygonTriangleBatch of size: vertices: %d, indices: %d\n", maxVertices, maxIndices); //TODO: Verse needs some notification system

        this.maxVertices = maxVertices;
        this.maxIndices = maxIndices;
        currentVertex = 0;

        Material material = MaterialTemplate.PredefinedTemplates.POS_3D_AND_COLOR.createMaterial(); // TODO: custom material (Shader)

        mesh = implementation.createMesh(material, maxVertices);
        vertexData = BufferUtils.createByteBuffer(Shader.PredefinedAttributes.POS_3D_AND_COLOR.getStride() * maxVertices);    //TODO: material.getVertexAttributes instead, though MUST be identical
        indexData = new SpriteBatch.IndexData(maxIndices);

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();

        drawing = false;
        globalFlipX = false; globalFlipY = false;
        drawsTotal = drawsCycle = rendercallsTotal = rendercallsCycle = 0;
    }

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

    // TODO: polygon with fixed center
    // TODO: generic polygon

    public void drawConvexPolygon(IVertexProvider vertexProvider, int n) {
        if (n < 3) throw new IllegalArgumentException("polygon length must be at least 3, but got: " + n);
        if (n > maxVertices) Assertions.warn("vertexCount (n=%d) > maxVertices (%d). will cause crash.", n, maxVertices); // TODO: allocate more memory
        if ((n - 2) * 3 > maxIndices) Assertions.warn("polygon indices (%d) > maxIndices (%d). will cause crash", (n - 2) * 3, maxIndices); // TODO: allocate more memory

        if (!drawing) Assertions.warn("not drawing, call .begin() first");

        boolean willOverflowVertices = (currentVertex + n > maxVertices);
        boolean willOverflowIndices = (indexData.getBuffer().position() + (n - 2) * 3 > maxIndices);
        if (willOverflowVertices || willOverflowIndices) flush();

        vertexProvider.feed(vertexData, n);

        ByteBuffer indices = indexData.getBuffer();
        for (int i = 2; i < n; ++i) {
            indices.putShort(currentVertex);
            indices.putShort((short)(currentVertex + i - 1));
            indices.putShort((short)(currentVertex + i));
        }

        currentVertex += n;

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

        mesh.setIndices(indexData); //do not need to re-set every flush. TODO: make some getter for mesh's indexData instead

        mesh.getMaterial().setUniformValue("uProjection", projectionMatrix);
        mesh.getMaterial().setUniformValue("uView", viewMatrix);

        // todo: setNumIndices is temp
        indexData.setNumIndices(indexData.getBuffer().position() / 2);
        mesh.render();
        indexData.setNumIndices(indexData.getBuffer().capacity());

        vertexData.position(0);
        indexData.getBuffer().position(0);
        indexData.getBuffer().limit(indexData.getBuffer().capacity()); // TODO: temp
        currentVertex = 0;
    }

    // TODO: allow rotation if attributes contain position etc
    // TODO: 3d rotation ?
    /**
     * Rotate points around its center, adjusting the given array.
     * All elements in the array are replaced with new vectors, and so the originals remain unedited.
     */
    private void rotateAroundCenterZ(float rotation, Vector3f[] points) {
        Vector3f center = new Vector3f();
        for (Vector3f point : points) center.add(point);
        center.div(points.length);
        for (int i = 0; i < points.length; ++i) points[i] = new Vector3f(points[i]).sub(center).rotateZ(rotation).add(center);
    }

}