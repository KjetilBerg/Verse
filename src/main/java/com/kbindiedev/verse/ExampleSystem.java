package com.kbindiedev.verse;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;

import java.util.Iterator;

public class ExampleSystem extends ComponentSystem {

    private EntityQuery query;

    public ExampleSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        System.out.println("ExampleSystem start");
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(ExampleComponent.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
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
    }
}
