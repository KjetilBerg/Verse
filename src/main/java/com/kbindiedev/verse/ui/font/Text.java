package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.gfx.SpriteBatch;

import java.util.List;

/** Represents an ordered list of glyphs. */
public class Text {

    // TODO: colors, gradient ?

    private FlowMode flowMode;
    private int fontSize;
    private int linebreakWidth;
    private List<BitmapGlyph> linebreakable;

    private GlyphSequence sequence;
    private BitmapFont font;

    public Text(String text, BitmapFont font, int fontSize, FlowMode flowMode) {
        this(GlyphSequence.fromString(text), font, fontSize, flowMode);
    }

    public Text(GlyphSequence sequence, BitmapFont font, int fontSize, FlowMode flowMode) {
        this.sequence = sequence;
        this.font = font;
        this.fontSize = fontSize;
        this.flowMode = flowMode;
    }

    public void draw(SpriteBatch batch, float x, float y) {
        font.drawLine(sequence, batch, x, y, fontSize, flowMode);
    }

    public FlowMode getFlowMode() { return flowMode; }

}