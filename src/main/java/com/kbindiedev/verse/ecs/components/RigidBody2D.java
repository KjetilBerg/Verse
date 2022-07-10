package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.system.ISerializable;
import org.joml.Vector3f;

import java.io.InputStream;
import java.io.OutputStream;

/** Describes that an entity can collide with things. */
public class RigidBody2D implements IComponent, ISerializable {

    public Vector3f velocity = new Vector3f();

    // TODO: empty for now
    @Override
    public void serialize(OutputStream stream) {

    }

    @Override
    public void deserialize(InputStream stream) {

    }

}