package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.math.shape.Polygon;
import com.kbindiedev.verse.system.ISerializable;

import java.io.InputStream;
import java.io.OutputStream;

/** Describes a 2D polygon collider. The shape is related to its entity's transform. */
public class PolygonCollider2D implements IComponent, ISerializable {

    public Polygon polygon;     // the shape
    public boolean isTrigger;   // true = does not do collision resolution

    // TODO: empty for now
    @Override
    public void serialize(OutputStream stream) {

    }

    @Override
    public void deserialize(InputStream stream) {

    }

}