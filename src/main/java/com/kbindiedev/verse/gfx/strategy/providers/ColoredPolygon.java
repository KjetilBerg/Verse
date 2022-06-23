package com.kbindiedev.verse.gfx.strategy.providers;

import com.kbindiedev.verse.gfx.IndexDataBuffer;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.Shader;
import com.kbindiedev.verse.gfx.strategy.attributes.VertexAttributes;
import com.kbindiedev.verse.gfx.strategy.index.*;
import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.math.MathUtil;
import com.kbindiedev.verse.math.shape.Polygon;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/** An IMeshDataProvider that provides POS_3D_AND_COLOR vertices, with possibilities of triangle, line and point indices. */
public class ColoredPolygon extends Polygon implements IMeshDataProvider {

    private static final CollectiveIndexGenerator DEFAULT_PENTAGON_UP;
    private static final CollectiveIndexGenerator DEFAULT_QUADRILATERAL_DOWN;

    static {
        DEFAULT_PENTAGON_UP = new CollectiveIndexGenerator();
        DEFAULT_PENTAGON_UP.set(Mesh.RenderMode.TRIANGLES, TriangleZeroAsFanGenerator.INSTANCE);
        DEFAULT_PENTAGON_UP.set(Mesh.RenderMode.LINES, LineIndexSkipFirstGenerator.INSTANCE);
        DEFAULT_PENTAGON_UP.set(Mesh.RenderMode.POINTS, PointIndexSkipFirstGenerator.INSTANCE);

        DEFAULT_QUADRILATERAL_DOWN = new CollectiveIndexGenerator();
        DEFAULT_QUADRILATERAL_DOWN.set(Mesh.RenderMode.TRIANGLES, TriangleSkipZeroFirstToAllGenerator.INSTANCE);
        DEFAULT_QUADRILATERAL_DOWN.set(Mesh.RenderMode.LINES, LineIndexSkipFirstGenerator.INSTANCE);
        DEFAULT_QUADRILATERAL_DOWN.set(Mesh.RenderMode.POINTS, PointIndexSkipFirstGenerator.INSTANCE);
    }

    private static CollectiveIndexGenerator pickPredeterminedIndexGenerator(int numVertices) {
        if (numVertices >= 5) return DEFAULT_PENTAGON_UP; else return DEFAULT_QUADRILATERAL_DOWN;
    }

    private VertexAttributes attributes;

    private List<Pixel> colors;
    private Pixel defaultColor;

    private CollectiveIndexGenerator customIndexGenerator;

    /** Create a new ColoredPolygon by calculating the center of given points. */
    public ColoredPolygon(List<Vector3f> points, Pixel color) { this(MathUtil.findCenter(points), points, color); }

    public ColoredPolygon(Vector3f center, List<Vector3f> points, Pixel color) { this(center, points, color, null); }
    public ColoredPolygon(Vector3f center, List<Vector3f> points, Pixel color, CollectiveIndexGenerator customIndexGenerator) {
        this(new MathTransform(center), points, color, customIndexGenerator);
    }

    public ColoredPolygon(MathTransform transform, List<Vector3f> points, Pixel color) { this(transform, points, color, null); }
    public ColoredPolygon(MathTransform transform, List<Vector3f> points, Pixel color, CollectiveIndexGenerator customIndexGenerator) {
        super(transform, points);

        attributes = Shader.PredefinedAttributes.POS_3D_AND_COLOR;

        defaultColor = color;
        colors = new ArrayList<>(this.points.size());
        for (int i = 0, n = this.points.size(); i < n; ++i) colors.add(defaultColor);

        this.customIndexGenerator = customIndexGenerator;
    }

    private CollectiveIndexGenerator getIndexGenerator() {
        if (customIndexGenerator == null) return pickPredeterminedIndexGenerator(points.size());
        return customIndexGenerator;
    }

    @Override
    public VertexAttributes getVertexAttributes() { return attributes; }
    @Override
    public int getNumVertices() { return points.size() + 1; }
    @Override
    public int getNumIndexEntries(Mesh.RenderMode mode) { return getIndexGenerator().getNumIndexEntries(getNumVertices(), mode); }

    @Override
    public void feed(ByteBuffer vertexData, IndexDataBuffer indexDataBuffer, Mesh.RenderMode mode) {
        feedSingleVertex(vertexData, getCenter(), defaultColor);

        for (int i = 0; i < points.size(); ++i) {
            feedSingleVertex(vertexData, points.get(i), colors.get(i));
        }

        CollectiveIndexGenerator indexGenerator = getIndexGenerator();
        indexGenerator.feed(indexDataBuffer, getNumVertices(), mode);
    }

    private void feedSingleVertex(ByteBuffer vertexData, Vector3f point, Pixel color) {
        vertexData.putFloat(point.x);
        vertexData.putFloat(point.y);
        vertexData.putFloat(point.z);
        vertexData.put((byte)color.r());    // pixel.packed() does not work. needs to be inverted? Wrong regardless of Byte order
        vertexData.put((byte)color.g());
        vertexData.put((byte)color.b());
        vertexData.put((byte)color.a());
    }

    @Override
    public void addPoint(Vector3f point) {
        super.addPoint(point);
        colors.add(defaultColor);
    }

    /** Set the color of a single vertex. */
    public void setColor(int index, Pixel color) { colors.set(index, color); }



    /** Create a ColoredPolygon by copying an existing polygon. color must be supplied. */
    public static ColoredPolygon fromPolygon(Polygon polygon, Pixel color) {
        CollectiveIndexGenerator customIndexGenerator = null;
        if (polygon instanceof ColoredPolygon) customIndexGenerator = ((ColoredPolygon)polygon).customIndexGenerator;
        return fromPolygon(polygon, color, customIndexGenerator);
    }

    /** Create a ColoredPolygon by copying an existing polygon. color must be supplied. indexGenerator may be supplied. */
    public static ColoredPolygon fromPolygon(Polygon polygon, Pixel color, CollectiveIndexGenerator customIndexGenerator) {
        MathTransform transform = new MathTransform(new Vector3f(polygon.getCenter()), new Vector3f(polygon.getScale()), new Quaternionf(polygon.getRotation()));
        return new ColoredPolygon(transform, polygon.getPoints(), color, customIndexGenerator);
    }

}