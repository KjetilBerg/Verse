package com.kbindiedev.verse.ecs.components;

import org.joml.Matrix4f;

/** Represents a Camera object's data. */
public class Camera implements IComponent {

    public Type cameraType              = Type.ORTHOGRAPHIC;
    public Matrix4f projectionMatrix    = new Matrix4f();
    public Matrix4f viewMatrix          = new Matrix4f();
    public float nearPlane              = -1f;
    public float farPlane               = 1f;
    public float viewportWidth          = 1f;
    public float viewportHeight         = 1f;
    public float zoom                   = 1f;

    // TODO: perspective
    public enum Type {
        ORTHOGRAPHIC
    }

}