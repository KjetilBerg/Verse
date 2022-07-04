package com.kbindiedev.verse.gfx.font;

import com.kbindiedev.verse.gfx.Sprite;

import java.util.HashMap;

/** Represents a character of some font. */
public class Glyph {

    private Sprite sprite;      // atlas + x,y
    private int id;             // ascii or otherwise id
    private int width, height;
    private int xOffset, yOffset;
    private int xAdvance;
    private int glyphSize;      // font size, but local to glyph

    private HashMap<Glyph, Integer> kernings;

    public Glyph(Sprite sprite, int id, int width, int height, int xOffset, int yOffset, int xAdvance, int glyphSize) {
        this.sprite = sprite;
        this.id = id;
        this.width = width;
        this.height = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.xAdvance = xAdvance;
        this.glyphSize = glyphSize;

        kernings = new HashMap<>();
    }

    public Sprite getSprite() { return sprite; }
    public int getId() { return id; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getXOffset() { return xOffset; }
    public int getYOffset() { return yOffset; }
    public int getXAdvance() { return xAdvance; }
    public int getGlyphSize() { return glyphSize; }

    public void setKerning(Glyph glyph, int kerning) { kernings.put(glyph, kerning); }
    public int getKerning(Glyph glyph) { return kernings.get(glyph); }

}