package com.kbindiedev.verse.profiling.exceptions;

import com.kbindiedev.verse.gfx.strategy.providers.IVertexProvider;

/**
 * Describes that a vertex could not be provided because "we are out".
 * @see IVertexProvider
 */
public class NotEnoughVerticesException extends RuntimeException {

    public NotEnoughVerticesException(String message) { super(message); }

}