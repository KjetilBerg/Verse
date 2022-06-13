package com.kbindiedev.verse.ecs.datastore;

import com.kbindiedev.verse.gfx.Sprite;

/** Describes a frame of an animation. */
public class SpriteFrame {

    private Sprite sprite;
    private float duration;

    // TODO: make "ExtendedSpriteFrame" or something, because this is really just the Transform system (redundant for ECS).
    private float relativeX, relativeY;
    private float relativeScaleX, relativeScaleY;
    private float pivotX, pivotY;

    public SpriteFrame() { this(null, 0.1f); }
    public SpriteFrame(Sprite sprite, float duration) { this(sprite, duration, 0f, 0f); }
    public SpriteFrame(Sprite sprite, float duration, float relativeX, float relativeY) { this(sprite, duration, relativeX, relativeY, 0f, 0f, 0f, 0f); }
    public SpriteFrame(Sprite sprite, float duration, float relativeX, float relativeY, float relativeScalex, float relativeScaleY, float pivotX, float pivotY) {
        this.sprite = sprite;
        this.duration = duration;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeScaleX = relativeScalex;
        this.relativeScaleY = relativeScaleY;
        this.pivotX = pivotX;
        this.pivotY = pivotY;
    }

    public Sprite getSprite() { return sprite; }
    public void setSprite(Sprite sprite) { this.sprite = sprite; }

    public float getDuration() { return duration; }
    public void setDuration(float duration) { this.duration = duration; }

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

}