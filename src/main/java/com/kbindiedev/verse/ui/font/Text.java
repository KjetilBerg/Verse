package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.gfx.SpriteBatch;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Represents an ordered list of glyphs. */
public class Text {

    // TODO: colors, gradient ?

    private FlowMode flowMode;
    private int fontSize;
    private int linebreakWidth;
    private List<Integer> linebreakable;

    private GlyphSequence sequence;
    private BitmapFont font;

    private List<GlyphSequence> lines;

    public Text(String text, BitmapFont font, int fontSize, FlowMode flowMode) {
        this(new GlyphSequence(text), font, fontSize, flowMode);
    }
    public Text(String text, BitmapFont font, int fontSize, FlowMode flowMode, int linebreakWidth) {
        this(new GlyphSequence(text), font, fontSize, flowMode, linebreakWidth);
    }
    public Text(GlyphSequence sequence, BitmapFont font, int fontSize, FlowMode flowMode) {
        this(sequence, font, fontSize, flowMode, 0);
    }

    public Text(GlyphSequence sequence, BitmapFont font, int fontSize, FlowMode flowMode, int linebreakWidth) {
        this.sequence = sequence;
        this.font = font;
        this.fontSize = fontSize;
        this.flowMode = flowMode;
        this.linebreakWidth = linebreakWidth;

        lines = new ArrayList<>();
        linebreakable = new ArrayList<>();
        linebreakable.add(new GlyphSequence(" ").getGlyphIds().get(0)); // TODO temp

        buildLines(); // TODO: rebuild if character added
    }

    public void draw(SpriteBatch batch, float x, float y) {
        Vector2f flow = FlowMode.directionToCenter(flowMode);
        float offX = flow.x * getWidth();
        float offY = flow.y * getHeight(); // TODO: is this right?
        float cy = y;
        float localLineHeight = (float)font.getLineHeight() * fontSize / font.getFontSize();
        for (GlyphSequence seq : lines) {
            font.drawLine(seq, batch, x + offX, cy + offY, fontSize, flowMode);
            cy -= localLineHeight;
        }
    }

    // TODO: special characters, like \t and \n
    public void buildLines() {
        lines.clear();

        if (linebreakWidth == 0f) lines.add(sequence); // break = 0f -> no breaking.

        // split into "words"
        List<GlyphSequence> words = new ArrayList<>(); // TODO: should certain characters be omitted completely (space) ?
        GlyphSequence current = new GlyphSequence();
        for (int id : sequence.getGlyphIds()) {
            current.addGlyphId(id);
            if (linebreakable.contains(id)) {
                words.add(current);
                current = new GlyphSequence();
            }
        }
        words.add(current);

        // create lines from words
        GlyphSequence seq = new GlyphSequence();
        float segWidth = 0f;

        for (GlyphSequence c : words) {
            float cWidth = font.getWidth(c, fontSize);  // TODO: width varies when kernings are applied
            if (segWidth + cWidth > linebreakWidth) { // TODO: break single word
                lines.add(seq);
                seq = new GlyphSequence();
                segWidth = 0;
            }

            for (int i : c.getGlyphIds()) seq.addGlyphId(i);
            segWidth += cWidth;
        }
        lines.add(seq);
    }

    public FlowMode getFlowMode() { return flowMode; }

    public float getWidth() { return linebreakWidth; }
    public float getHeight() { return (float)fontSize * font.getLineHeight() / font.getFontSize() * lines.size(); }

}