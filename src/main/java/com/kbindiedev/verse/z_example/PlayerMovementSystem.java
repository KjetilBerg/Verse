package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.SpriteAnimator;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.input.keyboard.Keys;
import com.kbindiedev.verse.util.Properties;
import com.kbindiedev.verse.util.Trigger;

import java.util.Iterator;

public class PlayerMovementSystem extends ComponentSystem {

    private EntityQuery query;
    private Trigger attackTrigger;

    public PlayerMovementSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(Transform.class, PlayerComponent.class, SpriteRenderer.class, SpriteAnimator.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        attackTrigger = new Trigger();
    }


    @Override
    public void update(float dt) {

        Iterator<Entity> entities = query.execute().iterator();

        float dx = 0f, dy = 0f;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_UP)) dy += dt;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_DOWN)) dy -= dt;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_LEFT)) dx -= dt;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_RIGHT)) dx += dt;

        boolean attack = (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_F));
        attackTrigger.reset();
        if (attack) attackTrigger.trigger();

        while (entities.hasNext()) {
            Entity entity = entities.next();

            Transform transform = entity.getTransform();
            PlayerComponent player = entity.getComponent(PlayerComponent.class);
            SpriteRenderer renderer = entity.getComponent(SpriteRenderer.class);
            SpriteAnimator animator = entity.getComponent(SpriteAnimator.class);

            transform.position.x += dx * player.speed;
            transform.position.y += dy * player.speed;

            if (dx > 0) player.facingRight = true;
            if (dx < 0) player.facingRight = false;
            renderer.flipX = !player.facingRight;

            boolean moving = (dx != 0 || dy != 0);

            Properties properties = animator.controller.getContext().getProperties();

            properties.put("moving", moving);
            properties.put("attack", attackTrigger);
            // TODO BUG: can transition from slash to run for some reason

            // temporary bounds
            if (transform.position.x < -16f) transform.position.x = -16f;
            if (transform.position.y < 12f) transform.position.y = 12f;
            if (transform.position.x > 28 * 16f) transform.position.x = 28 * 16f;
            if (transform.position.y > 13 * 24f) transform.position.y = 13 * 24f;

        }
    }
}
