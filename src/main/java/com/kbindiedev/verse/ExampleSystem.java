package com.kbindiedev.verse;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.SpriteAnimator;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.datastore.SpriteAnimation;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.input.keyboard.KeyEventTracker;
import com.kbindiedev.verse.input.keyboard.Keys;
import com.kbindiedev.verse.util.view.FitViewMapper;
import org.joml.Vector2f;

import java.awt.*;
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
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(ExampleComponent.class, SpriteAnimator.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        query2 = new EntityQueryDesc(new ComponentTypeGroup(Camera.class), null, null).compile(getSpace().getEntityManager());
    }

    @Override
    public void update(float dt) {
        Iterator<Entity> entities = query.execute().iterator();

        boolean next = getSpace().getKeyboardTracker().wasKeyReleasedThisIteration(Keys.KEY_L);
        boolean prev = getSpace().getKeyboardTracker().wasKeyReleasedThisIteration(Keys.KEY_K);

        while (entities.hasNext()) {
            Entity entity = entities.next();

            ExampleComponent component = entity.getComponent(ExampleComponent.class);
            SpriteAnimator animator = entity.getComponent(SpriteAnimator.class);

            if (next) {
                component.currentIndex = (component.currentIndex + 1) % component.animations.size();
            }
            if (prev) {
                component.currentIndex = (component.currentIndex + component.animations.size() - 1) % component.animations.size();
            }

            animator.animation = component.animations.get(component.currentIndex);
        }
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
        float zoom = 0f;
        KeyEventTracker keys = getSpace().getKeyboardTracker();
        if (keys.isKeyDown(Keys.KEY_W)) movement.y += 3.0f;
        if (keys.isKeyDown(Keys.KEY_S)) movement.y -= 3.0f;
        if (keys.isKeyDown(Keys.KEY_A)) movement.x += 3.0f;
        if (keys.isKeyDown(Keys.KEY_D)) movement.x -= 3.0f;
        if (keys.isKeyDown(Keys.KEY_Q)) zoom += 100.0f;
        if (keys.isKeyDown(Keys.KEY_E)) zoom -= 100.0f;

        Iterator<Entity> cameras = query2.execute().iterator();
        while (cameras.hasNext()) {
            Entity entity = cameras.next();
            Camera camera = entity.getComponent(Camera.class);
            Transform transform = entity.getComponent(Transform.class);

            camera.zoom += zoom * dt;

            if (transform != null) {
                transform.position.x += movement.x() * dt * camera.zoom;
                transform.position.y += movement.y() * dt * camera.zoom;
            }

            if (transform == null) System.out.println("Warn: camera has no transform. cannot translate");

            // TODO: 1 frame late. this is temp while Scissoring is not implemented. still, there should be a way to retrieve screen/window.

            //float ar = (float)wWidth / wHeight;
            //camera.aspectRatio = ar;
/*
            Rectangle cameraRect = new Rectangle(0, 0, 1000*(int)camera.orthographicWidth, (int)(1000f * camera.orthographicWidth / camera.aspectRatio));
            Rectangle dest = new Rectangle(0, 0, wWidth, wHeight);
            FitViewMapper mapper = new FitViewMapper(cameraRect, dest); // TODO this is a test
            Rectangle r = mapper.getResult();
            System.out.printf("camX: %d, camY: %d, camW: %d, camH: %d, winW. %d, winY: %d, rX: %d, rY: %d, rW: %d, rH: %d\n",
                    0, 0, 1000*(int)camera.orthographicWidth, (int)(1000*camera.orthographicWidth / camera.aspectRatio),
                    wWidth, wHeight,
                    r.x, r.y, r.width, r.height);
*/

        }
    }

    // TODO: very temp and very sketchy.
    private int wWidth = 1, wHeight = 1;
    @Override
    public void render(RenderContext context) {
        wWidth = context.getApplicationWindow().getWindowWidth();
        wHeight = context.getApplicationWindow().getWindowHeight();
    }
}
