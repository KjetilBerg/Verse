package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.gfx.Material;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.strategy.IMemoryOccupant;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.EngineWarning;
import com.kbindiedev.verse.profiling.exceptions.NotEnoughMemoryException;

import java.nio.ByteBuffer;

//TODO: assertions vs exceptions
/** A structure that represents geometrical data, and allows rendering said data. */
public class GLMesh extends Mesh implements IMemoryOccupant {

    private VBO.VBOSlice buffer;    //TODO: should VBOSlice keep things in memory, and only buffer right before render step?
    private VertexArrayObject assignedVAO;        //TODO: temp????

    private int baseVertex;

    //TODO rename VAO and VBO to VertexArrayObject and VertexBufferObject respectively

    public GLMesh(Material material, long numVertices) {
        super(material);

        VertexAttributes attributes = material.creator().getShader(GEOpenGL33.class).getAttributes();   //TODO: needs cleanup
        assignedVAO = ResourceManager.mallocVAO(attributes, numVertices);

        try {
            buffer = assignedVAO.malloc(numVertices, this);
            baseVertex = (int)(buffer.getParentOffset() / attributes.getStride()); //TODO: feels dirty. cleanup  //note: this only works because buffer is directly allocated from a VAO memory space
        } catch (NotEnoughMemoryException e) {
            e.printStackTrace();
            Assertions.error("ResourceManager mallocVAO provided a VAO that does not fit the number of vertices requested. requested: %d, available: %d", numVertices, assignedVAO.maxAllocSizeInVertices());
        }

    }


    @Override
    public void bufferVertices(ByteBuffer data, long vertexOffset, long byteOffset, long length) {
        if (length > data.limit() - data.position()) {
            Assertions.warn("data size misalignment. limit: %d, position: %d, max length: %d, requested length: %d. setting to max length.",
                    data.limit(), data.position(), data.limit() - data.position(), length);
            length = data.limit() - data.position();
        }

        long finalOffset = vertexOffset * material.creator().getShader(GEOpenGL33.class).getAttributes().getStride() + byteOffset;
        if (length > buffer.bufferByteSize() - finalOffset) {
            Assertions.warn("data size too big. VBOSlice size: %d, finalOffset: %d, length: %d. setting to: %d",
                    buffer.bufferByteSize(), finalOffset, length, buffer.bufferByteSize() - finalOffset);

            //throw new IndexOutOfBoundsException(String.format("data size too big. VBOSlice size: %d, finalOffset: %d, length: %d. setting to: %d",
            //        buffer.bufferByteSize(), finalOffset, length, buffer.bufferByteSize() - finalOffset));

            length = buffer.bufferByteSize() - finalOffset;
        }

        buffer.buffer(finalOffset, data, length);
    }

    /*
    public void setIndices(short[] indices) {
        if (this.indices.capacity() != indices.length * Short.BYTES) {
            new EngineWarning("re-allocating indices buffer. old size: %d, new size: %d", this.indices.capacity(), indices.length * Short.BYTES).print();
            this.indices = BufferUtils.createByteBuffer(indices.length * Short.BYTES);
        }

        this.indices.position(0);
        for (short index : indices) this.indices.putShort(index);
        this.indices.flip();
    }*/

    /** Render my geometry by my current vertices, indices and material */
    @Override
    public void render() {

        //TODO: temp
        if (indexData == null) {
            new EngineWarning("indexData is null").print();
            return;
        }

        GLShader shader;
        try {
            shader = (GLShader)material.creator().getShader(GEOpenGL33.class);
        } catch (ClassCastException e) {
            e.printStackTrace();
            Assertions.error("shader for material: '%s' is missing for the GEOpenGL33 implementation", material);
            return;
        }

        shader.use();   //TODO: StateTracker .use()

        StateTracker.pushVertexArrayObject(assignedVAO);

        shader.useMaterial(material);

        ByteBuffer indices = indexData.getBuffer();
        indices.position(0);
        indices.limit(indexData.getNumIndices() * 2);   //TODO: is it always 2 ?

        //TODO: 0 and 3 are hardcoded. this is temporary
        //TODO: can use ebos for indices (?)
        //GEOpenGL33.gl33.glDrawRangeElementsBaseVertex(GL33.GL_TRIANGLES, 0, 3, GL33.GL_UNSIGNED_SHORT, indices, baseVertex);
        GEOpenGL33.gl33.glDrawRangeElementsBaseVertex(GL33.GL_TRIANGLES, 0, indices.limit(), GL33.GL_UNSIGNED_SHORT, indices, baseVertex);

        StateTracker.popVertexArrayObject();

        shader.detach();
    }

}
