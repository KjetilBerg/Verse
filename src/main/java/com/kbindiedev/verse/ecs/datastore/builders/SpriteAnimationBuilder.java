package com.kbindiedev.verse.ecs.datastore.builders;

import com.kbindiedev.verse.ecs.datastore.SpriteAnimation;
import com.kbindiedev.verse.ecs.datastore.SpriteFrame;

/**
 * Generates SpriteAnimation objects.
 *
 * @see SpriteAnimation
 */
public class SpriteAnimationBuilder implements IDataStoreObjectBuilder<SpriteAnimation> {

    private SpriteAnimation subject;

    public SpriteAnimationBuilder() {
        subject = new SpriteAnimation();
    }

    public void setCurrentFrameSeconds(float currentFrameSeconds) { subject.setCurrentFrameSeconds(currentFrameSeconds); }
    public void setCurrentFrameIndex(int currentFrameIndex) { subject.setCurrentFrameIndex(currentFrameIndex); }

    public void addFrame(SpriteFrame frame) {
        subject.getFrames().add(frame);
    }

    /** Reset this builder with the given animation as a base. */
    private void reset(SpriteAnimation animation) {
        reset();
        subject.setCurrentFrameIndex(animation.getCurrentFrameIndex());
        subject.setCurrentFrameSeconds(animation.getCurrentFrameSeconds());
        for (SpriteFrame frame : animation.getFrames()) addFrame(frame);
    }

    @Override
    public SpriteAnimation build(boolean reset) {
        SpriteAnimation animation = subject;
        if (reset) reset(); else reset(animation);
        return animation;
    }

    @Override
    public void reset() {
        subject = new SpriteAnimation();
    }

}