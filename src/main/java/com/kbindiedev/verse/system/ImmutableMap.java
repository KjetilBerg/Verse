package com.kbindiedev.verse.system;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * The implementation of java's "UnmodifiableMap" is not public. This is the only reason this class exists.
 *
 * @param <T> - The map's key.
 * @param <E> - The map's value.
 */
public class ImmutableMap<T, E> implements Map<T, E> {

    private Map<T, E> map;

    /**
     * Create a wrapper around another map.
     * @param map - The map to create a wrapper for.
     */
    public ImmutableMap(Map<T, E> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    @Override
    public int size() { return map.size(); }

    @Override
    public boolean isEmpty() { return map.isEmpty(); }

    @Override
    public boolean containsKey(Object key) { return map.containsKey(key); }

    @Override
    public boolean containsValue(Object value) { return map.containsValue(value); }

    @Override
    public E get(Object key) { return map.get(key); }

    @Override
    public E put(T key, E value) { return map.put(key, value); }

    @Override
    public E remove(Object key) { return map.remove(key); }

    @Override
    public void putAll(Map<? extends T, ? extends E> m) { map.putAll(m); }

    @Override
    public void clear() { map.clear(); }

    @Override
    public Set<T> keySet() { return map.keySet(); }

    @Override
    public Collection<E> values() { return map.values(); }

    @Override
    public Set<Entry<T, E>> entrySet() { return map.entrySet(); }

    // others

    @Override
    public int hashCode() { return map.hashCode(); }

    @Override
    public boolean equals(Object o) { return o == this || map.equals(o); }

    @Override
    public String toString() { return map.toString(); }
}
