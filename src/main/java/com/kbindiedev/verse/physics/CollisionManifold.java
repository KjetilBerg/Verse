package com.kbindiedev.verse.physics;

import org.joml.Vector3f;

import java.util.List;

/** Describes contact points, a normal and depth for a particular collision. */
public class CollisionManifold {

    private List<Vector3f> contactPoints;
    private Vector3f normal;
    private float depth;

    public CollisionManifold(List<Vector3f> contactPoints, Vector3f normal, float depth) {
        this.contactPoints = contactPoints;
        this.normal = normal;
        this.depth = depth;
    }

    public List<Vector3f> getContactPoints() { return contactPoints; }
    public Vector3f getNormal() { return normal; }
    public float getDepth() { return depth; }

}