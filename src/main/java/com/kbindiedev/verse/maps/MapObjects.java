package com.kbindiedev.verse.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Describes a set of {@link MapObject}. */
public class MapObjects {

    private HashMap<String, List<MapObject>> byName;
    private HashMap<String, List<MapObject>> byType;

    public MapObjects() {
        byName = new HashMap<>();
        byType = new HashMap<>();
    }

    public void addMapObject(MapObject object) {
        if (!byName.containsKey(object.getName())) byName.put(object.getName(), new ArrayList<>());
        if (!byName.containsKey(object.getType())) byType.put(object.getType(), new ArrayList<>());

        byName.get(object.getName()).add(object);
        byType.get(object.getType()).add(object);
    }

    /** @kb.time O(1). */
    public List<MapObject> getByName(String name) { return byName.get(name); }
    /** @kb.time O(1). */
    public MapObject getFirstByName(String name) {
        List<MapObject> m = getByName(name);
        if (m == null || m.size() == 0) return null;
        return m.get(0);
    }

    /** @kb.time O(1). */
    public List<MapObject> getByType(String type) { return byType.get(type); }
    /** @kb.time O(1). */
    public MapObject getFirstByType(String type) {
        List<MapObject> m = getByType(type);
        if (m == null || m.size() == 0) return null;
        return m.get(0);
    }

}