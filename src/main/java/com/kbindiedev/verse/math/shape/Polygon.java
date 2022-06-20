package com.kbindiedev.verse.math.shape;

import com.kbindiedev.verse.math.helpers.Line;
import com.kbindiedev.verse.math.helpers.Point2Df;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** A collection of points and lines. */
public class Polygon {

    private Point2Df center;
    private List<Point2Df> points;
    private List<Line<Point2Df>> lines;

    public Polygon(Point2Df center) {
        this.center = center;
        points = new ArrayList<>();
        lines = new ArrayList<>();
    }

    public Point2Df getCenter() { return center; }
    public void setCenter(Point2Df center) { this.center = center; }

    public void translate(Vector2f vector) {
        center.translate(vector);
        for (Point2Df point : points) point.translate(vector);
    }

    public void translateTo(Vector2f vector) {
        float ox = vector.x, oy = vector.y;
        vector.x -= center.getX();
        vector.y -= center.getY();
        translate(vector);
        vector.x = ox; vector.y = oy;
    }

    public void addPoint(Point2Df point) {
        points.add(point);
        if (points.size() < 2) return;

        Point2Df prev = points.get(points.size() - 2), next = points.get(0);
        if (lines.size() > 0) lines.remove(lines.get(lines.size() - 1));
        lines.add(new Line<>(prev, point));
        lines.add(new Line<>(point, next));
    }

    public Iterator<Point2Df> iteratePoints() { return points.iterator(); }
    public Iterator<Line<Point2Df>> iterateLines() { return lines.iterator(); }

}