package com.kbindiedev.verse;

// TODO: not sure what package this should belong to

import com.kbindiedev.verse.gfx.window.ApplicationWindow;

/** Defines how a space is mapped onto another, by using floats (0 - 1). */
public class Viewport {

    // TODO: project and unproject (mouse position)

    private float x, y, w, h;

    public Viewport() { this(0f, 0f, 1f, 1f); }
    public Viewport(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /** Apply this viewport to the given window. */
    public void applyToWindow(ApplicationWindow window) {
        int locX = (int)(window.getWindowWidth() * x);
        int locY = (int)(window.getWindowHeight() * y);
        int locW = (int)(window.getWindowWidth() * w);
        int locH = (int)(window.getWindowHeight() * h);
        window.setViewportBounds(locX, locY, locW, locH);
    }

}