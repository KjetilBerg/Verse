package com.kbindiedev.verse.ecs.datastore;

import java.util.ArrayList;
import java.util.List;

/** Describes a collection of SpriteFrames. */
public class SpriteAnimation {

    private List<SpriteFrame> frames;
    private int currentFrameIndex;
    private float currentFrameSeconds;

    public SpriteAnimation() {
        frames = new ArrayList<>(); // TODO: blank default frame
        currentFrameIndex = 0;
        currentFrameSeconds = 0f;
    }

    public List<SpriteFrame> getFrames() { return frames; }
    public void setFrames(List<SpriteFrame> frames) { this.frames = frames; }

    public int getCurrentFrameIndex() { return currentFrameIndex; }
    public void setCurrentFrameIndex(int currentFrameIndex) { this.currentFrameIndex = currentFrameIndex; }

    public float getCurrentFrameSeconds() { return currentFrameSeconds; }
    public void setCurrentFrameSeconds(float currentFrameSeconds) { this.currentFrameSeconds = currentFrameSeconds; }
}
