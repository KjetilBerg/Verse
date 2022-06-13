package com.kbindiedev.verse.maps;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Maps tiles onto a space with an arbitrary position and size.
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

    public void addEntry(Tile tile, int x, int y) {
        entries.add(new Entry(tile, x, y, tile.getWidth(), tile.getHeight()));
    }
    public void addEntry(Tile tile, int x, int y, int width, int height) {
        entries.add(new Entry(tile, x, y, width, height));
    }

    public Collection<Entry> getAllEntries() { return entries; }

    public static class Entry {
        private Tile tile;
        private int x, y, width, height;
        private Entry(Tile tile, int x, int y, int width, int height) {
            this.tile = tile; this.x = x; this.y = y; this.width = width; this.height = height;
        }
        public Tile getTile() { return tile; }
        public int getX() { return x; }
        public int getY() { return y; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
    }

}