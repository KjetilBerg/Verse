package com.kbindiedev.verse.physics;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.physics.collisions.CollisionUtil;
import com.kbindiedev.verse.system.FastList;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        for (PhysicsRigidBody prb : dynamicBodies) prb.tick(dt);

        simplestStaticResolution(5);

        /*
        List<Collision> collisions = findAllCollisions();

        HashMap<PhysicsRigidBody, List<Vector3f>> velocityAdjustments = new HashMap<>();

        for (Collision collision : collisions) {
            Vector3f velAdjust = new Vector3f(collision.getManifold().getNormal()).mul(collision.getManifold().getDepth());

            if (!velocityAdjustments.containsKey(collision.getBody1())) velocityAdjustments.put(collision.getBody1(), new ArrayList<>());

            velocityAdjustments.get(collision.getBody1()).add(velAdjust);


            //float a = 1f / dt;
            //velAdjust.mul(a);
            //collision.getBody1().applyImpulse(velAdjust);
            //collision.getBody1().getTransform().getPosition().add(velAdjust.mul(dt));
        }

        for (Map.Entry<PhysicsRigidBody, List<Vector3f>> e : velocityAdjustments.entrySet()) {
            Vector3f avg = new Vector3f();
            for (Vector3f a : e.getValue()) avg.add(a);
            avg.div(e.getValue().size());

            avg.mul(2f / dt);

            e.getKey().applyImpulse(avg);
        }

        for (PhysicsRigidBody prb : dynamicBodies) prb.tick(dt);
        */

    }

    /** Perform the simplest static resolution I can think of */
    private void simplestStaticResolution(int maxIterations) {
        FastList<PhysicsRigidBody> allBodies = new FastList<>();
        allBodies.addAll(dynamicBodies);
        allBodies.addAll(staticBodies);


        for (int m = 0; m < maxIterations; ++m) {
            boolean didCollide = false;
            for (PhysicsRigidBody body1 : dynamicBodies) {

                List<CollisionManifold> allManifolds = new ArrayList<>();

                // find all collisions with body1
                for (PhysicsRigidBody body2 : allBodies) {
                    if (body1 == body2) continue;

                    CollisionManifold manifold = CollisionUtil.checkCollisions(body1, body2);
                    if (manifold == null) continue;

                    allManifolds.add(manifold);

                }

                if (allManifolds.size() == 0) continue;
                didCollide = true;

                // sort by biggest depth, then closest average contact point
                allManifolds.sort((m1, m2) -> {
                    if (m1.getDepth() > m2.getDepth()) return -1;
                    if (m1.getDepth() < m2.getDepth()) return 1;
                    float l1 = new Vector3f(body1.getTransform().getPosition()).sub(averageContactPoints(m1)).lengthSquared();
                    float l2 = new Vector3f(body1.getTransform().getPosition()).sub(averageContactPoints(m2)).lengthSquared();
                    if (l1 < l2) return -1;
                    if (l1 > l2) return 1;
                    return 0;
                });

                // consider first point only (do not get caught on corners)
                Vector3f adjustment = new Vector3f(allManifolds.get(0).getNormal()).mul(allManifolds.get(0).getDepth());

                body1.getTransform().getPosition().add(adjustment);
                body1.getFixtures().forEach(f -> f.getShape().translateTo(body1.getTransform().getPosition())); // TODO TEMP
            }
            if (!didCollide) break;
        }
    }

    private Vector3f averageContactPoints(CollisionManifold manifold) {
        Vector3f avg = new Vector3f();
        for (Vector3f p : manifold.getContactPoints()) avg.add(p);
        avg.div(manifold.getContactPoints().size());
        return avg;
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