package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.util.Properties;

/** Describes a group layer in a {@link TileMap}. */
public class ObjectLayer {

    private TileMap tilemap;
    private Properties properties;
    private MapObjects objects;

    private String name;
    private String type;
    private float opacity;
    private boolean visible;
    private Pixel tint;
    private float offsetX;
    private float offsetY;


    public ObjectLayer(TileMap tilemap, String name) { this(tilemap, name, ""); }
    public ObjectLayer(TileMap tilemap, String name, String type) {
        this.tilemap = tilemap;
        properties = new Properties();
        objects = new MapObjects(tilemap.getTileset());

        this.name = name;
        this.type = type;
        opacity = 1f;
        visible = true;
        tint = Pixel.SOLID_WHITE;
        offsetX = 0f;
        offsetY = 0f;
    }

    public TileMap getTilemap() { return tilemap; }
    public String getName() { return name; }
    public String getType() { return type; }

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