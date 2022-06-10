package com.kbindiedev.verse.system;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/** Bi-directional map */
public class BiMap<K, V> implements Map<K, V> {

    private Map<K, V> keyToValueMap;
    private Map<V, K> valueToKeyMap;

    public BiMap(Supplier<Map<K, V>> keyToValueMapSupplier, Supplier<Map<V, K>> valueToKeyMapSupplier) {
        keyToValueMap = keyToValueMapSupplier.get();
        valueToKeyMap = valueToKeyMapSupplier.get();
    }

    public V getForKey(K key) { return get(key); }
    public K getForValue(V value) { return valueToKeyMap.get(value); }

    public V getForKeyOrDefault(K key, V def) {
        V value = getForKey(key);
        if (value != null) return value;
        return def;
    }

    public K getForValueOrDefault(V value, K def) {
        K key = getForValue(value);
        if (key != null) return key;
        return def;
    }

    @Override
    public int size() {
        return keyToValueMap.size();
    }

    @Override
    public boolean isEmpty() {
        return keyToValueMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keyToValueMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return valueToKeyMap.containsKey(value);
    }

    @Override
    public V get(Object key) {
        return keyToValueMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        valueToKeyMap.put(value, key);
        return keyToValueMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        V value = keyToValueMap.remove(key);
        valueToKeyMap.remove(value);
        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) put(entry.getKey(), entry.getValue());
    }

    @Override
    public void clear() {
        keyToValueMap.clear();
        valueToKeyMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return keyToValueMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return keyToValueMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return keyToValueMap.entrySet();
    }
}
