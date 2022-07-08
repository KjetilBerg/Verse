package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.gfx.Sprite;

import java.util.HashMap;

/** Represents a bitmap character of some font. */
public class BitmapGlyph {

    public static final BitmapGlyph EMPTY_GLYPH = new BitmapGlyph(null, 0, 1, 1, 0, 0, 0, 1);

    private Sprite sprite;      // atlas + x,y
    private int id;             // ascii or otherwise id
    private int width, height;
    private int xOffset, yOffset;
    private int xAdvance;
    private int glyphSize;      // font size, but local to glyph

    private HashMap<BitmapGlyph, Integer> kernings;

    public BitmapGlyph(Sprite sprite, int id, int width, int height, int xOffset, int yOffset, int xAdvance, int glyphSize) {
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

    public void setKerning(BitmapGlyph glyph, int kerning) { kernings.put(glyph, kerning); }
    public int getKerning(BitmapGlyph glyph) { return kernings.getOrDefault(glyph, 0); }

    @Override
    public String toString() {
        return String.format("id=%d, width=%d, height=%d, xOffset=%d, yOffset=%d, xAdvance=%d, glyphSize=%d",
                id, width, height, xOffset, yOffset, xAdvance, glyphSize);
    }

}