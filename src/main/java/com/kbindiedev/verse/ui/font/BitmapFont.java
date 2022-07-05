package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.HashMap;

// TODO: abstract into typeface ?

/** Represents a collection of {@link Glyph}. */
public class BitmapFont {

    private HashMap<Integer, Glyph> glyphs;

    public BitmapFont() {
        glyphs = new HashMap<>();
    }

    public Glyph getGlyph(int id) { return glyphs.get(id); }

    public void registerGlyph(Glyph glyph) {
        if (glyphs.containsKey(glyph.getId())) Assertions.warn("glyph for id: %d already exists. overwriting...", glyph.getId());
        glyphs.put(glyph.getId(), glyph);
    }

}