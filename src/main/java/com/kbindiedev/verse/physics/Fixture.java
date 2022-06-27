package com.kbindiedev.verse.physics;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.math.shape.Polygon;

public class Fixture {

    private PhysicsRigidBody body;
    private MathTransform transform;
    private Polygon shape;

    protected Fixture(PhysicsRigidBody body, MathTransform transform, Polygon shape) {
        this.body = body;
        this.transform = transform;
        this.shape = shape;
    }

    public PhysicsRigidBody getBody() { return body; }

    public MathTransform getTransform() { return transform; }

    public Polygon getShape() { return shape; }

    /** Remove this fixture from the body that it belongs to. */
    public void remove() {
        body.removeFixture(this);
    }

}
