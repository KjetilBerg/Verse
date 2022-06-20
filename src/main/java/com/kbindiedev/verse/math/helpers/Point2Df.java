package com.kbindiedev.verse.math.helpers;

import org.joml.Vector2f;

/** A 2D point made of floats. */
public class Point2Df {

    private float x, y;

    public Point2Df(float x, float y) {
        this.x = x; this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void translate(Vector2f vector) {
        x += vector.x; y += vector.y;
    }

    public void translateTo(Vector2f vector) {
        x = vector.x; y = vector.y;
    }

    @Override
    public String toString() {
        return "[ x: " + x + ", y: " + y + " ]";
    }

}