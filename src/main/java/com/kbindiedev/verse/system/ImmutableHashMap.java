package com.kbindiedev.verse.system;

import java.util.HashMap;

public class ImmutableHashMap<T, E> extends ImmutableMap<T, E> {


    /**
     * Create an ImmutableHashMap.
     *
     * This map is backed by a HashMap.
     */
    public ImmutableHashMap() { this(new HashMap<>()); }

    /**
     * Create a wrapper around another map.
     *
     * @param map - The map to create a wrapper for.
     */
    public ImmutableHashMap(HashMap<T, E> map) {
        super(map);
    }

}
