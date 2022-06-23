package com.kbindiedev.verse.math;

import org.joml.Vector3f;

import java.util.List;

/** Better Math utilities than what java provides. */
public class MathUtil {

    /** {@link Math#max(int, int)} but with any number of numbers, minimum 1. */
    public static int max(int... numbers) {
        if (numbers.length == 0) throw new IllegalArgumentException("numbers must have a size of at least 1");
        int highest = -Integer.MAX_VALUE;
        for (int n : numbers) { if (n > highest) highest = n; }
        return highest;
    }

    /** {@link Math#max(long, long)} but with any number of numbers, minimum 1. */
    public static long max(long... numbers) {
        if (numbers.length == 0) throw new IllegalArgumentException("numbers must have a size of at least 1");
        long highest = -Long.MAX_VALUE;
        for (long n : numbers) { if (n > highest) highest = n; }
        return highest;
    }

    /** {@link Math#max(float, float)} but with any number of numbers, minimum 1. */
    public static float max(float... numbers) {
        if (numbers.length == 0) throw new IllegalArgumentException("numbers must have a size of at least 1");
        float highest = -Float.MAX_VALUE;
        for (float n : numbers) { if (n > highest) highest = n; }
        return highest;
    }

    /** {@link Math#max(double, double)} but with any number of numbers, minimum 1. */
    public static double max(double... numbers) {
        if (numbers.length == 0) throw new IllegalArgumentException("numbers must have a size of at least 1");
        double highest = -Double.MAX_VALUE;
        for (double n : numbers) { if (n > highest) highest = n; }
        return highest;
    }

    /** {@link Math#min(int, int)} but with any number of numbers, minimum 1. */
    public static int min(int... numbers) {
        if (numbers.length == 0) throw new IllegalArgumentException("numbers must have a size of at least 1");
        int lowest = Integer.MAX_VALUE;
        for (int n : numbers) { if (n < lowest) lowest = n; }
        return lowest;
    }

    /** {@link Math#min(long, long)} but with any number of numbers, minimum 1. */
    public static long min(long... numbers) {
        if (numbers.length == 0) throw new IllegalArgumentException("numbers must have a size of at least 1");
        long lowest = Long.MAX_VALUE;
        for (long n : numbers) { if (n < lowest) lowest = n; }
        return lowest;
    }

    /** {@link Math#min(float, float)} but with any number of numbers, minimum 1. */
    public static float min(float... numbers) {
        if (numbers.length == 0) throw new IllegalArgumentException("numbers must have a size of at least 1");
        float lowest = Float.MAX_VALUE;
        for (float n : numbers) { if (n < lowest) lowest = n; }
        return lowest;
    }

    /** {@link Math#min(double, double)} but with any number of numbers, minimum 1. */
    public static double min(double... numbers) {
        if (numbers.length == 0) throw new IllegalArgumentException("numbers must have a size of at least 1");
        double lowest = Double.MAX_VALUE;
        for (double n : numbers) { if (n < lowest) lowest = n; }
        return lowest;
    }

    public static Vector3f findCenter(List<Vector3f> vectors) {
        Vector3f center = new Vector3f();
        for (Vector3f v : vectors) center.add(v);
        center.div(vectors.size());
        return center;
    }

}