package com.kbindiedev.verse.math.helpers;

import com.kbindiedev.verse.math.shape.Polygon;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** Creates various polygons. */
public class PolygonMaker {

    /** Create a polygon of n vertices (and thereby n sides) of a given radius. */
    public static Polygon generatePolygon(int n, float radius) { return generatePolygon(n, radius, 0f); }

    /**
     * Creates a polygon of n vertices (and thereby n sides) of a given radius, offset by some rotation.
     * The resulting polygon is said to have no rotation.
     */
    public static Polygon generatePolygon(int n, float radius, float rotation) {
        double radsPerPoint = 2 * Math.PI / n;

        List<Vector3f> points = new ArrayList<>(n);

        double currentRad = rotation;
        while (n-- > 0) {
            float x = (float)(Math.cos(currentRad) * radius);
            float y = (float)(Math.sin(currentRad) * radius);
            points.add(new Vector3f(x, y, 0f));
            currentRad += radsPerPoint;
        }

        return new Polygon(new Vector3f(), points);
    }

    /** Creates a square on the form of a polygon, centered at (x, y) with dimensions (width x height). */
    public static Polygon generateSquare(float x, float y, float width, float height) {
        List<Vector3f> points = new ArrayList<>(4);
        points.add(new Vector3f(x - width / 2, y - height / 2, 0f));
        points.add(new Vector3f(x + width / 2, y - height / 2, 0f));
        points.add(new Vector3f(x + width / 2, y + height / 2, 0f));
        points.add(new Vector3f(x - width / 2, y + height / 2, 0f));
        Vector3f center = new Vector3f(x, y, 0f);

        return new Polygon(center, points);
    }

}