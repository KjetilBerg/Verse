package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.math.shape.Polygon;

/** Describes a 2D polygon collider. The shape is related to its entity's transform. */
public class PolygonCollider2D implements IComponent {

    public Polygon polygon;     // the shape
    public boolean isTrigger;   // true = does not do collision resolution

}
