package com.kbindiedev.verse.physics;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.math.shape.Polygon;

//TODO FUTURE: consider LocalMathTransform (or something) so that fixtures could be reused (?)

public class Fixture {

    private MathTransform transform;
    private Polygon shape;

    protected Fixture(MathTransform transform, Polygon shape) {
        this.transform = transform;
        this.shape = shape;
    }

    public MathTransform getTransform() { return transform; }

    public Polygon getShape() { return shape; }

}
