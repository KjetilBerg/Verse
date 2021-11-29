package com.kbindiedev.verse.gfx.impl.opengl_33;

import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/** Default OpenGL bindings via lwjgl */
public class GL33Standard implements GL33 {

    public void glBindVertexArray(int array) {
        GL30.glBindVertexArray(array);
    }

    public void glBindBuffer(int target, int buffer) {
        GL15.glBindBuffer(target, buffer);
    }

    public void glDrawElements(int mode, ShortBuffer indices) {
        GL20.glDrawElements(mode, indices);
    }

    public void glDrawRangeElements(int mode, int start, int end, int type, ByteBuffer indices) {
        GL12.glDrawRangeElements(mode, start, end, type, indices);
    }

    public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int type, ByteBuffer indices, int baseVertex) {
        GL32.glDrawRangeElementsBaseVertex(mode, start, end, type, indices, baseVertex);
    }

    public int glGenVertexArrays() {
        return GL30.glGenVertexArrays();
    }

    public int glGenBuffers() {
        return GL15.glGenBuffers();
    }

    public void glEnableVertexAttribArray(int index) {
        GL20.glEnableVertexAttribArray(index);
    }

    public void glDisableVertexAttribArray(int index) {
        GL20.glDisableVertexAttribArray(index);
    }

    public void glVertexAttribPointer(int index, int count, int type, boolean normalized, int stride, long offset) {
        GL20.glVertexAttribPointer(index, count, type, normalized, stride, offset);
    }

    public int glGetError() {
        return GL11.glGetError();
    }

    public void glBufferData(int target, float[] data, int usage) { GL15.glBufferData(target, data, usage); }   //TODO: consider remove

    public void glBufferData(int target, long size, int usage) { GL15.glBufferData(target, size, usage); }

    public void glBufferSubData(int target, long offset, ByteBuffer data) { GL15.glBufferSubData(target, offset, data); }

    public void glBufferSubData(int target, long offset, float[] data) {
        GL15.glBufferSubData(target, offset, data);
    }

    public void glGetIntegerv(int pname, int[] params) { GL11.glGetIntegerv(pname, params); }

}
