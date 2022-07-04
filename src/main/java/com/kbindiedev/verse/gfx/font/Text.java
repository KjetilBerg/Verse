package com.kbindiedev.verse.gfx.font;

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
            int xDest = currentX + x + glyph.getXOffset();
            if (prevGlyph != null) xDest += glyph.getKerning(prevGlyph);
            batch.draw(glyph.getSprite(), xDest, y + glyph.getYOffset());
            currentX += glyph.getXAdvance();
            if (prevGlyph != null) currentX += glyph.getKerning(prevGlyph);
            prevGlyph = glyph;
        }
    }

}