package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.gfx.Sprite;

/** Describes a frame of an animation. Every SpriteFrame belongs to exactly one SpriteAnimation. */
public class SpriteFrame implements Comparable<SpriteFrame> {

    private SpriteAnimation creator;
    private Sprite sprite;
    private float duration;
    private float startTimeIntoAnimation;

    private float relativeX, relativeY;
    private float relativeScaleX, relativeScaleY;
    private float pivotX, pivotY;

    protected SpriteFrame(SpriteAnimation creator, Sprite sprite, float duration, float startTimeIntoAnimation) {
        this.creator = creator;
        this.sprite = sprite;
        this.duration = duration;
        this.startTimeIntoAnimation = startTimeIntoAnimation;

        relativeX = 0; relativeY = 0;
        relativeScaleX = 1f; relativeScaleY = 1f;
        pivotX = 0.5f; pivotY = 0.5f;
    }

    public SpriteAnimation getCreator() { return creator; }
    public Sprite getSprite() { return sprite; }
    public float getDuration() { return duration; }

    public float getRelativeX() { return relativeX; }
    public void setRelativeX(float relativeX) { this.relativeX = relativeX; }

    public float getRelativeY() { return relativeY; }
    public void setRelativeY(float relativeY) { this.relativeY = relativeY; }

    public float getRelativeScaleX() { return relativeScaleX; }
    public void setRelativeScaleX(float relativeScaleX) { this.relativeScaleX = relativeScaleX; }

    public float getRelativeScaleY() { return relativeScaleY; }
    public void setRelativeScaleY(float relativeScaleY) { this.relativeScaleY = relativeScaleY; }

    public float getPivotX() { return pivotX; }
    public void setPivotX(float pivotX) { this.pivotX = pivotX; }

    public float getPivotY() { return pivotY; }
    public void setPivotY(float pivotY) { this.pivotY = pivotY; }

    /** Note: this class has a natural ordering that is inconsistent with equals. */
    @Override
    public int compareTo(SpriteFrame o) {
        float diff = o.startTimeIntoAnimation - startTimeIntoAnimation;
        if (diff < 0) return -1;
        if (diff > 0) return 1;
        return 0;
    }
}