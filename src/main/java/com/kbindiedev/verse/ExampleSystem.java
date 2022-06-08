package com.kbindiedev.verse;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;

import java.util.Iterator;

public class ExampleSystem extends ComponentSystem {

    private EntityQuery query;
    private EntityQuery query2;

    public ExampleSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        System.out.println("ExampleSystem start");
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(ExampleComponent.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        query2 = new EntityQueryDesc(new ComponentTypeGroup(Camera.class), null, null).compile(getSpace().getEntityManager());

        query2.execute().iterator().next().getComponent(Camera.class).zoom = 4f;
    }

    @Override
    public void update(float dt) {
        //System.out.println("update: " + dt);
    }

    @Override
    public void fixedUpdate(float dt) {
        System.out.println("fixedUpdate: " + dt);
        EntityGroup entities = query.execute();

        Iterator<Entity> x = entities.iterator();
        while (x.hasNext()) {
            ExampleComponent comp = x.next().getComponent(ExampleComponent.class);
            if (comp == null) throw new RuntimeException("comp not ExampleComponent");
            System.out.println("Component data: " + comp.data);
        }

        Iterator<Entity> cameras = query2.execute().iterator();
        while (cameras.hasNext()) {
            Entity entity = cameras.next();
            Transform transform = entity.getComponent(Transform.class);

            if (transform != null) transform.position.x += 0.1f * dt;

            if (transform == null) System.out.println("Warn: camera has no transform. cannot translate");

        }
    }
}
