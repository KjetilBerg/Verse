package com.kbindiedev.verse.ui.font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/** A sequence of glyph ids. */
public class GlyphSequence {

    public static GlyphSequence copy(GlyphSequence sequence) {
        GlyphSequence newSequence = new GlyphSequence(sequence.count());
        for (int id : sequence.getGlyphIds()) newSequence.addGlyphId(id);
        return newSequence;
    }

    private List<Integer> glyphIds;
    private StringBuilder stringValue;

    public GlyphSequence() { this(-1); }
    public GlyphSequence(int initialCapacity) {
        if (initialCapacity < 0) glyphIds = new ArrayList<>(); else glyphIds = new ArrayList<>(initialCapacity);
        stringValue = new StringBuilder();
    }

    public GlyphSequence(String text) {
        this(text.codePointCount(0, text.length()));
        text.codePoints().forEach(glyphIds::add);
    }

    public List<Integer> getGlyphIds() { return glyphIds; }
    public List<BitmapGlyph> getGlyphs(BitmapFont font) {
        return glyphIds.stream().map(font::getGlyph).collect(Collectors.toList());
    }

    public String getStringValue() { return stringValue.toString(); }

    /** @return the number of stored glyphs (ids). */
    public int count() { return glyphIds.size(); }

    public void addGlyphId(int id) { glyphIds.add(id); stringValue.append((char)id); }
    public void addGlyphIfValid(int id, BitmapFont font) {
        if (font.getGlyph(id) != null) addGlyphId(id);
    }

    /** Remove the last "n" stored glyphs (ids). */
    public void remove(int n) {
        if (n > glyphIds.size()) { clear(); return; }

        Iterator it = glyphIds.listIterator(glyphIds.size() - n);
        while (it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    /** Empty the sequence of all glyphs. */
    public void clear() { glyphIds.clear(); stringValue.setLength(0); }

}