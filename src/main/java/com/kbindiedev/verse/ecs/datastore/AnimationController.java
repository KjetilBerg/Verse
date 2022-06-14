package com.kbindiedev.verse.ecs.datastore;

// TODO: datastore or animation package ?

import com.kbindiedev.verse.animation.Animation;
import com.kbindiedev.verse.animation.AnimationMap;
import com.kbindiedev.verse.util.Properties;

/** Manages the state of set of animations, along with some variable state (properties). */
public class AnimationController implements IConstantSize {

    // References
    private AnimationMap animationMap;
    private Animation currentAnimation;
    private Properties properties;

    public AnimationController(AnimationMap animationMap) {
        this.animationMap = animationMap;
        currentAnimation = animationMap.getDefault();
        properties = new Properties();
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

}