package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.ShapeDrawer;
import com.kbindiedev.verse.gfx.SpriteBatch;
import org.joml.Vector3f;

import java.util.List;
import java.util.stream.Collectors;

/** Represents an ordered list of glyphs. */
public class Text {

    private int fontSize;
    private int linebreakWidth;
    private List<BitmapGlyph> linebreakable;

    private List<BitmapGlyph> textGlyphs;
    private BitmapFont font;

    public Text(String text, BitmapFont font, int fontSize) {
        textGlyphs = text.codePoints().mapToObj(font::getGlyph).collect(Collectors.toList());
        this.font = font;
        this.fontSize = fontSize;
    }

    // TODO: getWidth/getHeight, x, y should be center of text
    public void draw(SpriteBatch batch, float x, float y) {
        font.drawLine(textGlyphs, batch, x, y, fontSize, FlowMode.CENTER);
    }

}