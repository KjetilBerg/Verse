package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.components.IComponent;

public class PlayerComponent implements IComponent {

    public float speed = 128f;
    public float runSpeed = 256f;
    public boolean facingRight = true;

}