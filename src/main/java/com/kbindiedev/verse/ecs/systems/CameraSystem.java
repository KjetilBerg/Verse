package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.math.helpers.Rectanglef;
import com.kbindiedev.verse.profiling.Assertions;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.*;
import java.util.Iterator;

public class CameraSystem extends ComponentSystem {

    private EntityQuery query;
    private Rectanglef tempViewport;

    public CameraSystem(Space space) {
        super(space);
    }

    @Override
    public void start() {
        EntityQueryDesc desc = new EntityQueryDesc(new ComponentTypeGroup(Camera.class), null, null);
        query = desc.compile(getSpace().getEntityManager());

        tempViewport = new Rectanglef();
    }

    @Override
    public void update(float dt) {

        Iterator<Entity> entities = query.execute().iterator();

        while (entities.hasNext()) {
            Entity entity = entities.next();
            Transform transform = entity.getTransform();
            Camera camera = entity.getComponent(Camera.class);

            camera.zoom = adjustCameraZoom(camera.zoom); // prevent floating point errors

            // TODO: better following and inverse camera
            if (camera.target != null) {
                transform.position.x = -(camera.target.position.x + camera.target.scale.x / 2);
                transform.position.y = -(camera.target.position.y + camera.target.scale.y / 2);
            }
            // TODO: this is only if perspective camera / 3d camera
            //if (camera.target != null) transform.rotation.lookAlong(camera.target.position, camera.up);

            switch (camera.cameraType) {
                case ORTHOGRAPHIC:
                    float horizontal = camera.zoom * camera.orthographicWidth;
                    float vertical = camera.zoom * camera.orthographicWidth / camera.aspectRatio;

                    tempViewport.setWidth(horizontal); tempViewport.setHeight(vertical);
                    tempViewport.focusCenterX(transform.position.x); tempViewport.focusCenterY(transform.position.y);

                    // TODO: otherwise minWidth/height

                    if (camera.translateWhenHitBounds) {
                        tempViewport.translateBound(camera.bounds);
                    } else {
                        tempViewport.shrinkBound(camera.bounds, camera.minOrthographicWidth, camera.minOrthographicWidth * camera.aspectRatio);
                    }

                    transform.position.x = tempViewport.getCenterX();
                    transform.position.y = tempViewport.getCenterY();
                    float oHorizontal = horizontal;
                    horizontal = tempViewport.getWidth(); vertical = tempViewport.getHeight();

                    float hScale = horizontal / oHorizontal;
                    camera.zoom = adjustCameraZoom(camera.zoom * hScale);

                    //horizontal = camera.zoom * camera.orthographicWidth;
                    //vertical = camera.zoom * camera.orthographicWidth / camera.aspectRatio;

                    camera.projectionMatrix.setOrtho(-horizontal, horizontal, vertical, -vertical, camera.nearPlane, camera.farPlane);
                    break;
                default:
                    Assertions.error("unknown CameraType: %s", camera.cameraType.name());
                    throw new IllegalArgumentException("unknown CameraType: " + camera.cameraType.name());
            }

            camera.viewMatrix.identity();
            camera.viewMatrix.translate(transform.position);
            camera.viewMatrix.scale(transform.scale); // note: does scale matter ?
            camera.viewMatrix.rotate(transform.rotation);

        }
    }

    private static float adjustCameraZoom(float zoom) {
        float cameraIncrements = 1 / 32f; // TODO: some errors still prevail
        //return ((int)(zoom / cameraIncrements)) * cameraIncrements;
        return zoom;
    }

}