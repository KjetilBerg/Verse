package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.gfx.Texture;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.InvalidDataException;
import com.kbindiedev.verse.util.IFormatLoaderImplementation;

import java.io.InputStream;
import java.util.*;

/** Loads an Angel Code BMFont file into a BitmapFont. Only plain-text files are supported; binary format is not. */
public class BMFontLoader implements IFormatLoaderImplementation<BitmapFont> {

    @Override
    public BitmapFont load(InputStream stream) {


        List<String> lines = extractLines(stream);
        Map<String, List<String>> tagData = extractTags(lines);

        integrityCheck(tagData);

        int lineHeight = Integer.parseInt(extractAttributes(tagData.get("common").get(0)).get("lineHeight"));

        HashMap<Integer, Texture> pages = loadPages(tagData.get("page"));
        HashMap<Integer, Glyph> glyphs = loadGlyphs(pages, lineHeight, tagData.get("char"));

        if (tagData.containsKey("kerning")) applyKernings(glyphs, tagData.get("kerning"));


        BitmapFont font = new BitmapFont();
        for (Glyph glyph : glyphs.values()) font.registerGlyph(glyph);

        System.out.println("kerning: " + glyphs.get("T".codePointAt(0)).getKerning(glyphs.get("o".codePointAt(0))));
        System.out.println("T".codePointAt(0) + " " + "o".codePointAt(0));

        return font;
    }

    private void applyKernings(HashMap<Integer, Glyph> glyphs, List<String> kerningTags) {
        for (String tag : kerningTags) applyKerning(glyphs, tag);
    }

    private void applyKerning(HashMap<Integer, Glyph> glyphs, String kerningTag) {
        Map<String, String> attributes = extractAttributes(kerningTag);
        int first = Integer.parseInt(attributes.get("first"));
        int second = Integer.parseInt(attributes.get("second"));
        int amount = Integer.parseInt(attributes.get("amount"));

        Glyph gFirst = glyphs.get(first), gSecond = glyphs.get(second);
        gFirst.setKerning(gSecond, amount);
    }

    private HashMap<Integer, Glyph> loadGlyphs(Map<Integer, Texture> pages, int lineHeight, List<String> charTags) {
        HashMap<Integer, Glyph> glyphs = new HashMap<>();
        for (String charTag : charTags) {
            Glyph glyph = loadGlyph(pages, lineHeight, charTag);
            glyphs.put(glyph.getId(), glyph);
        }
        return glyphs;
    }

    private Glyph loadGlyph(Map<Integer, Texture> pages, int lineHeight, String charTag) {
        Map<String, String> attributes = extractAttributes(charTag);

        int x = Integer.parseInt(attributes.get("x")), y = Integer.parseInt(attributes.get("y")),
                w = Integer.parseInt(attributes.get("width")), h = Integer.parseInt(attributes.get("height"));

        Texture page = pages.get(Integer.parseInt(attributes.get("page")));

        float u1 = (float)x / page.getWidth();
        float u2 = (float)(x+w) / page.getWidth();
        float v1 = (float)y / page.getHeight();
        float v2 = (float)(y+h) / page.getHeight();

        float temp = v1; v1 = v2; v2 = temp; //y is top-down

        Sprite sprite = new Sprite(page, u1, v1, u2, v2);

        int id = Integer.parseInt(attributes.get("id"));
        int xOffset = Integer.parseInt(attributes.get("xoffset")), yOffset = Integer.parseInt(attributes.get("yoffset"));
        int xAdvance = Integer.parseInt(attributes.get("xadvance"));
        int glyphSize = 72; // TODO

        // TODO rename BitmapGlyph ??
        return new Glyph(sprite, id, w, h, xOffset, lineHeight - yOffset - h, xAdvance, glyphSize); // y is down, should be up // TODO: input flag?
    }

    private HashMap<Integer, Texture> loadPages(List<String> pageTags) {
        HashMap<Integer, Texture> pages = new HashMap<>();
        for (String tag : pageTags) {
            Map<String, String> attributes = extractAttributes(tag);
            int id = Integer.parseInt(attributes.get("id"));
            Texture page = new GLTexture("../" + attributes.get("file")); // TODO change

            pages.put(id, page);
        }
        return pages;
    }

    private void integrityCheck(Map<String, List<String>> tagData) throws InvalidDataException {
        if (tagData.containsKey("chars")) {
            int tagCount = Integer.parseInt(extractAttributes(tagData.get("chars").get(0)).get("count"));
            if (tagCount != 0 && tagCount != tagData.get("char").size())
                Assertions.warn("chars count=%d, but found: %d characters", tagCount, tagData.get("char").size());
        }

        if (tagData.containsKey("kernings")) {
            int kerningCount = Integer.parseInt(extractAttributes(tagData.get("kernings").get(0)).get("count"));
            if (kerningCount != 0 && kerningCount != tagData.get("kerning").size())
                Assertions.warn("kernings count=%d, but found: %d kernings", kerningCount, tagData.get("kerning").size());
        }
    }

    private List<String> extractLines(InputStream stream) {
        List<String> lines = new ArrayList<>();
        Scanner scanner = new Scanner(stream);
        while (scanner.hasNextLine()) lines.add(scanner.nextLine());
        return lines;
    }

    /** Puts all lines into a map by their tag name. */
    private Map<String, List<String>> extractTags(List<String> lines) {
        HashMap<String, List<String>> map = new HashMap<>();
        for (String line : lines) {
            String tag = line.substring(0, line.indexOf(" "));
            if (!map.containsKey(tag)) map.put(tag, new ArrayList<>());
            map.get(tag).add(line);
        }
        System.out.println(lines);
        return map;
    }

    /** Puts a line of plaintext BMFont file into a map of attrib=value, skipping the leading tag. */
    private Map<String, String> extractAttributes(String line) {
        HashMap<String, String> map = new HashMap<>();
        String[] attribs = line.split(" ");
        for (int i = 1; i < attribs.length; ++i) {
            if (attribs[i].isEmpty()) continue;
            String[] parts = attribs[i].split("=");
            String key = parts[0], value = parts[1];
            if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) value = value.substring(1, value.length() - 1);
            map.put(key, value);
        }
        return map;
    }

}