package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.gfx.Sprite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Maps sprites onto a space with an arbitrary position and size.
 *
 * @see LayeredTileMap
 */
public class TileMap {

    private String name;
    private Collection<Entry> entries;

    public TileMap() { this(""); }
    public TileMap(String name) {
        this.name = name;
        entries = new ArrayList<>();
    }

    public String getName()  { return name; }

    public void addEntry(Sprite sprite, int x, int y) {
        addEntry(sprite, x, y, sprite.getWidth(), sprite.getHeight());
    }

    public void addEntry(Sprite sprite, int x, int y, int width, int height) {
        entries.add(new Entry(sprite, x, y, width, height));
    }

    public Collection<Entry> getAllEntries() { return entries; }

    public static class Entry {
        private Sprite sprite;
        private int x, y, width, height;
        private Entry(Sprite sprite, int x, int y, int width, int height) {
            this.sprite = sprite; this.x = x; this.y = y; this.width = width; this.height = height;
        }
        public Sprite getSprite() { return sprite; }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
    }

}