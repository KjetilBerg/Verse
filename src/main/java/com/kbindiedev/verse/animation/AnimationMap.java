package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.system.FastList;
import com.kbindiedev.verse.util.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Defines a set of animations and transitions between them. */
public class AnimationMap<T extends Animation> {

    // TODO: set default
    // TODO: animations is unused.
    private FastList<T> animations;
    private HashMap<T, List<AnimationTransition<T>>> transitions;

    public AnimationMap() {
        animations = new FastList<>();
        transitions = new HashMap<>();
    }

    // TODO: temp, return first
    public T getDefault() { return animations.iterator().next(); }

    public void addAnimation(T animation) { animations.add(animation); }
    public void addTransition(T base, AnimationTransition<T> transition) {
        if (!hasAnimation(transition.getToAnimation())) addAnimation(transition.getToAnimation());

        if (!transitions.containsKey(base)) transitions.put(base, new ArrayList<>());
        transitions.get(base).add(transition);
    }

    public boolean hasAnimation(T animation) { return animations.contains(animation); }

    /** @return the animation to be played by the current state of the given properties. */
    public T pickAnimation(T base, Properties properties) {
        if (base == null) return getDefault(); // TODO: probably temp

        if (!transitions.containsKey(base)) return base;

        for (AnimationTransition<T> transition : transitions.get(base)) {
            if (transition.conditionMet(properties)) return transition.getToAnimation();
        }

        return base;
    }

}