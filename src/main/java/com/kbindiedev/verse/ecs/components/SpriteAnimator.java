package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.animation.AnimationController;
import com.kbindiedev.verse.animation.SpriteAnimation;

/** For use in animating a sprite on a SpriteRenderer */
public class SpriteAnimator implements IComponent {

    // References
    public AnimationController<SpriteAnimation> controller;

}