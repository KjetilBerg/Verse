package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.gfx.SpriteBatch;

import java.util.Iterator;

public class SpriteRendererSystem extends ComponentSystem {

    private EntityQuery query;
    private SpriteBatch batch;

    public SpriteRendererSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        System.out.println("SpriteRendererSystem start");
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(SpriteRenderer.class), null, null);
        query = desc.compile(getSpace().getEntityManager());


        // TODO: from some localized system (reuse batches)?
        // TODO: 128, 8. use other numbers
        batch = new SpriteBatch(getSpace().getGfxImplementation(), 128, 8);
    }

    // TODO FUTURE: 3DSpriteBatch (consider other rotations and z component of position)
    // TODO MAYBE: implement rotation (pitch and yaw) into SpriteBatch
        // TODO: or have angle be part of SpriteRenderer component
    @Override
    public void render(RenderContext context) {
        Iterator<Entity> entities = query.execute().iterator();

        // TODO: temp (context prepare). When change rendering pipeline, batch renders to FBO (on camera). Renderer then considers context.
        context.prepare();
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