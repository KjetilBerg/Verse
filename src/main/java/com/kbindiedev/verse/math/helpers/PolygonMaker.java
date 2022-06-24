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

    /** Creates a rectangle on the form of a polygon, centered at (x, y) with dimensions (width x height). */
    public static Polygon generateRectangle(float x, float y, float width, float height) {
        List<Vector3f> points = new ArrayList<>(4);
        points.add(new Vector3f(x - width / 2, y - height / 2, 0f));
        points.add(new Vector3f(x + width / 2, y - height / 2, 0f));
        points.add(new Vector3f(x + width / 2, y + height / 2, 0f));
        points.add(new Vector3f(x - width / 2, y + height / 2, 0f));
        Vector3f center = new Vector3f(x, y, 0f);

        return new Polygon(center, points);
    }

    /** Create a rectangle that goes from source to destination with a given thickness. */
    public static Polygon generateRectangleFromTo(Vector3f source, Vector3f destination, float thickness) {
        return generateRectangleFromTo(source, destination, thickness, new Vector3f(0f, 0f, 1f));
    }

    /** Create a rectangle that goes from source to destination with a given thickness, facing the given forward vector. */
    public static Polygon generateRectangleFromTo(Vector3f source, Vector3f destination, float thickness, Vector3f forward) {

        Vector3f direction = new Vector3f(destination).sub(source);
        Vector3f np = new Vector3f(forward).cross(direction).normalize();

        Vector3f q = new Vector3f(np).mul(thickness / 2);

        // Polygon lines form a 2D shape. To create a "cubeFromTo", we need something other than Polygon.
        ArrayList<Vector3f> points = new ArrayList<>(4);
        points.add(new Vector3f(source).sub(q));
        points.add(new Vector3f(source).add(q));
        points.add(new Vector3f(destination).add(q));
        points.add(new Vector3f(destination).sub(q));
        Vector3f center = new Vector3f(source).add(destination).div(2);

        return new Polygon(center, points);

        // TODO testing: does the code above lose dimension of forward vector, or is it maintained (future rotations)?.
        //      code below works, but is slower.
        /*
        Vector3f center = new Vector3f(source).add(destination).div(2);
        Vector3f direction = new Vector3f(destination).sub(source);

        Polygon p = generateRectangle(0, 0, thickness, direction.length());
        Quaternionf rot = new Quaternionf().rotateTo(new Vector3f(0f, 1f, 0f), direction);
        p.rotateTo(rot);
        p.translateTo(center);
        return p;
        */
    }

}