package com.kbindiedev.verse.physics;

/** Describes a collision between two {@link PhysicsRigidBody}. */
public class Collision {

    private Fixture fixture1;
    private Fixture fixture2;
    private CollisionManifold manifold;

    public Collision(Fixture fixture1, Fixture fixture2, CollisionManifold manifold) {
        this.fixture1 = fixture1;
        this.fixture2 = fixture2;
        this.manifold = manifold;
    }

    public Fixture getFixture1() { return fixture1; }
    public Fixture getFixture2() { return fixture2; }
    public CollisionManifold getManifold() { return manifold; }

}