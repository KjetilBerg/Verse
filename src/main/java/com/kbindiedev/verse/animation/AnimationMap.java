package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.util.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Defines a set of animations and transitions between them. */
public class AnimationMap {

    // TODO: set default
    // TODO: animations is unused.
    private List<Animation> animations;
    private HashMap<Animation, List<AnimationTransition>> transitions;

    public AnimationMap() {
        animations = new ArrayList<>();
        transitions = new HashMap<>();
    }

    // TODO: temp, return 0
    public Animation getDefault() { return animations.get(0); }

    public void addAnimation(Animation animation) { animations.add(animation); }
    public void addTransition(Animation base, AnimationTransition transition) {
        if (!transitions.containsKey(base)) transitions.put(base, new ArrayList<>());
        transitions.get(base).add(transition);
    }

    /** @return the animation to be played by the current state of the given properties. */
    public Animation pickAnimation(Animation base, Properties properties) {
        if (!transitions.containsKey(base)) return base;

        for (AnimationTransition transition : transitions.get(base)) {
            if (transition.conditionMet(properties)) return transition.getAnimation();
        }

        return base;
    }

}