package com.kbindiedev.verse.ecs.components;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/** Represents a Camera object's data. */
public class Camera implements IComponent {

    public Type cameraType              = Type.ORTHOGRAPHIC;
    public Matrix4f projectionMatrix    = new Matrix4f();
    public Matrix4f viewMatrix          = new Matrix4f();
    public Vector3f up                  = new Vector3f(0f, 1f, 0f);
    public float orthographicWidth      = 1f;   // height is determined by aspectRatio
    public float aspectRatio            = 1f;
    public float nearPlane              = -1f;
    public float farPlane               = 1f;
    public float zoom                   = 1f;

    public Transform target             = null;

    // TODO: perspective
    public enum Type {
        ORTHOGRAPHIC
    }

}