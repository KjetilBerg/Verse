package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.util.Properties;

/**
 * Describes a tile in a OldTileMap.
 *
 * @see StaticTile
 * @see AnimatedTile
 */
public abstract class Tile {

    private Properties properties;

    public Tile() { this(new Properties()); }
    public Tile(Properties properties) { this.properties = properties; }

    public Properties getProperties() { return properties; }

    // TODO: int or float?
    public abstract int getWidth();
    public abstract int getHeight();

}