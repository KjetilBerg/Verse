package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.util.Properties;

/** Describes some object in a TileMap.
 *
 * @see MapObjects
 * @see ObjectLayer
 */
public class MapObject {

    private TileMap tilemap;
    private Properties properties;

    private String name;
    private String type;
    private float x, y;
    private float width, height;
    private float rotation;
    private int referencedTileId;


    public MapObject(TileMap tilemap, Properties properties, String name, String type, float x, float y, float width, float height, float rotation, int referencedTileId) {
        this.tilemap = tilemap;
        this.properties = properties;

        this.name = name;
        this.type = type;
        this.x = x; this.y = y; this.width = width; this.height = height;
        this.rotation = rotation;
        this.referencedTileId = referencedTileId;
    }

    public TileMap getTilemap() { return tilemap; }
    public Properties getProperties() { return properties; }
    public String getName() { return name; }
    public String getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public float getRotation() { return rotation; }
    public Tile getReferencedTile() { return tilemap.getTileset().getTile(referencedTileId); }

}