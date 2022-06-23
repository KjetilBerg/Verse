package com.kbindiedev.verse.gfx.strategy.providers;

import com.kbindiedev.verse.gfx.IndexDataBuffer;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.strategy.index.*;
import com.kbindiedev.verse.profiling.Assertions;

public class StaticIndicesProvider {

    /*
    private short[] indexEntriesTriangles;
    private short[] indexEntriesLines;
    private short[] indexEntriesPoints;

    public StaticIndicesProvider(short[] indexEntriesTriangles, short[] indexEntriesLines, short[] indexEntriesPoints) {
        this.indexEntriesTriangles = indexEntriesTriangles;
        this.indexEntriesLines = indexEntriesLines;
        this.indexEntriesPoints = indexEntriesPoints;
    }

    public static StaticIndicesProvider newByPolygonShape(int numVertices, PolygonIndexMode indexMode) {
        return new StaticIndicesProvider(
                PolygonIndexEntriesGenerator.generateIndicesTriangles(numVertices, indexMode.getTriangleMode()),
                PolygonIndexEntriesGenerator.generateIndicesLines(numVertices, indexMode.getLineMode()),
                PolygonIndexEntriesGenerator.generateIndicesPoints(numVertices, indexMode.getPointMode())
        );
    }

    /** @return the number of index entries for a given RenderMode. */
    //public int getNumIndexEntries(Mesh.RenderMode mode) { return getIndexEntries(mode).length; }

    /** Feed one set to the given buffer, by the given RenderMode. */
    /*
    public void feed(IndexDataBuffer buffer, Mesh.RenderMode mode) {
        for (short s : getIndexEntries(mode)) buffer.addLocalIndexEntry(s);
    }

    private short[] getIndexEntries(Mesh.RenderMode mode) {
        switch (mode) {
            case TRIANGLES: return indexEntriesTriangles;
            case LINES: return indexEntriesLines;
            case POINTS: return indexEntriesPoints;
            default: Assertions.warn("unknown RenderMode: %s, assuming triangles", mode.name()); return indexEntriesTriangles;
        }
    }
    */

}
