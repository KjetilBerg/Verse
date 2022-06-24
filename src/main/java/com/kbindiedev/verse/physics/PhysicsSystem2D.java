package com.kbindiedev.verse.physics;

// TODO FUTURE: 3D.

import com.kbindiedev.verse.math.MathTransform;

import java.util.ArrayList;
import java.util.List;

/** The root of all physics, for now. */
public class PhysicsSystem2D {

    private List<PhysicsRigidBody> bodies;

    public void PhysicsSystem2D() {
        bodies = new ArrayList<>();
    }

    public PhysicsRigidBody createBody() { return createBody(new MathTransform()); }
    public PhysicsRigidBody createBody(MathTransform transform) {
        PhysicsRigidBody body = new PhysicsRigidBody(transform);
        bodies.add(body);
        return body;
    }

}