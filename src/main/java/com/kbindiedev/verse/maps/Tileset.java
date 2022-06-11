package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.system.BiHashMap;

import java.util.Collection;
import java.util.Map;

/** A set of tiles. */
public class Tileset {

    private String name;
    private BiHashMap<Integer, Sprite> idToSprite;

    public Tileset() { this(""); }
    public Tileset(String name) {
        this.name = name;
        idToSprite = new BiHashMap<>();
    }

    public String getName() { return name; }

    /** Get an id by the given sprite, or -1 if none exist. */
    public int getId(Sprite sprite) {
        return idToSprite.getForValueOrDefault(sprite, -1);
    }

    /** Get a sprite by the given id, or null if none exist. */
    public Sprite getSprite(int id) {
        return idToSprite.getForKeyOrDefault(id, null);
    }

    /**
     * Register a sprite to this Tileset.
     * If an id is occupied by another sprite, then nothing happens.
     * @param id - The id of the sprite in this Tileset.
     * @param sprite - The sprite.
     * @return true = added successfully, false = id slot is taken by another sprite.
     */
    public boolean registerSprite(int id, Sprite sprite) {
        if (idToSprite.containsKey(id)) return false;
        idToSprite.put(id, sprite);
        return true;
    }

    /**
     * Merge another tileset into this tileset, appending indexOffset to the provided tileset's indices.
     * Any tiles that cannot be registered by {@link #registerSprite(int, Sprite)} will be skipped.
     * @param tileset - The tileset to merge into me.
     * @param indexOffset - The offset to apply to all of the given tileset's indices.
     * @return true if all tiles in the tileset were successfully merged, false otherwise.
     */
    public boolean merge(Tileset tileset, int indexOffset) {
        boolean allOk = true;
        for (Map.Entry<Integer, Sprite> entry : tileset.idToSprite.entrySet()) {
            boolean success = registerSprite(entry.getKey() + indexOffset, entry.getValue());
            if (!success) allOk = false;
        }
        return allOk;
    }

    public Collection<Sprite> getAllSprites() { return idToSprite.values(); }

}