package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.state.StateMachine;

public class AnimationController<T extends Animation> extends StateMachine<T, AnimatorContext> {

    public AnimationController(AnimationMap<T> animationMap) { this(animationMap, new AnimatorContext()); }
    public AnimationController(AnimationMap<T> animationMap, AnimatorContext context) {
        super(animationMap, context);
    }

    public T pickAnimation() { return pickState(); }
    public T getLastTransitionedAnimation() { return getLastTransitionedState(); }

}