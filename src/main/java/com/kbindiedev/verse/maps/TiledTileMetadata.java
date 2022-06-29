package com.kbindiedev.verse.maps;

/**
 * Describes metadata associated with {@link Tile} fcr "Tiled" map implementations.
 * For all {@link Tile} that are generated from "Tiled" map files, they will each have a
 *      property according to {@link TmxTileMapLoader#TILE_METADATA_PROPERTY_NAME} that points to an instance of this class.
 *
 * @see TmxTileMapLoader
 * @see Tile
 */
public class TiledTileMetadata {

    private MapObjects objects;

    public TiledTileMetadata(Tileset tileset) {
        objects = new MapObjects(tileset);
    }

    public MapObjects getObjects() { return objects; }

}