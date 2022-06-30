package com.kbindiedev.verse.physics;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.math.shape.Polygon;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    /** Move this fixture by the given amount. */
    public void move(Vector3f movement) { shape.translate(movement); }
    /** Move this fixture to the given position. */
    public void translateTo(Vector3f position) { shape.translateTo(position); }
    /** Scale this fixture to the given scale. */
    public void scaleTo(Vector3f scale) { shape.scaleTo(scale); }
    /** Rotate this fixture to the given quaternion. */
    public void rotateTo(Quaternionf rotation) { shape.rotateTo(rotation); }

    /** Remove this fixture from the body that it belongs to. */
    public void remove() {
        body.removeFixture(this);
    }

}
