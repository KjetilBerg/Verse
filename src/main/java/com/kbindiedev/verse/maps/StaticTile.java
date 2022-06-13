package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.gfx.Sprite;

/** Represents a Tile that is a static sprite. */
public class StaticTile extends Tile {

    private Sprite sprite;

    public StaticTile(Sprite sprite) { this.sprite = sprite; }

    public Sprite getSprite() { return sprite; }

    @Override
    public int getWidth() { return sprite.getWidth(); }

    @Override
    public int getHeight() { return sprite.getHeight(); }
}