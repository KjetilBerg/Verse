package com.kbindiedev.verse.maps;

// TODO: this is a temporary class

import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.gfx.Texture;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.profiling.exceptions.InvalidDataException;

import java.io.InputStream;

/**
 * A test-loader for an asset pack for a demo.
 */
public class TestTileMapLoader implements ITileMapLoaderImplementation {

    private static String basePath = "./../spritepack_demo/";
    private static Tileset tileset = new Tileset();

    //static { prepareTileset(); }

    private static void prepareTileset() {

        Texture fences = new GLTexture(getPath("tilesets/fences.png"));

        tileset.registerSprite(0, new Sprite(fences, 0f, 0f, 0.25f, 0.25f));
        tileset.registerSprite(1, new Sprite(fences, 0.0f, 0.5f, 0.25f, 0.75f));

    }

    private static String getPath(String path) { return basePath + path; }


    @Override
    public LayeredTileMap loadTileMap(InputStream stream) {
        // note: this is a demo, and the inputstream is ignored.

        LayeredTileMap layeredTileMap = new LayeredTileMap();
        TileMap tilemap = layeredTileMap.getForLayer(0);

        tilemap.addEntry(tileset.getSprite(0), 0, 0, 16, 16);
        tilemap.addEntry(tileset.getSprite(0), 16, 0, 16, 16);
        tilemap.addEntry(tileset.getSprite(1), 0, 16, 16, 16);
        tilemap.addEntry(tileset.getSprite(1), 16, 16, 16, 16);

        return layeredTileMap;

    }

}