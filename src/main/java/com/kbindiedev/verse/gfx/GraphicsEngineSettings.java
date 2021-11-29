package com.kbindiedev.verse.gfx;

/** A class that defines some settings for a GraphicsEngine implementation */
public class GraphicsEngineSettings {

    private Pixel backgroundColor;

    public GraphicsEngineSettings() {
        backgroundColor = new Pixel(0, 0, 0);
    }

    public void setBackgroundColor(Pixel pixel) { backgroundColor = pixel; }
    public Pixel getBackgroundColor() { return backgroundColor; }

}
