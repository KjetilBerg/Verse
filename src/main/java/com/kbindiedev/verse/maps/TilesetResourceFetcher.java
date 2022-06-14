package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.ecs.datastore.SpriteAnimation;
import com.kbindiedev.verse.gfx.Sprite;

/** Allows you to fetch certain resources from {@link Tileset}. */
public class TilesetResourceFetcher {

    /**
     * @param tileset - The tileset.
     * @param id - The id.
     * @return the sprite corresponding to the tile in the tileset at the given id.
     * @throws IllegalArgumentException - if the tile at the given id is not a StaticTile.
     */
    public static Sprite getSprite(Tileset tileset, int id) {
        Tile tile = tileset.getTile(id);
        if (!(tile instanceof StaticTile)) throw new IllegalArgumentException(String.format("tile with id: %d is not a StaticTile", id));
        return ((StaticTile)tile).getSprite();
    }

    /**
     * @param tileset - The tileset.
     * @param id - The id.
     * @return the animation corresponding to the tile in the tileset at the given id.
     * @throws IllegalArgumentException - if the tile at the given id is not an AnimatedTile.
     */
    public static SpriteAnimation getAnimation(Tileset tileset, int id) {
        Tile tile = tileset.getTile(id);
        if (!(tile instanceof AnimatedTile)) throw new IllegalArgumentException(String.format("tile with id: %d is not an AnimatedTile", id));
        return ((AnimatedTile)tile).getAnimation();
    }

}