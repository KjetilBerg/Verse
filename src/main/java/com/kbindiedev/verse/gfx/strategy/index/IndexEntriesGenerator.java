package com.kbindiedev.verse.gfx.strategy.index;

import com.kbindiedev.verse.profiling.Assertions;

/** Generates indexEntries (indices) of various forms. */
public class IndexEntriesGenerator {

    public static short[] generateIndicesTriangles(int numVertices, TriangleMode mode) {
        if (numVertices < 3) {
            Assertions.warn("Generating triangle indices for numVertices < 3 (got: %d). assuming 0 indices.", numVertices);
            return new short[0];
        }

        short[] ret = new short[(numVertices - 2) * 3 + (mode == TriangleMode.ZERO_IS_CENTER ? 3 : 0)];
        int index = 0;
        for (int i = 2; i < numVertices; ++i) {
            ret[index++] = (short)(0);
            ret[index++] = (short)(i - 1);
            ret[index++] = (short)(i);
        }

        if (mode == TriangleMode.ZERO_IS_CENTER) {
            ret[index++] = (short)(0);
            ret[index++] = (short)(numVertices - 1);
            ret[index++] = (short)(1);
        }
        return ret;
    }

    public static short[] generateIndicesLines(int numVertices, LineMode mode) {

        if (numVertices < 2) {
            Assertions.warn("Generating line indices for numVertices < 2 (got: %d). assuming 0 indices.", numVertices);
            return new short[0];
        }

        int size = 0;
        switch (mode) {
            case NORMAL: size = numVertices * 2; break;
            case ZERO_IS_CENTER_TO_ALL: size = (numVertices - 1) * 4; break;
            case ZERO_IS_CENTER_TO_FIRST: size = numVertices * 2; break;
            case ZERO_IS_CENTER_OUTLINE_ONLY: size = (numVertices - 1) * 2; break;
            default: Assertions.warn("unknown LineMode: %s", mode.name()); break;
        }

        short[] ret = new short[size];
        int index = 0;
        for (int i = 1; i < numVertices-1; ++i) {
            ret[index++] = (short)(i);
            ret[index++] = (short)(i + 1);
        }

        if (mode == LineMode.NORMAL) {
            if (numVertices > 2) {
                ret[index++] = (short)(numVertices - 1);
                ret[index++] = (short)(0);
            }
            ret[index++] = (short)(0);
            ret[index++] = (short)(1);

            return ret;
        }

        if (numVertices > 2) {
            ret[index++] = (short)(numVertices - 1);
            ret[index++] = (short)(1);
        }

        if (mode == LineMode.ZERO_IS_CENTER_OUTLINE_ONLY) return ret;

        ret[index++] = (short)(0);
        ret[index++] = (short)(1);

        if (mode == LineMode.ZERO_IS_CENTER_TO_FIRST) return ret;

        for (int i = 2; i < numVertices; ++i) {
            ret[index++] = (short)(0);
            ret[index++] = (short)(i);
        }

        return ret;
    }

    public static short[] generateIndicesPoints(int numVertices, PointMode mode) {
        int start = 0;
        if (mode == PointMode.DISREGARD_FIRST) start = 1;

        short[] ret = new short[numVertices - start];
        int index = 0;
        for (int i = start; i < numVertices; ++i) ret[index++] = (short)(i);

        return ret;
    }

}
