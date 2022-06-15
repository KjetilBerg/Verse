package com.kbindiedev.verse.animation;

/** Represents some transformation of an object over time. */
public abstract class Animation {

    protected boolean loop;
    protected float secondsIntoAnimation;
    protected float durationSeconds;
    protected int loopCount;

    public Animation() { this(true); }
    public Animation(boolean loop) {
        this.loop = loop;
        secondsIntoAnimation = 0f;
        durationSeconds = 0f;
        loopCount = 0;
    }

    public boolean doesLoop() { return loop; }

    /** @return how many times this animation has looped since it started playing. */
    public int getLoopCount() { return loopCount; }

    /** @return the total amount of time, in seconds, this animation lasts from start to end. */
    public float getDuration() { return durationSeconds; }

    public float getSecondsIntoAnimation() { return secondsIntoAnimation; }
    public void setSecondsIntoAnimation(float secondsIntoAnimation) { this.secondsIntoAnimation = secondsIntoAnimation; } // TODO: wrap around and loopCount

    /** Progress this animation by "dt". */
    public void progress(float dt) { setSecondsIntoAnimation(getSecondsIntoAnimation() + dt); }

}