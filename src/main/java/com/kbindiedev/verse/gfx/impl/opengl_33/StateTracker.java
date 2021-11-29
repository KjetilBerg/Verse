package com.kbindiedev.verse.gfx.impl.opengl_33;

import java.util.Stack;

//TODO: use Assertions instead of assert.
public class StateTracker {

    public static VertexArrayObject VAO_NULL = new VertexArrayObject(0);
    public static VBO VBO_NULL = new VBO(0);  //TODO: rework. different assignment method?

    private static VertexArrayObject currentlyBoundVAO = VAO_NULL;
    private static VBO currentlyBoundVBO = VBO_NULL;

    private static Stack<VertexArrayObject> vaoStack = new Stack<>();
    private static Stack<VBO> vboStack = new Stack<>();

    public static VertexArrayObject getCurrentlyBoundVAO() {
        //if (currentlyBoundVAO.getID() == 0) new EngineWarning("WARN: getCurrentlyBoundVAO is NULL").print();
        return currentlyBoundVAO;
    }

    public static VBO getCurrentlyBoundVBO() {
        //if (currentlyBoundVBO.getID() == 0) new EngineWarning("WARN: getCurrentlyBoundVBO is NULL").print();
        return currentlyBoundVBO;
    }

    public static void pushVertexArrayObject(VertexArrayObject vao) {
        vaoStack.push(getCurrentlyBoundVAO());
        setVertexArrayObject(vao);
    }

    public static VertexArrayObject popVertexArrayObject() {
        VertexArrayObject ret = getCurrentlyBoundVAO();
        if (vaoStack.empty()) {
            assert false : "ERR: popVertexArrayObject stack is empty";
        } else {
            setVertexArrayObject(vaoStack.pop());
        }
        return ret;
    }

    public static void pushVertexBufferObject(VBO vbo) {
        vboStack.push(getCurrentlyBoundVBO());
        setVertexBufferObject(vbo);
    }

    public static VBO popVertexBufferObject() {
        VBO ret = getCurrentlyBoundVBO();
        if (vboStack.empty()) {
            assert false : "ERR: popVertexBufferObject stack is empty";
        } else {
            setVertexBufferObject(vboStack.pop());
        }
        return ret;
    }



    public static void bindVertexBufferObject(VBO vbo) { setVertexBufferObject(vbo); }
    public static void unbindVertexBufferObject() { setVertexBufferObject(VBO_NULL); }
    public static void setVertexBufferObject(VBO vbo) {
        if (currentlyBoundVBO.equals(vbo)) return;
        GEOpenGL33.gl33.glBindBuffer(GEOpenGL33.gl33.GL_ARRAY_BUFFER, vbo.getID());
        currentlyBoundVBO = vbo;
    }


    //TODO: if ever, take gl context into account
    public static void bindVertexArrayObject(VertexArrayObject vao) { setVertexArrayObject(vao); }
    public static void unbindVertexArrayObject() { setVertexArrayObject(VAO_NULL); }
    private static void setVertexArrayObject(VertexArrayObject vao) {
        if (currentlyBoundVAO.equals(vao)) return;
        if (currentlyBoundVAO.getID() != 0 && vao.getID() != 0) System.out.println("TODO: remove this. WARN: binding new VAO from '"+currentlyBoundVAO.getID()+"' to '"+vao.getID()+"' without assigning 0 first");
        GEOpenGL33.gl33.glBindVertexArray(vao.getID());
        currentlyBoundVAO = vao;
    }

}
