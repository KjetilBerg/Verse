package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.sfx.Source;

public class PlayerComponent implements IComponent {

    public float speed = 128f;
    public float runSpeed = 256f;
    public boolean facingRight = true;

    public boolean wasMoving = false;
    public Source walkSound = null;
    public Source defaultWalkSound = null;
    public boolean collidedWalkAreaLastIteration = false;

}
