package com.kbindiedev.verse;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.input.keyboard.KeyEventTracker;
import com.kbindiedev.verse.input.keyboard.Keys;
import org.joml.Vector2f;

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
        EntityGroup entities = query.execute();

        Iterator<Entity> x = entities.iterator();
        while (x.hasNext()) {
            ExampleComponent comp = x.next().getComponent(ExampleComponent.class);
            if (comp == null) throw new RuntimeException("comp not ExampleComponent");
        }

        // camera movement:
        Vector2f movement = new Vector2f();
        KeyEventTracker keys = getSpace().getKeyboardState();
        if (keys.isKeyDown(Keys.KEY_W)) movement.y -= 1.0f;
        if (keys.isKeyDown(Keys.KEY_S)) movement.y += 1.0f;
        if (keys.isKeyDown(Keys.KEY_A)) movement.x -= 1.0f;
        if (keys.isKeyDown(Keys.KEY_D)) movement.x += 1.0f;

        Iterator<Entity> cameras = query2.execute().iterator();
        while (cameras.hasNext()) {
            Entity entity = cameras.next();
            Transform transform = entity.getComponent(Transform.class);

            if (transform != null) {
                transform.position.x += movement.x() * dt;
                transform.position.y += movement.y() * dt;
            }

            if (transform == null) System.out.println("Warn: camera has no transform. cannot translate");

            // TODO: 1 frame late. this is temp while Scissoring is not implemented. still, there should be a way to retrieve screen/window.
            entity.getComponent(Camera.class).aspectRatio = screenAspectRatio;

        }
    }

    // TODO: very temp and very sketchy.
    private float screenAspectRatio = 1f;
    @Override
    public void render(RenderContext context) {
        screenAspectRatio = (float)context.getApplicationWindow().getWindowWidth() / context.getApplicationWindow().getWindowHeight();
    }
}
