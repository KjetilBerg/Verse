package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.ecs.datastore.SpriteAnimation;

/** For use in animating a sprite on a SpriteRenderer */
public class SpriteAnimator implements IComponent {

    public SpriteAnimation animation    = new SpriteAnimation();

}