package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.Transform;

/** A context for rendering the scene. Includes camera entity and varying gfx details. */
public class RenderContext {

    private Entity cameraEntity;

    // TODO: some window or idk
    public RenderContext(Entity cameraEntity) {
        if (!cameraEntity.hasComponents(Camera.class)) throw new IllegalArgumentException("cameraEntity must have a camera"); // TODO: VoidCamera ? to avoid crash
        this.cameraEntity = cameraEntity;
    }

    public Camera getCameraComponent() { return cameraEntity.getComponent(Camera.class); }
    public Transform getCameraTransform() { return cameraEntity.getTransform(); }

}
