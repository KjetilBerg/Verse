package com.kbindiedev.verse.ecs.systems;

// TODO: consider rename this to some "non-system", since you can call methods on it externally

import com.kbindiedev.verse.ecs.*;
import com.kbindiedev.verse.gfx.window.ApplicationWindow;

import java.util.ArrayList;
import java.util.List;

/** Prepares things in the scene before the Renderer runs. */
public class RenderContextPreparerSystem extends ComponentSystem {

    private List<RenderContext> renderContexts;

    public RenderContextPreparerSystem(Space space) {
        super(space);
        renderContexts = new ArrayList<>();
    }

    @Override
    public void update(float dt) {

        for (RenderContext context : renderContexts) {
            if (!context.shouldCameraFollowAspectRatio()) continue;

            ApplicationWindow window = context.getApplicationWindow();
            context.getCameraComponent().aspectRatio = (float)window.getWindowWidth() / window.getWindowHeight();
        }

    }

    // TODO: get from Renderer or something
    public void addRenderContext(RenderContext context) { renderContexts.add(context); }

}
