package com.kbindiedev.verse.util;

import com.kbindiedev.verse.profiling.exceptions.InvalidDataException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Loads a stream of data into a data object.
 * @param <T> - The data object that this loader loads.
 */
public interface IFormatLoaderImplementation<T> {

    /**
     * Load an object by the implementor's standard.
     * The received InputStream should not be closed by the implementor.
     * @param stream - The stream to retrieve the data from.
     * @return the loaded object.
     * @throws IOException - If any IO errors occur.
     * @throws InvalidDataException - If the data from the stream does not match the implementor's standards.
     */
    T load(InputStream stream) throws InvalidDataException, IOException;

}