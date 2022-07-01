package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.util.Properties;

/** Describes a group layer in a {@link OldTileMap}. */
public class ObjectLayer extends TileMapLayer {

    private MapObjects objects;

    private String type;
    private float opacity;
    private boolean visible;
    private Pixel tint;
    private float offsetX;
    private float offsetY;

    public ObjectLayer(Tilemap tilemap, Properties properties, String name) { this(tilemap, properties, name,""); }
    public ObjectLayer(Tilemap tilemap, Properties properties, String name, String type) {
        super(tilemap, properties, name);
        objects = new MapObjects(tilemap.getTileset());

        this.type = type;
        opacity = 1f;
        visible = true;
        tint = Pixel.SOLID_WHITE;
        offsetX = 0f;
        offsetY = 0f;
    }

    public String getType() { return type; }

    public void addMapObject(MapObject o) { objects.addMapObject(o); }
    public MapObjects getMapObjects() { return objects; }

    public float getOpacity() { return opacity; }
    public void setOpacity(float opacity) { this.opacity = opacity; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public Pixel getTint() { return tint; }
    public void setTint(Pixel tint) { this.tint = tint; }

    public float getOffsetX() { return offsetX; }
    public void setOffsetX(float offsetX) { this.offsetX = offsetX; }

    public float getOffsetY() { return offsetY; }
    public void setOffsetY(float offsetY) { this.offsetY = offsetY; }

}