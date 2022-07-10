package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.animation.AnimationController;
import com.kbindiedev.verse.animation.SpriteAnimation;
import com.kbindiedev.verse.system.ISerializable;

import java.io.InputStream;
import java.io.OutputStream;

/** For use in animating a sprite on a SpriteRenderer */
public class SpriteAnimator implements IComponent, ISerializable {

    // References
    public AnimationController<SpriteAnimation> controller;

    // TODO: empty for now
    @Override
    public void serialize(OutputStream stream) {

    }

    @Override
    public void deserialize(InputStream stream) {

    }

}