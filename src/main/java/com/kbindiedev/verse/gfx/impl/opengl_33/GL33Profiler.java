package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.util.SizedHistoryStack;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GL33Profiler implements GL33 {

    private enum GL_OPERATION {
        NULL, GL_BIND_VERTEX_ARRAY, GL_BIND_BUFFER, GL_DRAW_ELEMENTS, GL_DRAW_RANGE_ELEMENTS, GL_DRAW_RANGE_ELEMENTS_BASE_VERTEX, GL_GEN_VERTEX_ARRAYS, GL_GEN_BUFFERS,
        GL_ENABLE_VERTEX_ATTRIB_ARRAY, GL_DISABLE_VERTEX_ATTRIB_ARRAY, GL_VERTEX_ATTRIB_POINTER, GL_GET_ERROR, GL_BUFFER_DATA, GL_BUFFER_SUB_DATA, GL_GET_INTEGER_V
    }

    private GL33 context;
    private GL_OPERATION currentlyPerforming = GL_OPERATION.NULL;
    private GL_OPERATION lastOperation = GL_OPERATION.NULL;
    private HashMap<GL_OPERATION, Integer> numCalls = new HashMap<>();
    private SizedHistoryStack<GL_OPERATION> history = new SizedHistoryStack<>(100);

    public GL33Profiler(GL33 context) {
        this.context = context;
    }

    public void logHistory() {
        System.out.println("--- GL33 Profiler History Log ---");

        //current
        System.out.printf("Currently performing: %s\n", currentlyPerforming.name());

        //numCalls
        int totalCalls = 0;
        int longestString = 0;
        for (Map.Entry<GL_OPERATION, Integer> calls : numCalls.entrySet()) { int len = calls.getKey().name().length(); if (len > longestString) longestString = len; totalCalls += calls.getValue(); }  //TODO: remove?
        System.out.printf("Number of calls per operation: Total calls: %d\n", totalCalls);
        System.out.printf("TEMP longest string: %d\n", longestString);  //TODO: temp (remove)
        for (Map.Entry<GL_OPERATION, Integer> calls : numCalls.entrySet()) System.out.printf("\t%30s : %d\n", calls.getKey().name(), calls.getValue());   //TODO: 20 characters enough?

        //history
        Collection<GL_OPERATION> operationHistory = history.history();
        System.out.printf("Operation stack / history: Size: %d (max 100)\n", operationHistory.size());
        for (GL_OPERATION operation : operationHistory) System.out.printf("\t%s\n", operation.name());

        System.out.println("--- GL33 Profiler History Log Complete ---");
    }

    private void checkError() {
        int error = context.glGetError();
        while (error != GL33.GL_NO_ERROR) {
            System.out.printf("OpenGL encountered an error. code: %d. last operation: %s\n", error, lastOperation.name());
            new Exception().printStackTrace();
            logHistory();
            error = context.glGetError();
        }
        //int error = gl20.glGetError();
        //while (error != GL20.GL_NO_ERROR) {
        //    glProfiler.getListener().onError(error);
        //    error = gl20.glGetError();
        //}
    }

    private void preCall(GL_OPERATION operation) {
        currentlyPerforming = operation;
    }
    private void postCall(GL_OPERATION operation) {
        lastOperation = currentlyPerforming;
        currentlyPerforming = GL_OPERATION.NULL;

        if (!numCalls.containsKey(operation)) numCalls.put(operation, 0);
        numCalls.replace(operation, numCalls.get(operation) + 1);
        history.push(operation);

        checkError();
    }

    //--- The bindings ---

    public void glBindVertexArray(int array) {
        preCall(GL_OPERATION.GL_BIND_VERTEX_ARRAY);
        context.glBindVertexArray(array);
        postCall(GL_OPERATION.GL_BIND_VERTEX_ARRAY);
    }

    public void glBindBuffer(int target, int buffer) {
        preCall(GL_OPERATION.GL_BIND_BUFFER);
        context.glBindBuffer(target, buffer);
        postCall(GL_OPERATION.GL_BIND_BUFFER);
    }

    public void glDrawElements(int mode, ShortBuffer indices) {
        preCall(GL_OPERATION.GL_DRAW_ELEMENTS);
        context.glDrawElements(mode, indices);
        postCall(GL_OPERATION.GL_DRAW_ELEMENTS);
    }

    public void glDrawRangeElements(int mode, int start, int end, int type, ByteBuffer indices) {
        preCall(GL_OPERATION.GL_DRAW_RANGE_ELEMENTS);
        context.glDrawRangeElements(mode, start, end, type, indices);
        postCall(GL_OPERATION.GL_DRAW_RANGE_ELEMENTS);
    }

    public void glDrawRangeElementsBaseVertex(int mode, int start, int end, int type, ByteBuffer indices, int baseVertex) {
        preCall(GL_OPERATION.GL_DRAW_RANGE_ELEMENTS_BASE_VERTEX);
        context.glDrawRangeElementsBaseVertex(mode, start, end, type, indices, baseVertex);
        postCall(GL_OPERATION.GL_DRAW_RANGE_ELEMENTS_BASE_VERTEX);
    }

    public int glGenVertexArrays() {
        preCall(GL_OPERATION.GL_GEN_VERTEX_ARRAYS);
        int result = context.glGenVertexArrays();
        postCall(GL_OPERATION.GL_GEN_VERTEX_ARRAYS);
        return result;
    }

    public int glGenBuffers() {
        preCall(GL_OPERATION.GL_GEN_BUFFERS);
        int result = context.glGenBuffers();
        postCall(GL_OPERATION.GL_GEN_BUFFERS);
        return result;
    }

    public void glEnableVertexAttribArray(int index) {
        preCall(GL_OPERATION.GL_ENABLE_VERTEX_ATTRIB_ARRAY);
        context.glEnableVertexAttribArray(index);
        postCall(GL_OPERATION.GL_ENABLE_VERTEX_ATTRIB_ARRAY);
    }

    public void glDisableVertexAttribArray(int index) {
        preCall(GL_OPERATION.GL_DISABLE_VERTEX_ATTRIB_ARRAY);
        context.glDisableVertexAttribArray(index);
        postCall(GL_OPERATION.GL_DISABLE_VERTEX_ATTRIB_ARRAY);
    }

    public void glVertexAttribPointer(int index, int count, int type, boolean normalized, int stride, long offset) {
        preCall(GL_OPERATION.GL_VERTEX_ATTRIB_POINTER);
        context.glVertexAttribPointer(index, count, type, normalized, stride, offset);
        postCall(GL_OPERATION.GL_VERTEX_ATTRIB_POINTER);
    }

    //Note: internal usage of getError does not go by profiling
    public int glGetError() {
        preCall(GL_OPERATION.GL_GET_ERROR);
        int result = context.glGetError();
        postCall(GL_OPERATION.GL_GET_ERROR);
        return result;
    }


    public void glBufferData(int target, float[] data, int usage) { //TODO: consider remove
        preCall(GL_OPERATION.GL_BUFFER_DATA);
        context.glBufferData(target, data, usage);
        postCall(GL_OPERATION.GL_BUFFER_DATA);
    }

    public void glBufferData(int target, long size, int usage) {
        preCall(GL_OPERATION.GL_BUFFER_DATA);
        context.glBufferData(target, size, usage);
        postCall(GL_OPERATION.GL_BUFFER_DATA);
    }

    public void glBufferSubData(int target, long offset, ByteBuffer data) { //TODO: calls, differentiate
        preCall(GL_OPERATION.GL_BUFFER_SUB_DATA);
        context.glBufferSubData(target, offset, data);
        postCall(GL_OPERATION.GL_BUFFER_SUB_DATA);
    }

    public void glBufferSubData(int target, long offset, float[] data) {
        preCall(GL_OPERATION.GL_BUFFER_SUB_DATA);
        context.glBufferSubData(target, offset, data);
        postCall(GL_OPERATION.GL_BUFFER_SUB_DATA);
    }

    public void glGetIntegerv(int pname, int[] params) {
        preCall(GL_OPERATION.GL_GET_INTEGER_V);
        context.glGetIntegerv(pname, params);
        postCall(GL_OPERATION.GL_GET_INTEGER_V);
    }

}
