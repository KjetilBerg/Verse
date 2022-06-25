package com.kbindiedev.verse.physics;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.physics.collisions.CollisionUtil;
import com.kbindiedev.verse.system.FastList;

import java.util.ArrayList;
import java.util.List;

public class PhysicsEnvironment {

    private FastList<PhysicsRigidBody> dynamicBodies;
    private FastList<PhysicsRigidBody> staticBodies;

    public PhysicsEnvironment() {
        dynamicBodies = new FastList<>();
        staticBodies = new FastList<>();
    }

    public PhysicsRigidBody createBody(boolean dynamic) { return createBody(new MathTransform(), dynamic); }
    public PhysicsRigidBody createBody(MathTransform transform, boolean dynamic) {
        PhysicsRigidBody body = new PhysicsRigidBody(this, transform, dynamic);
        if (dynamic) dynamicBodies.add(body); else staticBodies.add(body);
        return body;
    }

    public boolean removeBody(PhysicsRigidBody body) { return dynamicBodies.remove(body) || staticBodies.remove(body); }

    /** Perform a single iteration of this environment. */
    public void simulate(float dt) {
    }

    private List<Collision> findAllCollisions() {
        // TODO: for now very primitive. no octree, just compare everything to everything else
        FastList<PhysicsRigidBody> allBodies = new FastList<>();
        allBodies.addAll(dynamicBodies);
        allBodies.addAll(staticBodies);

        List<Collision> collisions = new ArrayList<>();

        for (PhysicsRigidBody body1 : dynamicBodies) {
            for (PhysicsRigidBody body2 : allBodies) {
                if (body1 == body2) continue;

                CollisionManifold manifold = CollisionUtil.checkCollisions(body1, body2);
                if (manifold != null) collisions.add(new Collision(body1, body2, manifold)); // TODO: better checks ??
            }
        }

        return collisions;
    }

    // TODO TEMP
    public List<Collision> tempListCollisions() {
        return findAllCollisions();
    }

}