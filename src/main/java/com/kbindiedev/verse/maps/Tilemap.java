package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.util.Properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/** A collection of {@link TileMapLayer}. */
public class Tilemap {

    private List<TileMapLayer> layers;
    private HashMap<String, TileMapLayer> layersByName;

    private Tileset tileset;

    public Tilemap(Tileset tileset) {
        layers = new ArrayList<>();
        layersByName = new HashMap<>();
        this.tileset = tileset;
    }

    public Tileset getTileset() { return tileset; }

    public TileMapLayer getLayerByName(String name) { return layersByName.get(name); }

    /** @return a layer by name as a given type, or null if no layer exists by the given name or if it is of the wrong type. */
    public <T extends TileMapLayer> T getLayerByName(String name, Class<T> asType) {
        TileMapLayer layer = getLayerByName(name);
        if (!asType.isInstance(layer)) return null;
        return asType.cast(layer);
    }

    public List<TileMapLayer> getAllLayers() { return layers; }

    @SuppressWarnings("unchecked")
    public <T extends TileMapLayer> List<T> getLayersOfType(Class<T> clazz) {
        return (List<T>)layers.stream().filter(clazz::isInstance).collect(Collectors.toList());
    }

    public TileLayer createTileLayer(Properties properties, String name) {
        TileLayer layer = new TileLayer(this, properties, name);
        add(layer);
        return layer;
    }

    public ObjectLayer createObjectLayer(Properties properties, String name, String type) {
        ObjectLayer layer = new ObjectLayer(this, properties, name, type);
        add(layer);
        return layer;
    }

    private void add(TileMapLayer layer) {
        if (layersByName.containsKey(layer.getName())) Assertions.warn("layer already exists by name: '%s'", layer.getName());
        layers.add(layer);
        layersByName.put(layer.getName(), layer);
    }

}