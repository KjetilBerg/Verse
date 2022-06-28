package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.util.Properties;

/**
 * Describes a tile in a TileMap.
 *
 * @see StaticTile
 * @see AnimatedTile
 */
public abstract class Tile {

    private Properties properties;
    private ObjectLayer objectGroup;

    // TODO: int or float?
    public abstract int getWidth();
    public abstract int getHeight();

}
