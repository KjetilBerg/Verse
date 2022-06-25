package com.kbindiedev.verse.physics;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.math.shape.Polygon;
import com.kbindiedev.verse.system.FastList;
import org.joml.Vector3f;

import java.util.List;

/**
 * A structure describing a set of fixtures.
 * Different from {@link com.kbindiedev.verse.ecs.components.RigidBody2D} (that describes entity properties). // TODO: and RigidBody3D
 */
public class PhysicsRigidBody {

    private PhysicsEnvironment environment;
    private MathTransform transform;
    private FastList<Fixture> fixtures;

    private Vector3f force, velocity, acceleration;
    private float mass, inverseMass;
    private float restitution;
    private boolean dynamic;

    public PhysicsRigidBody(PhysicsEnvironment environment, MathTransform transform, boolean dynamic) {
        this.environment = environment;
        this.transform = transform;
        force = new Vector3f(); velocity = new Vector3f(); acceleration = new Vector3f();
        fixtures = new FastList<>();

        // TODO:
        mass = 1f;
        inverseMass = 1f / mass;
        restitution = 1f;
        this.dynamic = dynamic;
    }

    public MathTransform getTransform() { return transform; }
    public List<Fixture> getFixtures() { return fixtures.asList(); }

    public Vector3f getForceVector() { return force; }
    public void setForceVector(Vector3f force) { this.force = force; }

    public Vector3f getVelocity() { return velocity; }
    public void setVelocity(Vector3f velocity) { this.velocity = velocity; }

    public Vector3f getAcceleration() { return acceleration; }
    public void setAcceleration(Vector3f acceleration) { this.acceleration = acceleration; }

    public float getMass() { return mass; }
    public void setMass(float mass) { this.mass = mass; }

    public float getInverseMass() { return inverseMass; } // TODO auto calc???
    //public void setInverseMass(float inverseMass) { this.inverseMass = inverseMass; }

    public float getRestitution() { return restitution; }
    public void setRestitution(float restitution) { this.restitution = restitution; }

    public boolean isDynamic() { return dynamic; }

    public Fixture createFixture(Polygon polygon) { return createFixture(new MathTransform(polygon.getCenter(), polygon.getScale(), polygon.getRotation()), polygon); }
    public Fixture createFixture(MathTransform transform, Polygon polygon) {
        Fixture fixture = new Fixture(this, transform, polygon);
        fixtures.add(fixture);
        return fixture;
    }

    public boolean removeFixture(Fixture fixture) { return fixtures.remove(fixture); }

    /** Remove this body from the PhysicsEnvironment that i belong to. */
    public void remove() {
        environment.removeBody(this);
    }

}