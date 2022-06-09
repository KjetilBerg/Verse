package com.kbindiedev.verse.util.view;

import java.awt.*;

/**
 * Transforms a source so that it fits to a certain destination.
 *
 * @see FitViewMapper
 * @see FillViewMapper
 * @see StretchViewMapper
 */
public abstract class ViewMapper {

    protected Rectangle source;
    protected Rectangle destination;

    private boolean dirty;
    private Rectangle result;

    public ViewMapper(Rectangle source, Rectangle destination) {
        this.source = source;
        this.destination = destination;
        dirty = true;
    }

    public void setSource(Rectangle source) { this.source = source; dirty = true; }
    public void setDestination(Rectangle destination) { this.destination = destination; dirty = true; }

    public Rectangle getResult() {
        if (dirty) {
            dirty = false;
            result = calculate();
        }
        return result;
    }

    protected abstract Rectangle calculate();

}