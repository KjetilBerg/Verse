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

    public BitmapFont() {
        glyphs = new HashMap<>();
    }

    public BitmapGlyph getGlyph(int id) { return glyphs.get(id); }

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

            float kerning = previousGlyph.getKerning(glyph) * fontSize / previousGlyph.getGlyphSize();
            cursorX -= kerning;

            float glyphScale = fontSize / glyph.getGlyphSize();
            float xOffset = glyph.getXOffset() * glyphScale;
            float yOffset = glyph.getYOffset() * glyphScale;
            float width = glyph.getSprite().getWidth() * glyphScale;
            float height = glyph.getSprite().getHeight() * glyphScale;

            batch.draw(glyph.getSprite(), flow.x + cursorX + xOffset, flow.y + yOffset, width, height);

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
        Vector2f centerToOld = distanceToCenter(oldMode).mul(size).mul(-1);
        Vector2f newToCenter = distanceToCenter(newMode).mul(size);

        return centerToOld.add(newToCenter).add(point);
    }

    // TODO: move to FlowMode?
    /** @return a normalized vector (-0.5f to 0.5f) representing the distance away from FlowMode: CENTER */
    private Vector2f distanceToCenter(FlowMode mode) {
        float dx = 0, dy = 0;
        if (mode == FlowMode.TOP_LEFT || mode == FlowMode.LEFT || mode == FlowMode.BOTTOM_LEFT)        dx = 0.5f;
        if (mode == FlowMode.TOP_RIGHT || mode == FlowMode.RIGHT || mode == FlowMode.BOTTOM_RIGHT)     dx = -0.5f;
        if (mode == FlowMode.TOP_LEFT || mode == FlowMode.TOP || mode == FlowMode.TOP_RIGHT)           dy = -0.5f;
        if (mode == FlowMode.BOTTOM_LEFT || mode == FlowMode.BOTTOM || mode == FlowMode.BOTTOM_RIGHT)  dy = 0.5f;
        return new Vector2f(dx, dy);
    }

    public float getWidth(GlyphSequence sequence, float fontSize) {
        return sequence.getGlyphIds().stream()
                .map(this::getGlyph)
                .filter(Objects::nonNull)
                .map(g -> g.getWidth() * fontSize / g.getGlyphSize())
                .reduce((a, b) -> a+b)
                .orElse(0f);
    }

}