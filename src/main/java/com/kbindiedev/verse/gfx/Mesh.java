package com.kbindiedev.verse.gfx;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

/** A general impl of a storage of geometry. All GraphicsEngine implementations must also have some Mesh impl, made available through GraphicsEngine.createMesh(). */
public abstract class Mesh {

    protected Material material;
    protected IndexDataBuffer indexDataBuffer; //TODO: default to some blank implementation
    protected RenderMode renderMode; // TODO: lines = linewidth, points = point size

    public Mesh(Material material) {
        this.material = material;
        renderMode = RenderMode.TRIANGLES;
    }

    public Material getMaterial() { return material; }

    public void setRenderMode(RenderMode mode) { renderMode = mode; }
    public RenderMode getRenderMode() { return renderMode; }

    public void setVertices(float[] data) { updateVertices(data, 0); }
    public void updateVertices(float[] data, long vertexOffset) {
        ByteBuffer b = BufferUtils.createByteBuffer(data.length * Float.BYTES);
        for (float f : data) b.putFloat(f);
        b.flip();

        bufferVertices(b, vertexOffset, 0, data.length * Float.BYTES);
    }

    public void setIndices(IndexDataBuffer indexDataBuffer) { this.indexDataBuffer = indexDataBuffer; }

    /* TODO if ever: move to some implementation of IndexDataBuffer
    public void updateIndices(short[] data, long indexOffset) {
        ByteBuffer b = BufferUtils.createByteBuffer(data.length * Short.BYTES);
        for (short s : data) b.putShort(s);
        b.flip();

        bufferIndices(b, indexOffset, 0, data.length * Short.BYTES);
    }

    @Override
    public void bufferIndices(ByteBuffer data, long indexOffset, long byteOffset, long length) {
        if (data.limit() - data.position() > length) {
            Assertions.warn("data size misalignment. limit: %d, position: %d, max length: %d, requested length: %d. setting to max length.",
                    data.limit(), data.position(), data.limit() - data.position(), length);
            length = data.limit() - data.position();
        }

        long finalOffset = indexOffset * 2 + byteOffset; //TODO: is it always 2?
        data.limit(data.position() + (int)length);
        //TODO: set indices here
        if (this.indices.capacity() != data.limit()) {
            new EngineWarning("re-allocating indices buffer. old size: %d, new size: %d", this.indices.capacity(), data.limit()).print();
            ByteBuffer b = BufferUtils.createByteBuffer(data.limit());
            for (int i = 0, len = data.position(); i < len; ++i) b.put(this.indices.get(i));    //copy old index data
            this.indices = b;
        }

        this.indices.position((int)finalOffset);
        this.indices.put(data);
        this.indices.flip();
    }
    */

    //TODO: note: needs to target specific fragment if using FragmentedVAO. make some FragmentedMesh instead ?

    /**
     * Buffer VertexData, generally directly to the GPU.
     * @throws IndexOutOfBoundsException - If length exceeds data capacity by current position, or length + offsets exceed allocated GPU memory.
     * @param data - The data to buffer. Must be a direct buffer. Limit, position and capacity will remain unchanged.
     * @param vertexOffset - An offset according to this mesh's material's VertexAttribute stride; which vertex, by index, to start buffering to.
     * @param byteOffset - An offset in bytes to be added on top of the vertexOffset calculation step.
     * @param length - The number of bytes to buffer.
     */
    public abstract void bufferVertices(ByteBuffer data, long vertexOffset, long byteOffset, long length) throws IndexOutOfBoundsException;

    /** Render the geometry to the currently defined target. */
    public abstract void render();

    public enum RenderMode {
        TRIANGLES, LINES, POINTS
    }

}
