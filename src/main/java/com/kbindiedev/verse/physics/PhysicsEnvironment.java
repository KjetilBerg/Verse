package com.kbindiedev.verse.physics;

import com.kbindiedev.verse.math.MathTransform;
import com.kbindiedev.verse.math.helpers.Tuple;
import com.kbindiedev.verse.physics.collisions.CollisionUtil;
import com.kbindiedev.verse.system.FastList;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhysicsEnvironment {

    private static final IPhysicsCollisionListener BLANK_LISTENER = new IPhysicsCollisionListener() {
        @Override public void onCollision(Collision collision) {}
    };

    private IPhysicsCollisionListener listener;
    private List<Collision> collisionsThisIteration;
    private FastList<PhysicsRigidBody> dynamicBodies;
    private FastList<PhysicsRigidBody> staticBodies;

    public PhysicsEnvironment() {
        listener = BLANK_LISTENER;
        collisionsThisIteration = new ArrayList<>();
        dynamicBodies = new FastList<>();
        staticBodies = new FastList<>();
    }

    public void setListener(IPhysicsCollisionListener listener) { this.listener = listener; }

    public List<Collision> getActiveCollisions() { return collisionsThisIteration; }

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

        collisionsThisIteration.clear();
        collisionsThisIteration.addAll(doCollisionChecks());
        for (Collision c : collisionsThisIteration) listener.onCollision(c);

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


    // TODO: for now, quite simple. should use octree.
    /**
     * The broad phase.
     * The returned list is guaranteed to contain all colliding fixture pairs, however
     *      it may also contain fixture pairs that are not colliding.
     * @return a list of fixtures that may or may not be colliding.
     */
    private List<Tuple<Fixture, Fixture>> getPotentialCollisions() {
        List<Tuple<Fixture, Fixture>> potentialCollisions = new ArrayList<>();

        FastList<Fixture> primaryFixtures = new FastList<>();
        FastList<Fixture> allFixtures = new FastList<>();

        for (PhysicsRigidBody b : dynamicBodies) {
            primaryFixtures.addAll(b.getFixtures());
            allFixtures.addAll(b.getFixtures());
        }
        for (PhysicsRigidBody b : staticBodies) {
            allFixtures.addAll(b.getFixtures());
        }

        for (Fixture fixture1 : primaryFixtures) {
            for (Fixture fixture2 : allFixtures) {
                //if (fixture1 == fixture2) continue;
                if (fixture1.getBody() == fixture2.getBody()) continue;

                potentialCollisions.add(new Tuple<>(fixture1, fixture2));
            }
        }

        return potentialCollisions;
    }

    /**
     * Narrow phase.
     * @return a list of all collisions.
     */
    private List<Collision> doCollisionChecks() {
        List<Collision> collisions = new ArrayList<>();

        List<Tuple<Fixture, Fixture>> potentialCollisions = getPotentialCollisions();
        for (Tuple<Fixture, Fixture> pCollision : potentialCollisions) {
            Fixture fixture1 = pCollision.getFirst();
            Fixture fixture2 = pCollision.getSecond();

            CollisionManifold manifold = CollisionUtil.checkCollisions(fixture1, fixture2);
            if (manifold == null) continue;

            Collision collision = new Collision(fixture1, fixture2, manifold);
            collisions.add(collision);
        }

        return collisions;
    }

    /** Perform the simplest static resolution I can think of */
    private void simplestStaticResolution(int maxIterations) {

        HashMap<PhysicsRigidBody, List<Collision>> singleMap = new HashMap<>();

        for (int i = 0; i < maxIterations; ++i) {

            List<Collision> collisions = doCollisionChecks();
            collisions.removeIf(c -> c.getFixture1().isSensor() || c.getFixture2().isSensor());
            if (collisions.size() == 0) break;

            singleMap.clear();

            for (Collision collision : collisions) {
                PhysicsRigidBody body = collision.getFixture1().getBody();
                if (!singleMap.containsKey(body)) singleMap.put(body, new ArrayList<>());
                singleMap.get(body).add(collision);
            }

            for (Map.Entry<PhysicsRigidBody, List<Collision>> e : singleMap.entrySet()) {

                PhysicsRigidBody body = e.getKey();
                List<Collision> c = e.getValue();

                c.sort((c1, c2) -> {
                    CollisionManifold m1 = c1.getManifold(), m2 = c2.getManifold();
                    if (m1.getDepth() > m2.getDepth()) return -1;
                    if (m1.getDepth() < m2.getDepth()) return 1;
                    float l1 = new Vector3f(body.getPosition()).sub(averageContactPoints(m1)).lengthSquared();
                    float l2 = new Vector3f(body.getPosition()).sub(averageContactPoints(m2)).lengthSquared();
                    if (l1 < l2) return -1;
                    if (l1 > l2) return 1;
                    return 0;
                });

                CollisionManifold prioritizedCollision = c.get(0).getManifold();

                Vector3f adjustment = new Vector3f(prioritizedCollision.getNormal()).mul(prioritizedCollision.getDepth());
                body.applyMovement(adjustment);

            }

        }

    }

    private Vector3f averageContactPoints(CollisionManifold manifold) {
        Vector3f avg = new Vector3f();
        for (Vector3f p : manifold.getContactPoints()) avg.add(p);
        avg.div(manifold.getContactPoints().size());
        return avg;
    }

    /*
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
    */

}