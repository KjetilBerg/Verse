package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.Transform;

import java.util.Iterator;

public class CameraSystem extends ComponentSystem {

    private EntityQuery query;

    public CameraSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(Camera.class), null, null);
        query = desc.compile(getSpace().getEntityManager());
    }

    @Override
    public void update(float dt) {

        Iterator<Entity> entities = query.execute().iterator();

        while (entities.hasNext()) {
            Entity entity = entities.next();
            Transform transform = entity.getTransform();
            Camera camera = entity.getComponent(Camera.class);

            float amplitude = camera.zoom * 0.5f;   // TODO: camera: 1/zoom instead ?
            camera.projectionMatrix.setOrtho(-amplitude, amplitude, amplitude, -amplitude, camera.nearPlane, camera.farPlane);

            if (camera.target != null) transform.rotation.lookAlong(camera.target.position, camera.up);

            camera.viewMatrix.identity();
            camera.viewMatrix.translate(transform.position);
            camera.viewMatrix.scale(transform.scale); // note: does scale matter ?
            camera.viewMatrix.rotate(transform.rotation);

        }
    }

}
