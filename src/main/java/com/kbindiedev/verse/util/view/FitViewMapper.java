package com.kbindiedev.verse.util.view;

import java.awt.*;

/** Scales down the highest dimension of source to fit into the destination. */
public class FitViewMapper extends ViewMapper {

    public FitViewMapper(Rectangle source, Rectangle destination) { super(source, destination); }

    // TODO: rectangle of floats instead of int
    // TODO: this needs further testing
    @Override
    protected Rectangle calculate() {

        float widthRatio = (float)source.width / destination.width;
        float heightRatio = (float)source.height / destination.height;

        float largestRatio = Math.max(widthRatio, heightRatio);

        float newWidth = source.width / largestRatio;
        float newHeight = source.height / largestRatio;

        float centerDestX = (destination.x + destination.width) / 2f;
        float centerDestY = (destination.y + destination.height) / 2f;

        return new Rectangle(Math.round(centerDestX - newWidth/2), Math.round(centerDestY - newHeight/2), Math.round(newWidth), Math.round(newHeight));
    }

}