package com.kbindiedev.verse.maps;

import com.kbindiedev.verse.math.shape.Polygon;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.List;
import java.util.stream.Collectors;

/** Describes a polygon that exists on a {@link MapObject}. */
public class MapObjectPolygon extends MapObjectContent {

    private Polygon polygon;

    public MapObjectPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public Polygon getPolygon() { return polygon; }

    public static MapObjectPolygon fromPoints(List<Vector2f> points) {
        List<Vector3f> vec3Points = points.stream().map(v -> new Vector3f(v.x, v.y, 0f)).collect(Collectors.toList());
        Polygon polygon = new Polygon(vec3Points);
        return new MapObjectPolygon(polygon);
    }

}