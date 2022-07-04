package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.util.Properties;

/** Describes some object in a {@link OldTileMap}.
 *
 * @see MapObjects
 * @see ObjectLayer
 */
public class MapObject {

    private Tileset tileset;
    private Properties properties;
    private MapObjectContent content;

    private String name;
    private String type;
    private float x, y;
    private float width, height;
    private float rotation;
    private int referencedTileId;

    public MapObject(Tileset tileset, Properties properties, MapObjectContent content, String name, String type,
                     float x, float y, float width, float height, float rotation, int referencedTileId) {
        this.tileset = tileset;
        this.properties = properties;
        this.content = content;

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

    public MapObjectContent getContent() { return content; }
    public boolean isContentOfType(Class<? extends MapObjectContent> clazz) { return clazz.isInstance(content); }
    /** @return the stored content as the given type, or null if no content exists or content is of a different type. */
    public <T extends MapObjectContent> T getContentAs(Class<T> clazz) {
        if (!isContentOfType(clazz)) return null;
        return clazz.cast(content);
    }

}