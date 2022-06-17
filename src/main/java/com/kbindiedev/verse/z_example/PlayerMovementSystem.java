package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.input.keyboard.Keys;

import java.util.Iterator;

public class PlayerMovementSystem extends ComponentSystem {

    private EntityQuery query;

    public PlayerMovementSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(Transform.class, PlayerComponent.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
    }


    @Override
    public void update(float dt) {

        Iterator<Entity> entities = query.execute().iterator();

        float dx = 0f, dy = 0f;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_UP)) dy -= dt;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_DOWN)) dy += dt;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_LEFT)) dx -= dt;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_RIGHT)) dx += dt;

        while (entities.hasNext()) {
            Entity entity = entities.next();

            Transform transform = entity.getTransform();
            PlayerComponent player = entity.getComponent(PlayerComponent.class);


            transform.position.x += dx * player.speed;
            transform.position.y += dy * player.speed;

        }
    }
}
