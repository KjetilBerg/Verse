package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.PolygonCollider2D;
import com.kbindiedev.verse.ecs.components.RigidBody2D;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.ShapeDrawer;
import com.kbindiedev.verse.physics.collisions.CollisionUtils;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Collectors;

public class PolygonCollider2DSystem extends ComponentSystem {

    private EntityQuery query;

    public PolygonCollider2DSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(PolygonCollider2D.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
    }

    // TODO: quite primitive for now
    @Override
    public void fixedUpdate(float dt) {

        Iterator<Entity> entities = query.execute().iterator();

        HashMap<RigidBody2D, List<PolygonCollider2D>> colliderMap = new HashMap<>();
        HashMap<RigidBody2D, Transform> transformMap = new HashMap<>();

        while(entities.hasNext()) {
            Entity entity = entities.next();

            entity.getComponent(RigidBody2D.class);

            RigidBody2D body = entity.getComponent(RigidBody2D.class); // nullable
            if (!colliderMap.containsKey(body)) colliderMap.put(body, new ArrayList<>());

            Transform transform = entity.getComponent(Transform.class);
            if (body != null && transform != null) transformMap.put(body, transform);

            List<PolygonCollider2D> colliders = entity.getComponents(PolygonCollider2D.class);
            colliderMap.get(body).addAll(colliders);

            // TODO TEMP:
            for (PolygonCollider2D collider : colliders) {
                collider.polygon.translateTo(entity.getTransform().position);
            }
        }

        // do collisions
        List<RigidBody2D> bodies = colliderMap.keySet().stream().filter(Objects::nonNull).collect(Collectors.toList());
        for (RigidBody2D body : bodies) {
            Transform transform = transformMap.get(body);
            if (transform == null) continue;

            List<PolygonCollider2D> myColliders = colliderMap.get(body);
            List<PolygonCollider2D> possibleColliders = new ArrayList<>();
            for (Map.Entry<RigidBody2D, List<PolygonCollider2D>> e : colliderMap.entrySet()) {
                if (e.getKey() == body) continue;
                possibleColliders.addAll(e.getValue());
            }

            for (PolygonCollider2D c1 : myColliders) {
                for (PolygonCollider2D c2 : possibleColliders) {
                    Vector3f displacement = CollisionUtils.testPolygonCollision(c1.polygon, c2.polygon);
                    transform.position.add(displacement);
                }
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
            drawContactPoint(contact, normal);
        }

    }

    private void drawContactPoint(Vector3f point, Vector3f normal) {
        ShapeDrawer drawer = getSpace().getShapeDrawer();
        drawer.drawPoint(point, 1f, Pixel.SOLID_WHITE);
        drawer.drawLine(point, normal, 0.5f, Pixel.SOLID_WHITE);
    }

}