package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.profiling.exceptions.UnsupportedFormatException;
import com.kbindiedev.verse.system.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/** Loads TileMaps from some stream of data. */
public class TileMapLoader {

    private static final HashMap<String, ITileMapLoaderImplementation> loaders = new HashMap<>();

    static {
        loaders.put("tmx", new TmxTileMapLoader());
    }

    public static Tilemap loadTileMap(File file) throws IOException {
        return loadTileMap(file, FileUtils.getFileExtension(file));
    }

    public static Tilemap loadTileMap(File file, String format) throws IOException {
        throwIfUnknownFormat(format);
        try (FileInputStream stream = new FileInputStream(file)) {
            return loadTileMap(stream, format);
        }
    }

    public static Tilemap loadTileMap(InputStream stream, String format) throws IOException {
        throwIfUnknownFormat(format);
        ITileMapLoaderImplementation loader = loaders.get(format);
        return loader.loadTileMap(stream);
    }

    private static void throwIfUnknownFormat(String format) {
        if (!loaders.containsKey(format)) throw new UnsupportedFormatException("unknown Tilemap format: " + format);
    }

}