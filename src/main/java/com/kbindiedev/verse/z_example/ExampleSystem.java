package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.SpriteAnimator;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.ShapeDrawer;
import com.kbindiedev.verse.gfx.SpriteBatch;
import com.kbindiedev.verse.input.keyboard.KeyEventTracker;
import com.kbindiedev.verse.input.keyboard.Keys;
import com.kbindiedev.verse.input.keyboard.event.KeyEvent;
import com.kbindiedev.verse.io.files.Files;
import com.kbindiedev.verse.ui.font.BitmapFont;
import com.kbindiedev.verse.ui.font.BitmapFontLoader;
import com.kbindiedev.verse.ui.font.Text;
import com.kbindiedev.verse.util.Trigger;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class ExampleSystem extends ComponentSystem {

    private EntityQuery query;
    private EntityQuery query2;

    private Trigger nextTrigger;
    private Trigger prevTrigger;
    private Trigger attackTrigger;

    private BitmapFont font;
    private SpriteBatch spritebatch; // TODO remove

    public ExampleSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        System.out.println("ExampleSystem start");
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(ExampleComponent.class, SpriteAnimator.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        query2 = new EntityQueryDesc(new ComponentTypeGroup(Camera.class), null, null).compile(getSpace().getEntityManager());

        nextTrigger = new Trigger();
        prevTrigger = new Trigger();
        attackTrigger = new Trigger();

        try {
            font = BitmapFontLoader.getInstance().load(new File("../arial.fnt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        spritebatch = new SpriteBatch(getSpace().getGfxImplementation(), 512, 8);
    }

    @Override
    public void update(float dt) {
        List<KeyEvent> keyEvents = getSpace().getKeyboardTracker().getAllEventsThisIteration();

        Iterator<Entity> entities = query.execute().iterator();

        boolean next = getSpace().getKeyboardTracker().wasKeyReleasedThisIteration(Keys.KEY_L);
        boolean prev = getSpace().getKeyboardTracker().wasKeyReleasedThisIteration(Keys.KEY_K);

        nextTrigger.reset();
        prevTrigger.reset();
        if (next) nextTrigger.trigger();
        if (prev) prevTrigger.trigger();

        if (getSpace().getKeyboardTracker().wasKeyPressedThisIteration(Keys.KEY_M)) attackTrigger.trigger();


        // TODO: messy, should have something like .didTransitionToState(playerSlash)
        boolean playSlashSound = false;
        boolean playGenericSound = false;
        if (getSpace().getKeyboardTracker().wasKeyReleasedThisIteration(Keys.KEY_F)) playSlashSound = true;
        if (getSpace().getKeyboardTracker().wasKeyReleasedThisIteration(Keys.KEY_U)) playGenericSound = true;

        while (entities.hasNext()) {
            Entity entity = entities.next();

            ExampleComponent component = entity.getComponent(ExampleComponent.class);
            SpriteAnimator animator = entity.getComponent(SpriteAnimator.class);

            animator.controller.getContext().getProperties().put("next", nextTrigger);
            animator.controller.getContext().getProperties().put("prev", prevTrigger);
            animator.controller.getContext().getProperties().put("attack", attackTrigger);

            if (playSlashSound) component.slashSoundSource.play();
            if (playGenericSound) component.genericSoundSource.play();

            if (keyEvents.size() > 0) {
                StringBuilder sb = new StringBuilder(component.text);
                for (KeyEvent event : keyEvents) {
                    System.out.println("Keyevent: " + event);
                    if (event.getType() == KeyEvent.KeyEventType.KEYUP) continue;
                    int keycode = event.getKeycode();
                    if (keycode != Keys.KEY_BACKSPACE) sb.append((char)keycode);
                    if (keycode == Keys.KEY_BACKSPACE && sb.length() > 0) {
                        sb.setLength(sb.length() - 1);
                    }
                    if (keycode == Keys.KEY_W) component.textSize++;
                    if (keycode == Keys.KEY_S) component.textSize--;
                }
                component.text = sb.toString();
            }


        }
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
        if (keys.isKeyDown(Keys.KEY_A)) movement.x -= 3.0f;
        if (keys.isKeyDown(Keys.KEY_D)) movement.x += 3.0f;
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

    @Override
    public void render(RenderContext context) {

        spritebatch.setProjectionMatrix(context.getCameraComponent().projectionMatrix);
        spritebatch.setViewMatrix(context.getCameraComponent().viewMatrix);
        spritebatch.setZPos(0.3f);
        spritebatch.begin();

        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) {
            Entity entity = entities.next();
            Transform transform = entity.getTransform();
            ExampleComponent component = entity.getComponent(ExampleComponent.class);
            Text text = new Text(component.text, font, component.textSize);
            text.draw(spritebatch, transform.position.x, transform.position.y);
        }

        spritebatch.end();
    }

}