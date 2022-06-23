package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.gfx.strategy.index.*;
import com.kbindiedev.verse.gfx.strategy.providers.ColoredPolygon;
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

    public void drawOutlineConvexPolygon(Pixel color, List<Vector3f> points) {
        ColoredPolygon p = new ColoredPolygon(new Vector3f(), points, color);
        lineBatch.drawConvexPolygon(p);
    }

    public void drawOutlineSquare(Pixel color, float x, float y, float width, float height) {
        List<Vector3f> arr = new ArrayList<>(4);
        arr.add(new Vector3f(x, y, 0f));
        arr.add(new Vector3f(x+width, y, 0f));
        arr.add(new Vector3f(x+width, y+height, 0f));
        arr.add(new Vector3f(x, y+height, 0f));
        drawOutlineConvexPolygon(color, arr);
    }

    /*
    public void drawPoint(Pixel color, float x, float y, float radius) {
        PolygonIndexMode trianglesOnly = new PolygonIndexMode(PolygonTriangleMode.NORMAL, PolygonLineMode.NONE, PointMode.NONE);
        StaticIndicesProvider indices = StaticIndicesProvider.newByPolygonShape(POINT_EDGES_COUNT, trianglesOnly);

        ColoredPolygon polygon = ColoredPolygonGenerator.generatePolygon()
    }*/

}
