package com.kbindiedev.verse.ecs.datastore;

/**
 * Implementors of this interface must have a fixed size in bytes.
 * This means every field in the implementor's class must implement IConstantSize, be a primitive or be a {@link com.kbindiedev.verse.ecs.Reference}
 */
public interface IConstantSize {}
