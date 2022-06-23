package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.gfx.IndexDataBuffer;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.profiling.Assertions;

import java.util.HashMap;

/** A collection of IIndexGenerators. */
public class CollectiveIndexGenerator {

    private HashMap<Mesh.RenderMode, IIndexGenerator> map;

    public CollectiveIndexGenerator() {
        map = new HashMap<>();
        setupDefault(map);
    }

    public void set(Mesh.RenderMode renderMode, IIndexGenerator generator) { map.put(renderMode, generator); }

    public int getNumIndexEntries(int numVertices, Mesh.RenderMode mode) { return map.get(mode).getNumIndexEntries(numVertices); }

    public void feed(IndexDataBuffer buffer, int numVertices, Mesh.RenderMode mode) {
        IIndexGenerator generator = map.get(mode);
        if (generator == NoIndexGenerator.INSTANCE) Assertions.warn("unexpected use of NoIndexGenerator");
        generator.feed(buffer, numVertices);
    }

    private static void setupDefault(HashMap<Mesh.RenderMode, IIndexGenerator> map) {
        for (Mesh.RenderMode mode : Mesh.RenderMode.values()) map.put(mode, NoIndexGenerator.INSTANCE);
    }

}