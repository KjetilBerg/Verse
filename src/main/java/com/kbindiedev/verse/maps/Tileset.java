package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.system.BiHashMap;

import java.util.Collection;

/** A set of tiles. */
public class Tileset {

    private BiHashMap<Integer, Sprite> idToSprite;

    public Tileset() {
        idToSprite = new BiHashMap<>();
    }

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

    public Collection<Sprite> getAllSprites() { return idToSprite.values(); }

}