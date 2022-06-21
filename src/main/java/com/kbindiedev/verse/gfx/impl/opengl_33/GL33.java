package com.kbindiedev.verse.gfx.impl.opengl_33;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

//TODO: the general structure of this implementations need to be refactored so it all depends on this instead of LWJGL's in-built bindings
/** Bindings for OpenGL 3.3 calls. All calls that exist in versions lower than 3.3 also exist in here. */
public interface GL33 {

    //https://javagl.github.io/GLConstantsTranslator/GLConstantsTranslator.html

    int GL_NO_ERROR = 0x0;

    int GL_POINTS = 0x0;
    int GL_LINES = 0x1;
    int GL_TRIANGLES = 0x4;

    int GL_BYTE = 0x1400;
    int GL_UNSIGNED_BYTE = 0x1401;
    int GL_SHORT = 0x1402;
    int GL_UNSIGNED_SHORT = 0x1403;
    int GL_INT = 0x1404;
    int GL_UNSIGNED_INT = 0x1405;
    int GL_FLOAT = 0x1406;
    int GL_2_BYTES = 0x1407;
    int GL_3_BYTES = 0x1408;
    int GL_4_BYTES = 0x1409;
    int GL_DOUBLE = 0x140a;
    int GL_HALF_FLOAT = 0x140b;
    int GL_FIXED = 0x140c;
    int GL_UNSIGNED_INT64_ARB = 0x140f;

    int GL_MAX_VERTEX_ATTRIBS = 0x8869;



    int GL_ARRAY_BUFFER = 0x8892;

    int GL_STREAM_DRAW = 0x88e0;
    int GL_STREAM_READ = 0x88e1;
    int GL_STREAM_COPY = 0x88e2;
    int GL_STATIC_DRAW = 0x88e4;
    int GL_STATIC_READ = 0x88e5;
    int GL_STATIC_COPY = 0x88e6;
    int GL_DYNAMIC_DRAW = 0x88e8;
    int GL_DYNAMIC_READ = 0x88e9;
    int GL_DYNAMIC_COPY = 0x88ea;


    void glBindVertexArray(int array);

    void glBindBuffer(int target, int buffer);

    void glDrawElements(int mode, ShortBuffer indices);

    //TODO: consider checking GL12C#GL_MAX_ELEMENTS_VERTICES MAX_ELEMENTS_VERTICES}. was 'worse performance' if exceed; not 'break'
    //TODO: consider adding support for ShortBuffers too, since they are most commonly used
    void glDrawRangeElements(int mode, int start, int end, int type, ByteBuffer indices);

    void glDrawRangeElementsBaseVertex(int mode, int start, int end, int type, ByteBuffer indices, int baseVertex);

    int glGenVertexArrays();

    int glGenBuffers();

    void glEnableVertexAttribArray(int index);

    void glDisableVertexAttribArray(int index);

    void glVertexAttribPointer(int index, int count, int type, boolean normalized, int stride, long offset);

    int glGetError();

    void glBufferData(int target, float[] data, int usage); //TODO: consider remove (or replace float[] with ByteBuffer, or both)

    void glBufferData(int target, long size, int usage);

    void glBufferSubData(int target, long offset, ByteBuffer data);

    void glBufferSubData(int target, long offset, float[] data);

    void glGetIntegerv(int pname, int[] params);

}
