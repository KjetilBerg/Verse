package com.kbindiedev.verse.ecs.components;

// TODO FAR FUTURE: represent any dimension
// TODO FUTURE: replace Quaternion with Rotor

import com.kbindiedev.verse.system.ISerializable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.InputStream;
import java.io.OutputStream;

/** Represents a 3D position, scale and rotation. */
public class Transform implements IComponent, ISerializable {

    public Vector3f position    = new Vector3f();
    public Vector3f scale       = new Vector3f(1f, 1f, 1f);
    public Quaternionf rotation = new Quaternionf();

    // TODO: empty for now
    @Override
    public void serialize(OutputStream stream) {

    }

    @Override
    public void deserialize(InputStream stream) {

    }

}