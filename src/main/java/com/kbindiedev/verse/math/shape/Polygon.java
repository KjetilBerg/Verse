package com.kbindiedev.verse.math.shape;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.math.MathUtil;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A collection of points that form lines based by the order of the points. */
public class Polygon {

    protected MathTransform transform;
    protected List<Vector3f> points;

    public Polygon() { this(new MathTransform(), new ArrayList<>()); }
    public Polygon(List<Vector3f> points) { this(MathUtil.findCenter(points), points); }
    public Polygon(Vector3f center, List<Vector3f> points) { this(new MathTransform(center), points); }
    public Polygon(MathTransform transform, List<Vector3f> points) {
        this.transform = transform;
        this.points = points;
    }

    public Vector3f getCenter() { return transform.getPosition(); }
    public Vector3f getScale() { return transform.getScale(); }
    public Quaternionf getRotation() { return transform.getRotation(); }

    protected MathTransform getTransform() { return transform; }

    public void addPoint(Vector3f point) {
        points.add(point);
    }

    /** Set the position of a single vertex. */
    public void setPointPosition(int index, Vector3f position) { points.get(index).set(position); }

    public List<Vector3f> getPoints() { return points; }

    /** Assume that this polygon's center is the given center. existing vertices positions will not be changed. */
    public void assumeCenter(Vector3f center) { this.transform.setPosition(center); }

    /** Set this polygon's center by translating all points so that its center is at the given destination. */
    public void translateTo(Vector3f dest) {
        Vector3f by = transform.distanceTo(dest);
        translate(by);
    }

    /** Set this polygon's final scale by a given vector3f. */
    public void scaleTo(Vector3f scale) {
        Vector3f s = transform.scaleTo(scale);
        Vector3f center = transform.getPosition();
        for (Vector3f p : points) p.sub(center).mul(s).add(center);
    }

    /** Set this polygon's rotation by a given quaternion. */
    public void rotateTo(Quaternionf rotation) {
        Quaternionf rot = transform.rotateTo(rotation);
        Vector3f center = transform.getPosition();
        for (Vector3f p : points) p.sub(center).rotate(rot).add(center);
    }

    /** Translate center and all points by the given vector3f. */
    public void translate(Vector3f by) {
        transform.translate(by);
        for (Vector3f vec : points) vec.add(by);
    }

}