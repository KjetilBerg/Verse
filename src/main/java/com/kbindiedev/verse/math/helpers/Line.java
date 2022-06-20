package com.kbindiedev.verse.math.helpers;

/** Something connecting two of something else. */
public class Line<T> {

    private T p1, p2;

    public Line(T p1, T p2) {
        this.p1 = p1; this.p2 = p2;
    }

    public T getP1() { return p1; }
    public T getP2() { return p2; }

}
