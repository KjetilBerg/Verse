package com.kbindiedev.verse.maps;

/**
 * Describes a tile in a TileMap.
 *
 * @see StaticTile
 * @see AnimatedTile
 */
public abstract class Tile {

    // TODO: int or float?
    public abstract int getWidth();
    public abstract int getHeight();

}
