package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.gfx.SpriteBatch;
import org.joml.Matrix4f;

import java.util.Iterator;

public class SpriteRendererSystem extends ComponentSystem {

    private EntityQuery query;
    private SpriteBatch batch;
    private Matrix4f proj;

    public SpriteRendererSystem(Space space) {
        super(space);
    }

    @Override
    public void awake() {
        // TODO: from some localized system (reuse batches)?
        // TODO: 128, 8. use other numbers
        batch = new SpriteBatch(getSpace().getGfxImplementation(), 128, 8);

        proj = new Matrix4f();
        proj.ortho(-2f, 2f, 2f, -2f, -1f, 1f);
        //proj.ortho(-1f, 1f, 1f, -1f, -1f, 1f);  //default, y-down projection
        batch.setProjectionMatrix(proj); // TODO: proj matrix set by camera
    }

    @Override
    public void start() {
        System.out.println("SpriteRendererSystem start");
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(SpriteRenderer.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
    }

    // TODO FUTURE: 3DSpriteBatch (consider other rotations and z component of position)
    // TODO MAYBE: implement rotation (pitch and yaw) into SpriteBatch
    @Override
    public void render(RenderContext context) {
        System.out.println("SpriteRendererSystem render");

        Iterator<Entity> entities = query.execute().iterator();

        batch.setProjectionMatrix(context.getCameraComponent().projectionMatrix);
        batch.setViewMatrix(context.getCameraComponent().viewMatrix);
        batch.begin();
        while (entities.hasNext()) {
            Entity entity = entities.next();
            Transform transform = entity.getTransform();
            SpriteRenderer sprite = entity.getComponent(SpriteRenderer.class);

            batch.setColor(sprite.color);
            batch.draw(sprite.sprite, transform.position.x(), transform.position.y(), 0f, 0f, 1f, 1f,
                    transform.scale.x(), transform.scale.y(), transform.rotation.angle(), false, false); // TODO: is angle right?
        }
        batch.end();
    }

}