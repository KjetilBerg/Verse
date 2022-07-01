package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.util.Properties;

/**
 * Represents a layer in a {@link Tilemap}
 *
 * @see TileLayer
 * @see ObjectLayer
 */
public abstract class TileMapLayer {

    private Tilemap tilemap;
    private Properties properties;
    private String name;

    public TileMapLayer(Tilemap tilemap, Properties properties, String name) {
        this.tilemap = tilemap;
        this.properties = properties;
        this.name = name;
    }

    public Tilemap getTilemap() { return tilemap; }
    public Properties getProperties() { return properties; }
    public String getName() { return name; }

}