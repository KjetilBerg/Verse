package com.kbindiedev.verse.ecs.components;

import org.joml.Vector3f;

/** Describes that an entity can collide with things. */
public class RigidBody2D implements IComponent {

    public Vector3f velocity = new Vector3f();

}