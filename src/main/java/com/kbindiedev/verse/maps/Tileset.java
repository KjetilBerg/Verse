package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.system.BiHashMap;

import java.util.Collection;
import java.util.Map;

/** A set of tiles. */
public class Tileset {

    private String name;
    private BiHashMap<Integer, Tile> idToTile;

    public Tileset() { this(""); }
    public Tileset(String name) {
        this.name = name;
        idToTile = new BiHashMap<>();
    }

    public String getName() { return name; }

    /** Get an id by the given tile, or -1 if none exist. */
    public int getId(Tile tile) {
        return idToTile.getForValueOrDefault(tile, -1);
    }

    /** Get a tile by the given id, or null if none exist. */
    public Tile getTile(int id) {
        return idToTile.getForKeyOrDefault(id, null);
    }

    /**
     * Register a tile to this Tileset.
     * @param id - The id of the tile in this Tileset.
     * @param tile - The tile.
     * @param override - Whether or not I should override the existing tile taken by this id, if any.
     * @return true if no tile previously existed for the given id, false otherwise.
     */
    public boolean registerTile(int id, Tile tile, boolean override) {
        boolean free = !idToTile.containsKey(id);
        if (free || override) idToTile.put(id, tile);
        return free;
    }

    /**
     * Merge another tileset into this tileset, appending indexOffset to the provided tileset's indices.
     * @param tileset - The tileset to merge into me.
     * @param indexOffset - The offset to apply to all of the given tileset's indices.
     * @param override - if the given tileset + offset has a common index with myself, then I will override my own tile if this is true.
     * @return true if no common indices were encountered during merging, false otherwise.
     */
    public boolean merge(Tileset tileset, int indexOffset, boolean override) {
        boolean allFree = true;
        for (Map.Entry<Integer, Tile> entry : tileset.idToTile.entrySet()) {
            boolean wasFree = registerTile(entry.getKey() + indexOffset, entry.getValue(), override);
            if (!wasFree) allFree = false;
        }
        return allFree;
    }

    public Collection<Tile> getAllTiles() { return idToTile.values(); }

}