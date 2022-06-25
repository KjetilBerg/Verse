package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.PolygonCollider2D;
import com.kbindiedev.verse.ecs.components.RigidBody2D;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.ShapeDrawer;
import com.kbindiedev.verse.physics.Collision;
import com.kbindiedev.verse.physics.CollisionManifold;
import com.kbindiedev.verse.physics.Fixture;
import com.kbindiedev.verse.physics.PhysicsRigidBody;
import com.kbindiedev.verse.system.BiHashMap;
import org.joml.Vector3f;

import java.util.*;

/** Responsible for connecting an ECS space to a physics implementation. */
public class PhysicsManagerSystem extends ComponentSystem {

    private BiHashMap<RigidBody2D, PhysicsRigidBody> bodies;
    private BiHashMap<PolygonCollider2D, Fixture> fixtures;

    private EntityQuery query;

    public PhysicsManagerSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        bodies = new BiHashMap<>();
        fixtures = new BiHashMap<>();

        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(PolygonCollider2D.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        // TODO: dynamically register and unregister fixtures and bodies
        Iterator<Entity> entities = query.execute().iterator(); // TODO: lateStart() ????
        while (entities.hasNext()) {
            Entity entity = entities.next();

            List<PolygonCollider2D> colliders = entity.getComponents(PolygonCollider2D.class);
            RigidBody2D body = entity.getComponent(RigidBody2D.class);

            if (colliders == null) continue;

            registerCollidersForBody(body, colliders);
        }
    }

    // TODO: quite primitive for now
    @Override
    public void fixedUpdate(float dt) {

        updateAllTransforms();

        getSpace().getPhysicsEnvironment().simulate(dt);

        // TODO: retrieve transforms / rigidbody details from physics environment and apply to entities.

    }


    @Override
    public void update(float dt) { updateAllTransforms(); }

    // TODO: (temp?) fixedUpdate and update(). (move "update" to lateUpdate when supported?)
    private void updateAllTransforms() {
        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) {
            Entity entity = entities.next();
            List<PolygonCollider2D> colliders = entity.getComponents(PolygonCollider2D.class);
            for (PolygonCollider2D collider : colliders) {
                collider.polygon.translateTo(entity.getTransform().position);
            }
        }
    }

    @Override
    public void onDrawGizmos(RenderContext context) {

        ShapeDrawer drawer = getSpace().getShapeDrawer();
        Iterator<Entity> entities = query.execute().iterator();

        while (entities.hasNext()) {
            Entity entity = entities.next();

            PolygonCollider2D collider = entity.getComponent(PolygonCollider2D.class);
/*
            List<Vector3f> points = collider.polgyon.getPoints();
            List<Vector3f> pos = new ArrayList<>(points);

            ColoredPolygon p = new ColoredPolygon(new Vector3f(), pos, Pixel.RED);
            drawer.getLineBatch().drawConvexPolygon(p);
*/
            drawer.drawOutlineConvexPolygon(new Vector3f(), collider.polygon.getPoints(), Pixel.RED);

            Vector3f contact = new Vector3f(8f, 0f, 0f).add(collider.polygon.getPoints().get(0));
            Vector3f normal = new Vector3f(0f, -4f, 0f);
            //drawContactPoint(contact, normal);
        }

        List<Collision> collisions = getSpace().getPhysicsEnvironment().tempListCollisions();
        for (Collision collision : collisions) {
            CollisionManifold manifold = collision.getManifold();
            for (Vector3f contact : manifold.getContactPoints()) {
                Vector3f dir = new Vector3f(manifold.getNormal().mul(manifold.getDepth()));
                drawContactPoint(contact, dir);
            }

        }

    }

    private void drawContactPoint(Vector3f point, Vector3f normal) {
        ShapeDrawer drawer = getSpace().getShapeDrawer();
        drawer.drawPoint(point, 1f, Pixel.SOLID_WHITE);
        drawer.drawLine(point, normal, 0.5f, Pixel.SOLID_WHITE);
    }

    private PhysicsRigidBody newPhysicsBody(boolean dynamic) {
        return getSpace().getPhysicsEnvironment().createBody(dynamic);
    }

    /** Register colliders for a given body. if body == null, colliders are assumed to be static */
    private void registerCollidersForBody(RigidBody2D body, List<PolygonCollider2D> colliders) {
        System.out.println("registering body...");
        boolean dynamicBody = (body != null);
        PhysicsRigidBody prb = newPhysicsBody(dynamicBody);
        for (PolygonCollider2D collider : colliders) {
            if (collider.polygon == null) continue; // TODO: if change polygon during runtime
            Fixture fixture = prb.createFixture(collider.polygon);
            fixtures.put(collider, fixture);
        }
        if (body != null) bodies.put(body, prb);
    }

    /** Unregister a collider from the physics system. */
    private void unregisterCollider(PolygonCollider2D collider) {
        Fixture fixture = fixtures.remove(collider);
        if (fixture == null) return;

        PhysicsRigidBody prb = fixture.getBody();
        fixture.remove();

        if (prb.getFixtures().size() == 0 && !belongsToRigidBodyComponent(prb)) prb.remove();
    }

    /** Unregister a body and all its colliders form the physics system. */
    private void unregisterBody(RigidBody2D body) {
        PhysicsRigidBody prb = bodies.remove(body);
        if (prb == null) return;

        for (Fixture fixture : prb.getFixtures()) {
            PolygonCollider2D collider = fixtures.getForValue(fixture);
            unregisterCollider(collider);
        }

        prb.remove();
    }

    /** @return true if the given prb is associated with a RigidBody2D, false otherwise. */
    private boolean belongsToRigidBodyComponent(PhysicsRigidBody prb) {
        return bodies.getForValue(prb) != null;
    }

}