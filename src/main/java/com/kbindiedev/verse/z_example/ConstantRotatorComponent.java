package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.components.IComponent;

/** Rotates transforms by a fixed amount. */
public class ConstantRotatorComponent implements IComponent {

    public float amountPerSecond    = 1f;

}