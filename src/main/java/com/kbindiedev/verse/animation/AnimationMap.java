package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.state.StateMap;

/** Defines a set of animations and transitions between them. */
public class AnimationMap<T extends Animation> extends StateMap<T, AnimatorContext> {

    public AnimationMap() {}

    public boolean hasAnimation(T animation) { return hasState(animation); }

    /** @return the animation to be played by the current state of the given properties. */
    public T pickAnimation(T base, AnimatorContext context) { return pickState(base, context); }


}