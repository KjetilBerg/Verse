package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.gfx.SpriteBatch;

import java.util.List;
import java.util.stream.Collectors;

/** Represents an ordered list of glyphs. */
public class Text {

    private int fontSize;
    private int linebreakWidth;
    private List<Glyph> linebreakable;

    private List<Glyph> textGlyphs;

    public Text(String text, BitmapFont font) {
        textGlyphs = text.codePoints().mapToObj(font::getGlyph).collect(Collectors.toList());
    }

    // TODO: getWidth/getHeight, x, y should be center of text
    public void draw(SpriteBatch batch, int x, int y) {
        int currentX = 0;
        Glyph prevGlyph = null;
        for (Glyph glyph : textGlyphs) {
            int kerning = 0;
            if (prevGlyph != null) kerning = prevGlyph.getKerning(glyph);
            int xOffset = glyph.getXOffset();
            int xAdvance = glyph.getXAdvance();

            float scale = 0.1f;

            kerning = scale(kerning, scale);
            xOffset = scale(xOffset, scale);
            xAdvance = scale(xAdvance, scale);
            int yOffset = scale(glyph.getYOffset(), scale);

            int xDest = x + currentX + xOffset + kerning;
            int yDest = y + yOffset;
            batch.draw(glyph.getSprite(), xDest, yDest, glyph.getSprite().getWidth() * scale, glyph.getSprite().getHeight() * scale);
            currentX += xAdvance+ kerning;
            prevGlyph = glyph;
        }
    }

    private int scale(int v, float s) { return (int)(v * s); }

}