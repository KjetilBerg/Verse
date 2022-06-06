package com.kbindiedev.verse.ecs.components;

// TODO FAR FUTURE: represent any dimension

import com.kbindiedev.verse.ecs.util.Rotor;
import org.joml.Vector3f;

/** Represents a 3D position, scale and rotation. */
public class Transform implements IComponent {

    public Vector3f position    = new Vector3f();
    public Vector3f scale       = new Vector3f(1f, 1f, 1f);
    public Rotor rotation       = new Rotor();

}