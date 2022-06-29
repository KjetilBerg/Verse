package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** Describes a set of {@link MapObject}. */
public class MapObjects {

    private Tileset tileset;
    private List<MapObject> allObjects;
    private HashMap<String, List<MapObject>> byName;
    private HashMap<String, List<MapObject>> byType;

    public MapObjects(Tileset tileset) {
        this.tileset = tileset;
        allObjects = new ArrayList<>();
        byName = new HashMap<>();
        byType = new HashMap<>();
    }

    public void addMapObject(MapObject object) {
        if (!object.getTileset().equals(tileset)) Assertions.warn("MapObject tileset does not match my own tileset");
        if (!byName.containsKey(object.getName())) byName.put(object.getName(), new ArrayList<>());
        if (!byType.containsKey(object.getType())) byType.put(object.getType(), new ArrayList<>());

        allObjects.add(object);
        byName.get(object.getName()).add(object);
        byType.get(object.getType()).add(object);
    }

    public Tileset getTileset() { return tileset; }

    public List<MapObject> getAllObjects() { return allObjects; }

    /**
     * @kb.time O(1).
     * @return a list of all MapObjects that have the given name, or an empty list if none exist.
     */
    public List<MapObject> getByName(String name) {
        if (!containsByName(name)) return new ArrayList<>();
        return byName.get(name);
    }
    /**
     * @kb.time O(1).
     * @return the first found MapObject that has the given name, or null if none exist.
     */
    public MapObject getFirstByName(String name) {
        List<MapObject> m = getByName(name);
        if (m.size() == 0) return null;
        return m.get(0);
    }

    /**
     * @kb.time O(1).
     * @return a list of all MapObjects that have the given type, or an empty list if none exist.
     */
    public List<MapObject> getByType(String type) {
        if (!containsByType(type)) return new ArrayList<>();
        return byType.get(type);
    }

    /**
     * @kb.time O(1).
     * @return the first found MapObject that has the given type, or null if none exist.
     */
    public MapObject getFirstByType(String type) {
        List<MapObject> m = getByType(type);
        if (m.size() == 0) return null;
        return m.get(0);
    }

    /** @kb.time O(1). */
    public boolean containsByName(String name) { return byName.containsKey(name); }
    /** @kb.time O(1). */
    public boolean containsByType(String type) { return byType.containsKey(type); }

}