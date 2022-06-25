package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import org.joml.Quaternionf;

import java.util.Iterator;

public class ConstantRotatorSystem extends ComponentSystem {

    private EntityQuery query;

    public ConstantRotatorSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(ConstantRotatorComponent.class, Transform.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
    }

    @Override
    public void update(float dt) {

        Iterator<Entity> entities = query.execute().iterator();

        while (entities.hasNext()) {
            Entity entity = entities.next();

            ConstantRotatorComponent rotator = entity.getComponent(ConstantRotatorComponent.class);
            Transform transform = entity.getComponent(Transform.class);

            transform.rotation.rotateZ(rotator.amountPerSecond * dt);
        }

    }
}
