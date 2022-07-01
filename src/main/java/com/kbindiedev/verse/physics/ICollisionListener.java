package com.kbindiedev.verse.physics;

/** A collision listener for Physics. */
public interface ICollisionListener {

    void onCollision(CollisionManifold manifold);

}