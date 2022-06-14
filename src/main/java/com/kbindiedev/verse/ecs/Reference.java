package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.datastore.IConstantSize;

/** A reference to an object. Used by serialization. Counts as "constant size" because it assumes "T" as a pointer. */
public class Reference<T> implements IConstantSize {

    private T t;

    public Reference() { this(null); }
    public Reference(T t) { put(t); }

    public T get() { return t; }
    public void put(T t) { this.t = t; }

}