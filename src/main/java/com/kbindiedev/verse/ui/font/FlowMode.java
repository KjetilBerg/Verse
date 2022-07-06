package com.kbindiedev.verse.ui.font;

/**
 * Describes how an element "flows" from a focal point.
 * For example, {@link #TOP_LEFT} indicates that the focal point should be in the upper left corner of the element.
 */
public enum FlowMode {
    TOP_LEFT,       TOP,        TOP_RIGHT,
    LEFT,           CENTER,     RIGHT,
    BOTTOM_LEFT,    BOTTOM,     BOTTOM_RIGHT
}