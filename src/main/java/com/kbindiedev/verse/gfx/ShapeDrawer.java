package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.gfx.strategy.index.*;
import com.kbindiedev.verse.gfx.strategy.providers.ColoredPolygon;
import com.kbindiedev.verse.math.helpers.PolygonMaker;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/** A simple interface to draw shapes easily. */
public class ShapeDrawer {

    private static final int POINT_EDGES_COUNT = 4;

    private PolygonBatch triangleBatch, lineBatch, pointBatch;

    public ShapeDrawer(PolygonBatch triangleBatch, PolygonBatch lineBatch, PolygonBatch pointBatch) {
        this.triangleBatch = triangleBatch;
        this.lineBatch = lineBatch;
        this.pointBatch = pointBatch;
    }

    public PolygonBatch getTriangleBatch() { return triangleBatch; }
    public PolygonBatch getLineBatch() { return lineBatch; }
    public PolygonBatch getPointBatch() { return pointBatch; }

    public void setProjectionMatrix(Matrix4f projection) {
        triangleBatch.setProjectionMatrix(projection);
        lineBatch.setProjectionMatrix(projection);
        pointBatch.setProjectionMatrix(projection);
    }

    public void setViewMatrix(Matrix4f view) {
        triangleBatch.setViewMatrix(view);
        lineBatch.setViewMatrix(view);
        pointBatch.setViewMatrix(view);
    }

    public void begin() {
        triangleBatch.begin();
        lineBatch.begin();
        pointBatch.begin();
    }

    public void flush() {
        triangleBatch.flush();
        lineBatch.flush();
        pointBatch.flush();
    }

    public void end() {
        triangleBatch.end();
        lineBatch.end();
        pointBatch.end();
    }

    public void drawOutlineConvexPolygon(Vector3f position, List<Vector3f> points, Pixel color) {
        ColoredPolygon p = new ColoredPolygon(new Vector3f(), points, color);
        p.translateTo(position);
        lineBatch.drawConvexPolygon(p);
    }

    public void drawOutlineSquare(Vector3f position, float width, float height, Pixel color) {
        ColoredPolygon p = ColoredPolygon.fromPolygon(PolygonMaker.generateRectangle(position.x, position.y, width, height), color);
        //p.translateTo(position); // already covered by generateSquare
        lineBatch.drawConvexPolygon(p);
    }

    /** Draws a circle with the given radius, in the given color. */
    public void drawPoint(Vector3f position, float radius, Pixel color) { drawPoint(position, radius, 8, color); }

    /** Draws a circle with the given radius and number of edges, in the given color. */
    public void drawPoint(Vector3f position, float radius, int edges, Pixel color) {
        ColoredPolygon p = ColoredPolygon.fromPolygon(PolygonMaker.generatePolygon(edges, radius), color);
        p.translateTo(position);
        triangleBatch.drawConvexPolygon(p);
    }

    public void drawLineTo(Vector3f source, Vector3f destination, float thickness, Pixel color) {
        ColoredPolygon p = ColoredPolygon.fromPolygon(PolygonMaker.generateRectangleFromTo(source, destination, thickness), color);
        triangleBatch.drawConvexPolygon(p);
    }

    public void drawLine(Vector3f source, Vector3f direction, float thickness, Pixel color) {
        drawLineTo(source, new Vector3f(source).add(direction), thickness, color);
    }

}