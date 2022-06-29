package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.util.Properties;

/** Describes some object in a {@link TileMap}.
 *
 * @see MapObjects
 * @see ObjectLayer
 */
public class MapObject {

    private Tileset tileset;
    private Properties properties;

    private String name;
    private String type;
    private float x, y;
    private float width, height;
    private float rotation;
    private int referencedTileId;

    public MapObject(Tileset tileset, Properties properties, String name, String type, float x, float y, float width, float height, float rotation, int referencedTileId) {
        this.tileset = tileset;
        this.properties = properties;

        this.name = name;
        this.type = type;
        this.x = x; this.y = y; this.width = width; this.height = height;
        this.rotation = rotation;
        this.referencedTileId = referencedTileId;
    }

    public Tileset getTileset() { return tileset; }
    public Properties getProperties() { return properties; }
    public String getName() { return name; }
    public String getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getRotation() { return rotation; }
    public Tile getReferencedTile() { return tileset.getTile(referencedTileId); }

}