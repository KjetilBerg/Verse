package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.RigidBody2D;
import com.kbindiedev.verse.ecs.components.SpriteAnimator;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.input.keyboard.Keys;
import com.kbindiedev.verse.util.Properties;
import com.kbindiedev.verse.util.Trigger;
import org.joml.Vector3f;

import java.util.Iterator;

public class PlayerMovementSystem extends ComponentSystem {

    private EntityQuery query;
    private Trigger attackTrigger;

    public PlayerMovementSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(Transform.class, PlayerComponent.class, SpriteRenderer.class, SpriteAnimator.class, RigidBody2D.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        attackTrigger = new Trigger();
    }

    @Override
    public void fixedUpdate(float dt) {
        Iterator<Entity> entities = query.execute().iterator();

        float dx = 0f, dy = 0f;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_UP)) dy += 1;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_DOWN)) dy -= 1;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_LEFT)) dx -= 1;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_RIGHT)) dx += 1;

        //dx *= dt; dy *= dt;

        boolean running = false;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_LEFT_SHIFT)) running = true;

        boolean attack = (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_F));
        attackTrigger.reset();
        if (attack) attackTrigger.trigger();

        while (entities.hasNext()) {
            Entity entity = entities.next();

            PlayerComponent player = entity.getComponent(PlayerComponent.class);
            RigidBody2D body = entity.getComponent(RigidBody2D.class);

            float speed = (running ? player.runSpeed : player.speed);
            body.velocity.x = dx * speed;
            body.velocity.y = dy * speed;
            //transform.position.x += dx * speed;
            //transform.position.y += dy * speed;
        }
    }


    @Override
    public void update(float dt) {

        Iterator<Entity> entities = query.execute().iterator();

        float dx = 0f, dy = 0f;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_UP)) dy += 1;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_DOWN)) dy -= 1;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_LEFT)) dx -= 1;
        if (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_RIGHT)) dx += 1;


        boolean attack = (getSpace().getKeyboardTracker().isKeyDown(Keys.KEY_F));
        attackTrigger.reset();
        if (attack) attackTrigger.trigger();

        while (entities.hasNext()) {
            Entity entity = entities.next();

            Transform transform = entity.getTransform();
            PlayerComponent player = entity.getComponent(PlayerComponent.class);
            SpriteRenderer renderer = entity.getComponent(SpriteRenderer.class);
            SpriteAnimator animator = entity.getComponent(SpriteAnimator.class);


            if (dx > 0) player.facingRight = true;
            if (dx < 0) player.facingRight = false;
            renderer.flipX = !player.facingRight;

            boolean moving = (dx != 0 || dy != 0);

            Properties properties = animator.controller.getContext().getProperties();

            properties.put("moving", moving);
            properties.put("attack", attackTrigger);
            // TODO BUG: can transition from slash to run for some reason

            if (count++ % 60 == 0) System.out.println("Player pos: " + transform.position.x + " " + transform.position.y);
        }
    }
    private int count = 0;

}
