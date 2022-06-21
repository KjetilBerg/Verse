package com.kbindiedev.verse.gfx.strategy.providers;

import com.kbindiedev.verse.gfx.Shader;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import com.kbindiedev.verse.profiling.exceptions.NotEnoughVerticesException;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** A IVertexProvider that wraps around when it arrives at the end. */
public class ColoredVertexProvider implements IVertexProvider {

    private VertexAttributes attributes;
    private ByteBuffer wrapper;
    private byte[] vertexData;
    private int dataIndex;

    public ColoredVertexProvider(int numVertices) {
        attributes = Shader.PredefinedAttributes.POS_3D_AND_COLOR;
        vertexData = new byte[numVertices * attributes.getStride()];
        dataIndex = 0;
        wrapper = ByteBuffer.wrap(vertexData);
        wrapper.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public VertexAttributes getVertexAttributes() { return attributes; }

    @Override
    public void feedSingle(ByteBuffer buffer) throws NotEnoughVerticesException {
        buffer.put(vertexData, dataIndex, attributes.getStride());
        dataIndex += attributes.getStride();
        dataIndex %= vertexData.length;
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