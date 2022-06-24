package com.kbindiedev.verse.physics;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.math.shape.Polygon;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * A structure describing a set of fixtures.
 * Different from {@link com.kbindiedev.verse.ecs.components.RigidBody2D} (that describes entity properties). // TODO: and RigidBody3D
 */
public class PhysicsRigidBody {

    private MathTransform transform;
    private Vector3f forceVector, velocity, acceleration;
    private List<Fixture> fixtures;

    public PhysicsRigidBody(MathTransform transform) {
        this.transform = transform;
        forceVector = new Vector3f(); velocity = new Vector3f(); acceleration = new Vector3f();
        fixtures = new ArrayList<>();
    }

    public MathTransform getTransform() { return transform; }
    public List<Fixture> getFixtures() { return fixtures; }

    public Vector3f getForceVector() { return forceVector; }
    public void setForceVector(Vector3f forceVector) { this.forceVector = forceVector; }

    public Vector3f getVelocity() { return velocity; }
    public void setVelocity(Vector3f velocity) { this.velocity = velocity; }

    public Vector3f getAcceleration() { return acceleration; }
    public void setAcceleration(Vector3f acceleration) { this.acceleration = acceleration; }

    public Fixture createFixture(Polygon polygon) { return createFixture(new MathTransform(polygon.getCenter(), polygon.getScale(), polygon.getRotation()), polygon); }
    public Fixture createFixture(MathTransform transform, Polygon polygon) {
        Fixture fixture = new Fixture(transform, polygon);
        fixtures.add(fixture);
        return fixture;
    }

}