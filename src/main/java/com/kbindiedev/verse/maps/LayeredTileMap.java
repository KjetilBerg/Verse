package com.kbindiedev.verse.maps;

import java.util.*;

/**
 * Adds layering to TileMaps.
 *
 * @see TileMap
 */
public class LayeredTileMap {

    private HashMap<Integer, TileMap> layers;

    // TODO SOON: SortedFastList
    private List<Integer> layersSorted;

    public LayeredTileMap() {
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

    public TileMap getForLayer(int layer) {
        ensureExistsLayer(layer);
        return layers.get(layer);
    }

    /** Iterate over all TileMaps, starting from the lowest index and ending at the highest index. */
    public Iterator<TileMap> iterator() {
        Iterator<Integer> order = layersSorted.iterator();
        return new Iterator<TileMap>() {
            @Override
            public boolean hasNext() { return order.hasNext(); }
            @Override
            public TileMap next() { return layers.get(order.next()); }
        };
    }

    /** Add a layer if it does not exist, otherwise do nothing and return false. */
    private boolean ensureExistsLayer(int layer) {
        if (layers.containsKey(layer)) return false;
        layers.put(layer, new TileMap());
        layersSorted.add(layer);
        return true;
    }
}
