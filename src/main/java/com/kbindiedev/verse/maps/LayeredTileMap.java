package com.kbindiedev.verse.maps;

import java.util.*;

/**
 * Adds layering to TileMaps.
 *
 * @see OldTileMap
 */
public class LayeredTileMap {

    private Tileset tileset; // TODO: enforce tileset
    private HashMap<Integer, OldTileMap> layers;

    // TODO SOON: SortedFastList
    private List<Integer> layersSorted;

    public LayeredTileMap(Tileset tileset) {
        this.tileset = tileset;
        layers = new HashMap<>();

        //https://stackoverflow.com/questions/4903611/java-list-sorting-is-there-a-way-to-keep-a-list-permantly-sorted-automatically
        layersSorted = new ArrayList<Integer>() {
            @Override
            public boolean add (Integer i) {
                int index = Collections.binarySearch(this, i);
                if (index < 0) index = ~index;
                super.add(index, i);
                return true;
            }
        };
    }

    public Tileset getTileset() { return tileset; }

    public OldTileMap getForLayer(int layer) {
        ensureExistsLayer(layer);
        return layers.get(layer);
    }

    /**
     * Put a map for a certain layer.
     * Any existing map for the given layer will be replaced.
     * @param layer - The layer.
     * @param map - The map.
     * @return the map that was associated with the given layer before it was replaced, or null if none existed.
     */
    public OldTileMap putForLayer(int layer, OldTileMap map) {
        OldTileMap existing = layers.get(layer);
        makeExist(layer, map);
        return existing;
    }

    /** Iterate over all TileMaps, starting from the lowest index and ending at the highest index. */
    public Iterator<OldTileMap> iterator() {
        Iterator<Integer> order = layersSorted.iterator();
        return new Iterator<OldTileMap>() {
            @Override
            public boolean hasNext() { return order.hasNext(); }
            @Override
            public OldTileMap next() { return layers.get(order.next()); }
        };
    }

    // TODO: make better

    /** Add a layer if it does not exist, otherwise do nothing and return false. */
    private boolean ensureExistsLayer(int layer) {
        if (layers.containsKey(layer)) return false;
        return makeExist(layer, new OldTileMap(tileset));
    }

    /** Add the given map to layers if none exist for that layer, otherwise do nothing and return false. */
    private boolean makeExist(int layer, OldTileMap map) {
        if (layers.containsKey(layer)) return false;
        layers.put(layer, map);
        layersSorted.add(layer);
        return true;
    }
}
