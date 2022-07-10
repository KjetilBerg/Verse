package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.sfx.Source;
import com.kbindiedev.verse.system.ISerializable;

import java.io.InputStream;
import java.io.OutputStream;

public class PlayerComponent implements IComponent, ISerializable {

    public float speed = 128f;
    public float runSpeed = 256f;
    public boolean facingRight = true;

    public boolean wasMoving = false;
    public Source walkSound = null;
    public Source defaultWalkSound = null;
    public boolean collidedWalkAreaLastIteration = false;

    // TODO: empty for now
    @Override
    public void serialize(OutputStream stream) {

    }

    @Override
    public void deserialize(InputStream stream) {

    }

}
