package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.util.Properties;
import com.kbindiedev.verse.util.condition.Condition;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/** Describes a condition that must be met in order to arrive at a certain Animation. */
public class AnimationTransition<T extends Animation> {

    private T fromAnimation;
    private T toAnimation;

    private float exitTime;             // the exit time
    private boolean exitTimeAsSeconds;  // true = exitTime is treated as "seconds into animation", false = treated as seconds = animationDuration * exitTime
    private WhenExitTime exactExitTime;      // true = transition can only occur during this exitTime's exact moment, false = transition can happen any time after exitTime

    private Condition condition;

    public AnimationTransition(T fromAnimation, T toAnimation, Condition condition) {
        this.fromAnimation = fromAnimation;
        this.toAnimation = toAnimation;
        this.condition = condition;

        exitTime = 0f;
        exitTimeAsSeconds = false;
        exactExitTime = WhenExitTime.AFTER_LOCAL;
    }

    public T getFromAnimation() { return fromAnimation; }
    public T getToAnimation() { return toAnimation; }
    public boolean conditionMet(Properties properties) {
        // TODO: if exactExitTime, need to keep track of last time checked etc.
        if (exactExitTime == WhenExitTime.EXACT) throw new NotImplementedException();

        float secondsPassed = getExitTimeAsSeconds();

        float secondsIntoAnimation = fromAnimation.getSecondsIntoAnimation();
        if (exactExitTime == WhenExitTime.AFTER_GLOBAL) secondsIntoAnimation += fromAnimation.getLoopCount() * fromAnimation.getDuration();

        if (secondsPassed < secondsIntoAnimation) return false;

        return condition.pass(properties);
    }

    private float getExitTimeAsSeconds() {
        if (exitTimeAsSeconds) return exitTime * fromAnimation.getDuration();
        return exitTime;
    }

    // TODO: needs better naming. also refactor
    private enum WhenExitTime {
        EXACT, AFTER_LOCAL, AFTER_GLOBAL
    }

}