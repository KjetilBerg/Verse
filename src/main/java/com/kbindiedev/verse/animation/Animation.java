package com.kbindiedev.verse.animation;

/** Represents some transformation of an object over time. */
public abstract class Animation {

    protected boolean loop;
    protected float secondsIntoAnimation;

    public Animation() { this(true); }
    public Animation(boolean loop) {
        this.loop = loop;
        secondsIntoAnimation = 0f;
    }

    public boolean doesLoop() { return loop; }

    public float getSecondsIntoAnimation() { return secondsIntoAnimation; }
    public void setSecondsIntoAnimation(float secondsIntoAnimation) { this.secondsIntoAnimation = secondsIntoAnimation; }

}