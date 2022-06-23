package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.ShapeRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.PolygonBatch;
import com.kbindiedev.verse.gfx.strategy.providers.ColoredPolygon;
import com.kbindiedev.verse.gfx.strategy.providers.IMeshDataProvider;
import org.joml.Vector3f;

import java.util.Iterator;

/** Render particular shapes. */
public class ShapeRendererSystem extends ComponentSystem {

    private EntityQuery query;
    private PolygonBatch batch;

    public ShapeRendererSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(ShapeRenderer.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        batch = new PolygonBatch(getSpace().getGfxImplementation(), 128, 128, Mesh.RenderMode.TRIANGLES); // TODO numbers
    }

    @Override
    public void render(RenderContext context) {

        // TODO: temp (context prepare). When change rendering pipeline, batch renders to FBO (on camera). Renderer then considers context.
        context.prepare();
        batch.setProjectionMatrix(context.getCameraComponent().projectionMatrix);
        batch.setViewMatrix(context.getCameraComponent().viewMatrix);
        batch.begin();

        Iterator<Entity> entities = query.execute().iterator();
        while (entities.hasNext()) {
            Entity entity = entities.next();

            Transform transform = entity.getTransform();
            ShapeRenderer renderer = entity.getComponent(ShapeRenderer.class);

            ColoredPolygon polygon = renderer.polygon;
            if (polygon == null) continue;
            //polygon.setCenter(transform.position);
            //polygon.setRotation(transform.rotation);
            //polygon.setScale(transform.scale);
            //polygon.setScale(new Vector3f(2, 2, 1));

            batch.drawConvexPolygon(polygon);

        }
        batch.end();
    }

}
