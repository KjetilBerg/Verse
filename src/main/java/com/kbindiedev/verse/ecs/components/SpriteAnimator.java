package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.animation.SpriteAnimation;
import com.kbindiedev.verse.animation.SpriteAnimationMap;
import com.kbindiedev.verse.util.Properties;

/** For use in animating a sprite on a SpriteRenderer */
public class SpriteAnimator implements IComponent {

    //public SpriteAnimation animation    = new SpriteAnimation();

    // References
    public SpriteAnimationMap map               = null;
    public SpriteAnimation currentAnimation     = null;
    public Properties properties                = new Properties();

}