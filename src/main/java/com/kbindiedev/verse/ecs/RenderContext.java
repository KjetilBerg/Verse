package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.gfx.window.ApplicationWindow;
import com.kbindiedev.verse.Viewport;

/** A context for rendering the scene. Includes camera entity and varying gfx details. */
public class RenderContext {

    private Entity cameraEntity;
    private ApplicationWindow applicationWindow;
    private Viewport viewport;

    // TODO: some window or idk
    public RenderContext(Entity cameraEntity, ApplicationWindow applicationWindow) { this(cameraEntity, applicationWindow, new Viewport()); }
    public RenderContext(Entity cameraEntity, ApplicationWindow applicationWindow, Viewport viewport) {
        if (!cameraEntity.hasComponents(Camera.class)) throw new IllegalArgumentException("cameraEntity must have a camera"); // TODO: VoidCamera ? to avoid crash
        this.cameraEntity = cameraEntity;
        this.applicationWindow = applicationWindow;
        this.viewport = viewport;
    }

    public Camera getCameraComponent() { return cameraEntity.getComponent(Camera.class); }
    public Transform getCameraTransform() { return cameraEntity.getTransform(); }

    public ApplicationWindow getApplicationWindow() { return applicationWindow; }
    public Viewport getViewport() { return viewport; }

    /** Prepare the window to be rendered to. */
    public void prepare() {
        viewport.applyToWindow(applicationWindow);
    }

}
