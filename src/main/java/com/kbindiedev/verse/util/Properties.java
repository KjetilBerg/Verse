package com.kbindiedev.verse.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Properties {

    private Map<String, Object> properties;

    public Properties() {
        properties = new HashMap<>();
    }

    /** @return the property stored by the given key, or null if none stored. */
    public Object get(String key) { return properties.get(key); }

    /** @return the property stored by the given key, or return the provided "def" object if no value is stored for that key. */
    public Object getOrDefault(String key, Object def) {
        Object value = get(key);
        if (value == null) value = def;
        return value;
    }

    /** @return the property as a certain type by the given key, or null if no property is stored for that key or that property is of another type. */
    public <T> T getAs(String key, Class<T> clazz) {
        Object value = get(key);
        if (clazz.isInstance(value)) return (T)value;
        return null;
    }

    /** @return the property as a certain type by the given key, or the provided default value if no property is stored for that key or that property is of another type. */
    public <T> T getAsOrDefault(String key, Class<T> clazz, T def) {
        T value = getAs(key, clazz);
        if (value == null) value = def;
        return value;
    }

    /** Set the value for a given key. */
    public void put(String key, Object value) { properties.put(key, value); }

    /** @return true if some value exists for the given key. */
    public boolean containsKey(String key) { return properties.containsKey(key); }

    /** @return the entry set of all entries in this property object. */
    public Set<Map.Entry<String, Object>> entrySet() { return properties.entrySet(); }

    /**
     * Put all properties from the given properties onto myself.
     * @param other - The set of properties to put onto myself.
     * @param override - Whether or not to override existing properties for the same names.
     * @return whether or not a property already existed for some key in the given properties.
     */
    public boolean putAll(Properties other, boolean override) {
        boolean clash = false;
        for (Map.Entry<String, Object> e : other.properties.entrySet()) {
            boolean contains = properties.containsKey(e.getKey());
            if (contains) clash = true;
            if (!contains || override) properties.put(e.getKey(), e.getValue());
        }
        return clash;
    }

}