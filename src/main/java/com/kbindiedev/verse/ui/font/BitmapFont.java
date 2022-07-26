package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.gfx.SpriteBatch;
import com.kbindiedev.verse.profiling.Assertions;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Objects;

// TODO: abstract into typeface ?

/** Represents a collection of {@link BitmapGlyph}. */
public class BitmapFont {

    private HashMap<Integer, BitmapGlyph> glyphs;
    private int fontSize;
    private int lineHeight;
    private int base;

    public BitmapFont(int fontSize, int lineHeight, int base) {
        glyphs = new HashMap<>();
        this.fontSize = fontSize;
        this.lineHeight = lineHeight;
        this.base = base;
    }

    public BitmapGlyph getGlyph(int id) { return glyphs.get(id); }
    public int getFontSize() { return fontSize; }
    public int getLineHeight() { return lineHeight; }

    public void registerGlyph(BitmapGlyph glyph) {
        if (glyphs.containsKey(glyph.getId())) Assertions.warn("glyph for id: %d already exists. overwriting...", glyph.getId());
        glyphs.put(glyph.getId(), glyph);
    }

    /** Draw a list of glyphs in order on a single line. */
    public void drawLine(GlyphSequence sequence, SpriteBatch batch, float x, float y, float fontSize, FlowMode flowMode) {
        Vector2f flow = adjustForFlowMode(FlowMode.BOTTOM_LEFT, flowMode, new Vector2f(x, y),
                new Vector2f(getWidth(sequence, fontSize), 1f)); // TODO: getWidth(List<Glyph>) and getLineHeight()

        float cursorX = 0;

        BitmapGlyph previousGlyph = BitmapGlyph.EMPTY_GLYPH;

        for (BitmapGlyph glyph : sequence.getGlyphs(this)) {

            // TODO consider: assert ?
            if (glyph == null) continue;

            float kerning = previousGlyph.getKerning(glyph) * fontSize / this.fontSize;
            cursorX -= kerning;

            float glyphScale = fontSize / this.fontSize;
            float xOffset = glyph.getXOffset() * glyphScale;
            float yOffset = glyph.getYOffset() * glyphScale;
            float width = glyph.getSprite().getWidth() * glyphScale;
            float height = glyph.getSprite().getHeight() * glyphScale;

            batch.draw(glyph.getSprite(), flow.x + cursorX + xOffset, flow.y + yOffset, width, height); // TODO: consider base? (tried, looks weird)

            float xAdvance = glyph.getXAdvance() * glyphScale;
            cursorX += xAdvance;
        }
    }

    // TODO: move into general helper class?
    /**
     * @param oldMode - The old FlowMode that was followed.
     * @param newMode - The FlowMode that the returned value should represent.
     * @param point - The old point position.
     * @param size - The size of the element.
     * @return a newly created vector2f representing a translated position such that the given "toMode" is followed.
     *      assumes no rotation
     */
    private Vector2f adjustForFlowMode(FlowMode oldMode, FlowMode newMode, Vector2f point, Vector2f size) {
        Vector2f centerToOld = FlowMode.directionToCenter(oldMode).mul(size).mul(-1);
        Vector2f newToCenter = FlowMode.directionToCenter(newMode).mul(size);

        return centerToOld.add(newToCenter).add(point);
    }

    // TODO FIXME: sequence width varies when kernings are applied (not considered here)
    public float getWidth(GlyphSequence sequence, float fontSize) {
        return sequence.getGlyphIds().stream()
                //.map(this::getGlyph)
                //.filter(Objects::nonNull)
                //.map(g -> g.getWidth() * fontSize / g.getGlyphSize())
                .map(i -> getWidth(i, fontSize))
                .reduce((a, b) -> a+b)
                .orElse(0f);
    }

    public float getWidth(int glyphId, float fontSize) {
        BitmapGlyph glyph = getGlyph(glyphId);
        if (glyph == null) return 0f;
        return glyph.getWidth() * fontSize / this.fontSize;
    }

}