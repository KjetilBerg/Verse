package com.kbindiedev.verse.profiling.exceptions;

import com.kbindiedev.verse.maps.ITileMapLoaderImplementation;

// TODO: move this to another package??

/**
 * Describes that some data provided to some class is invalid according to its standard.
 *
 * @see ITileMapLoaderImplementation
 */
public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message) { super(message); }

}
