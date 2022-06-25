package com.kbindiedev.verse.physics;

/** Describes a collision between two {@link PhysicsRigidBody}. */
public class Collision {

    private PhysicsRigidBody body1;
    private PhysicsRigidBody body2;
    private CollisionManifold manifold;

    public Collision(PhysicsRigidBody body1, PhysicsRigidBody body2, CollisionManifold manifold) {
        this.body1 = body1;
        this.body2 = body2;
        this.manifold = manifold;
    }

    public PhysicsRigidBody getBody1() { return body1; }
    public PhysicsRigidBody getBody2() { return body2; }
    public CollisionManifold getManifold() { return manifold; }

}