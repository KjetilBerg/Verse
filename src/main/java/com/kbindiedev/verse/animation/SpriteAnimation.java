package com.kbindiedev.verse.animation;

import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.system.SortedFastList;

import java.util.ArrayList;
import java.util.List;

/** Describes a collection of SpriteFrames. */
public class SpriteAnimation extends Animation {

    private SortedFastList<SpriteFrame> frames;
    private int currentFrameIndex;
    private float currentFrameSeconds;

    public SpriteAnimation() { this(true); }
    public SpriteAnimation(boolean loop) {
        super(loop);
        frames = new SortedFastList<>(SpriteFrame::compareTo); // TODO: blank default frame
        currentFrameIndex = 0;
        currentFrameSeconds = 0f;
    }

    /**
     * Create a frame for this animation. The generated frame is added to this animation.
     * @return the created frame in case the user wants to modify its details.
     */
    public SpriteFrame createFrame(Sprite sprite, float duration) {
        SpriteFrame frame = new SpriteFrame(this, sprite, duration, durationSeconds);
        durationSeconds += duration;
        frames.add(frame);
        return frame;
    }

    public List<SpriteFrame> getFrames() { return frames.getCachedList(); }
    public SpriteFrame getCurrentFrame() { return getFrames().get(currentFrameIndex); }
    public Sprite getCurrentSprite() { return getCurrentFrame().getSprite(); }

    @Override
    public void setSecondsIntoAnimation(float secondsIntoAnimation) {
        float dt = secondsIntoAnimation - this.secondsIntoAnimation;    // TODO: sortedFastList getSurroundingEntries ??
        while (dt < 0) dt += getDuration();
        progressCurrentFrame(dt);
        super.setSecondsIntoAnimation(secondsIntoAnimation);
    }

    /** Go "dt" time forward in this animation. */
    private void progressCurrentFrame(float dt) {
        if (!doesLoop() && dt + secondsIntoAnimation == durationSeconds) return; // TODO: sortedFastList getSurroundingEntries would fix this (weird if-case).

        currentFrameSeconds += dt;
        SpriteFrame frame = getCurrentFrame();
        while (currentFrameSeconds >= frame.getDuration()) {
            currentFrameSeconds -= frame.getDuration();
            nextFrame();
        }
    }

    private void nextFrame() {
        currentFrameIndex = (currentFrameIndex + 1) % frames.size();
    }

}