package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.animation.SpriteAnimation;

/** Represents a Tile that is a SpriteAnimation */
public class AnimatedTile extends Tile {

    private SpriteAnimation animation;

    public AnimatedTile(SpriteAnimation animation) { this.animation = animation; }

    public SpriteAnimation getAnimation() { return animation; }

    // TODO: width/height

    @Override
    public int getWidth() {
        if (animation.getFrames().size() > 0) return animation.getFrames().get(0).getSprite().getWidth();
        return 16;
    }

    @Override
    public int getHeight() {
        if (animation.getFrames().size() > 0) return animation.getFrames().get(0).getSprite().getHeight();
        return 16;
    }
}