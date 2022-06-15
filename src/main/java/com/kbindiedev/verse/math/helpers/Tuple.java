package com.kbindiedev.verse.math.helpers;

/** A pair. */
public class Tuple<T, E> {

    private T t;
    private E e;

    public Tuple(T t, E e) {
        this.t = t;
        this.e = e;
    }

    public T getFirst() { return t; }
    public E getSecond() { return e; }

}