package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.util.Properties;
import com.kbindiedev.verse.util.condition.Condition;

/** Describes a condition that must be met in order to arrive at a certain Animation. */
public class AnimationTransition {

    private Animation animation;
    private Condition condition;

    public AnimationTransition(Animation animation, Condition condition) {
        this.animation = animation;
        this.condition = condition;
    }

    public Animation getAnimation() { return animation; }
    public boolean conditionMet(Properties properties) { return condition.pass(properties); }

}
