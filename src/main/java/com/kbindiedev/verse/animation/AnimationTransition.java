package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.state.StateTransition;
import com.kbindiedev.verse.util.condition.Condition;

/** Describes a condition that must be met in order to arrive at a certain Animation. */
public class AnimationTransition<T extends Animation> extends StateTransition<T, AnimatorContext> {

    private float exitTime;             // the exit time
    private boolean exitTimeAsSeconds;  // true = exitTime is treated as "seconds into animation", false = treated as seconds = animationDuration * exitTime
    private ExitTimeStrategy strategy;

    public AnimationTransition(T animationFrom, T animationTo, Condition condition) {
        this(animationFrom, animationTo, condition, 0f, false, ExitTimeStrategy.AFTER_GLOBAL);
    }

    public AnimationTransition(T animationFrom, T animationTo, Condition condition, float exitTime, boolean exitTimeAsSeconds, ExitTimeStrategy strategy) {
        super(animationFrom, animationTo, condition);

        this.exitTime = exitTime;
        this.exitTimeAsSeconds = exitTimeAsSeconds;
        this.strategy = strategy;
    }

    public T getAnimationFrom() { return getStateFrom(); }
    public T getAnimationTo() { return getStateTo(); }

    @Override
    public boolean canTransition(AnimatorContext context) {
        if (!canTransitionByExitTime(context.getDeltaTime())) return false;
        return super.canTransition(context);
    }

    @Override
    public T makeTransition(AnimatorContext context, boolean clean) {
        float leftoverTime = getLeftoverTime(context.getDeltaTime());
        context.setDeltaTime(leftoverTime);

        T animation = super.makeTransition(context, clean);
        animation.setSecondsIntoAnimation(0);
        animation.setLoopCount(0);

        return animation;
    }

    private boolean canTransitionByExitTime(float dt) {
        return dt - getTimeUntilReachExitTime() >= 0;
    }

    /** @return the left over time, in seconds, from the time that would be consumed before I could properly transition. */
    public float getLeftoverTime(float dt) {
        return dt - getTimeUntilReachExitTime();
    }

    /** @return the time in seconds until exitTime would be satisfied, by the stored fromAnimation. always >= 0. */
    public float getTimeUntilReachExitTime() {

        Animation animationFrom = getAnimationFrom();

        float exitTime = getExitTimeAsSeconds();
        float secondsIntoAnimation = animationFrom.getSecondsIntoAnimation();

        float time;
        switch (strategy) {
            case EXACT:
                time = exitTime - secondsIntoAnimation;
                while (time < 0) time += animationFrom.getDuration();
                return time;
            case AFTER_LOCAL:
                time = exitTime - secondsIntoAnimation;
                return Math.max(time, 0);
            case AFTER_GLOBAL:
                time = exitTime - (secondsIntoAnimation + animationFrom.getLoopCount() * animationFrom.getDuration());
                return Math.max(time, 0);
            default:
                Assertions.warn("unknown strategy: %s", strategy.name());
        }

        return 0f;

    }

    private float getExitTimeAsSeconds() {
        if (exitTimeAsSeconds) return exitTime;
        return exitTime * getAnimationFrom().getDuration();
    }

    public enum ExitTimeStrategy {
        EXACT, AFTER_LOCAL, AFTER_GLOBAL
    }

}