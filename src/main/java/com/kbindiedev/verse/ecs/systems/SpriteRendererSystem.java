package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.ShapeDrawer;
import com.kbindiedev.verse.gfx.SpriteBatch;

import java.util.*;

public class SpriteRendererSystem extends ComponentSystem {

    private EntityQuery query;
    private SpriteBatch batch;

    // TODO SOON: SortedFastList
    private List<Entity> sortedQueryResult;
    private long sortedQueryResultVersion = -1;

    public SpriteRendererSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        System.out.println("SpriteRendererSystem start");
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(SpriteRenderer.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        sortedQueryResult = new ArrayList<>();


        // TODO: from some localized system (reuse batches)?
        // TODO: 128, 8. use other numbers
        batch = new SpriteBatch(getSpace().getGfxImplementation(), 128, 8);
    }

    // TODO FUTURE: 3DSpriteBatch (consider other rotations and z component of position)
    // TODO MAYBE: implement rotation (pitch and yaw) into SpriteBatch
        // TODO: or have angle be part of SpriteRenderer component
    @Override
    public void render(RenderContext context) {
        ensureTargetsUpToDate();

        // TODO: temp (context prepare). When change rendering pipeline, batch renders to FBO (on camera). Renderer then considers context.
        context.prepare();
        batch.setProjectionMatrix(context.getCameraComponent().projectionMatrix);
        batch.setViewMatrix(context.getCameraComponent().viewMatrix);
        batch.setGlobalFlipSettings(false, true);
        batch.begin();

        for (Entity entity : sortedQueryResult) {
            Transform transform = entity.getTransform();
            SpriteRenderer sprite = entity.getComponent(SpriteRenderer.class);

            if (sprite.sprite == null) continue;

            batch.setZPos(1f);
            batch.setColor(sprite.color);
            //batch.setZPos(transform.position.z());
            // TODO sprite width/height ?

            /*
            batch.draw(sprite.sprite, transform.position.x() + sprite.offset.x(), transform.position.y() + sprite.offset.y(),
                    0.5f, 0.5f, 1f, 1f, transform.scale.x(), transform.scale.y(), transform.rotation.angle(),
                   sprite.flipX, sprite.flipY);
                   */

            batch.draw(sprite.sprite, transform.position.x() + sprite.offset.x() - transform.scale.x() / 2,
                    transform.position.y() + sprite.offset.y() - transform.scale.y() / 2,
                    0.0f, 0.0f, transform.scale.x(), transform.scale.y(), 1f, 1f, transform.rotation.angle(),
                    sprite.flipX, sprite.flipY);

        }
        batch.end();
    }

    private void ensureTargetsUpToDate() {
        if (query.getEntityTargetsContentVersion() <= sortedQueryResultVersion) return;
        buildSortedRenderers();
        sortedQueryResultVersion = query.getEntityTargetsContentVersion();
    }

    /** Retrieve all SpriteRenderers by query and sort them in a list. */
    private void buildSortedRenderers() {
        sortedQueryResult.clear();
        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) sortedQueryResult.add(entities.next());
        sortedQueryResult.sort((e1, e2) -> {
            float diff = e1.getTransform().position.z - e2.getTransform().position.z;
            if (diff == 0) return 0;
            if (diff < 0) return -1;
            return 1;
        });
    }

    /*
    @Override
    public void onDrawGizmos(RenderContext context) {
        ShapeDrawer drawer = getSpace().getShapeDrawer();

        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) {
            Transform t = entities.next().getTransform();

            drawer.drawPoint(t.position, 1f, new Pixel(127, 127, 127));
        }
    }
    */

}