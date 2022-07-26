package com.kbindiedev.verse.ui.font;

import org.joml.Vector2f;

// TODO consider: generalize, rename to Anchor ?

/**
 * Describes how an element "flows" from a focal point.
 * For example, {@link #TOP_LEFT} indicates that the focal point should be in the upper left corner of the element.
 */
public enum FlowMode {
    TOP_LEFT,       TOP,        TOP_RIGHT,
    LEFT,           CENTER,     RIGHT,
    BOTTOM_LEFT,    BOTTOM,     BOTTOM_RIGHT;

    /** @return a normalized vector (-0.5f to 0.5f) representing the direction required to move to reach FlowMode: CENTER */
    public static Vector2f directionToCenter(FlowMode mode) {
        float dx = 0, dy = 0;
        if (mode == FlowMode.TOP_LEFT || mode == FlowMode.LEFT || mode == FlowMode.BOTTOM_LEFT)        dx = 0.5f;
        if (mode == FlowMode.TOP_RIGHT || mode == FlowMode.RIGHT || mode == FlowMode.BOTTOM_RIGHT)     dx = -0.5f;
        if (mode == FlowMode.TOP_LEFT || mode == FlowMode.TOP || mode == FlowMode.TOP_RIGHT)           dy = -0.5f;
        if (mode == FlowMode.BOTTOM_LEFT || mode == FlowMode.BOTTOM || mode == FlowMode.BOTTOM_RIGHT)  dy = 0.5f;
        return new Vector2f(dx, dy);
    }
}