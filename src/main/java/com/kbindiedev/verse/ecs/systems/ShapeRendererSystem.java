package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.ShapeRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.PolygonTriangleBatch;
import com.kbindiedev.verse.gfx.strategy.providers.ColoredVertexProvider;
import org.joml.Vector3f;

import java.util.Iterator;

/** Render particular shapes. */
public class ShapeRendererSystem extends ComponentSystem {

    private EntityQuery query;
    private PolygonTriangleBatch batch;

    public ShapeRendererSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(ShapeRenderer.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        batch = new PolygonTriangleBatch(getSpace().getGfxImplementation(), 128, 128, Mesh.RenderMode.TRIANGLES); // TODO numbers
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

            if (sides != 3 && count++ % 10 == 0) sides = Math.max((sides + 1) % 30, 3);
            //batch.drawConvexPolygon(generateShape(32f, sides));
            //batch.drawConvexPolygon(provider, 5);
            batch.drawConvexPolygon(provider);
            batch.drawConvexPolygon(provider2);
        }
        batch.end();
    }

    private int count = 0;
    private int sides = 4;

    private float degToRad(float deg) {
        return (float)(deg * Math.PI / 180f);
    }

    private static Vector3f[] generateShape(float radius, int n) {
        double radsPerPoint = 2 * Math.PI / n;
        Vector3f[] arr = new Vector3f[n];
        double currentRad = 0;
        for (int i = 0; i < n; ++i) {
            float x = (float)(Math.cos(currentRad) * radius);
            float y = (float)(Math.sin(currentRad) * radius);
            arr[i] = new Vector3f(x, y, 0f);
            currentRad += radsPerPoint;
        }
        return arr;
    }

    //private static ColoredTrianglesProvider provider = new ColoredTrianglesProvider(6, true);
    private static ColoredVertexProvider provider = new ColoredVertexProvider(6);
    private static ColoredVertexProvider provider2 = new ColoredVertexProvider(6);
    static {
        Vector3f[] shape = generateShape(32f, 5);
        for (int i = 0; i < shape.length; ++i) {
            provider.setPosition(i+1, shape[i].x, shape[i].y, shape[i].z);
            provider2.setPosition(i+1, shape[i].x + 20f, shape[i].y, shape[i].z);
        }
        provider2.setPosition(0, 20f, 0, 0);
        //for (int i = 0; i < shape.length; ++i) provider.setColor(i, Pixel.random().packed());
        provider.setColor(0, new Pixel(255, 255, 255).packed());
        provider.setColor(1, new Pixel(255, 0, 0).packed());
        provider.setColor(2, new Pixel(255, 255, 0).packed());
        provider.setColor(3, new Pixel(0, 255, 0).packed());
        provider.setColor(4, new Pixel(0, 255, 255).packed());
        provider.setColor(5, new Pixel(0, 0, 255).packed());


        provider2.setColor(0, new Pixel(255, 0, 255).packed());
        provider2.setColor(1, new Pixel(255, 255, 0).packed());
        provider2.setColor(2, new Pixel(255, 255, 255).packed());
        provider2.setColor(3, new Pixel(0, 255, 0).packed());
        provider2.setColor(4, new Pixel(255, 0, 0).packed());
        provider2.setColor(5, new Pixel(0, 0, 255).packed());
    }
}
