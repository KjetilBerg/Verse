package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.profiling.exceptions.InvalidDataException;

import java.io.IOException;
import java.io.InputStream;

public interface ITileMapLoaderImplementation {

    /**
     * Load a Tilemap by the implementor's standard.
     * The received InputStream should not be closed by the implementor.
     * @param stream - The stream to retrieve the data from.
     * @return the loaded Tilemap.
     * @throws IOException - If any IO errors occur.
     * @throws InvalidDataException - If the data from the stream does not match the implementor's standards.
     */
    Tilemap loadTileMap(InputStream stream) throws InvalidDataException, IOException;

}