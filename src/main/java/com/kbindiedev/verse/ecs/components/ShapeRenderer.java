package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.math.shape.Polygon;

/** Render a particular shape. */
public class ShapeRenderer implements IComponent {

    public Mesh.RenderMode mode         = Mesh.RenderMode.TRIANGLES;
    public Polygon shape                = null; // TODO

}