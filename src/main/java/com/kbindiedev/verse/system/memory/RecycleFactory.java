package com.kbindiedev.verse.system.memory;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.exceptions.NotEnoughMemoryException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A factory for anything that is {@link IRecyclable}.
 * Instances are recycled by {@link Object#hashCode()} and {@link Object#equals(Object)}, so there must not be any clashes between instances.
 */
public class RecycleFactory<T extends IRecyclable> {

    private Supplier<T> factory;
    private int maxInstances;

    private HashSet<T> instances;
    private List<T> recycled;

    public RecycleFactory(Supplier<T> factory) { this(factory, 0); }

    /** maxInstances = 0 means an unlimited number of instances. */
    public RecycleFactory(Supplier<T> factory, int maxInstances) {
        this.factory = factory;
        this.maxInstances = maxInstances;

        instances = new HashSet<>();
        recycled = new LinkedList<>();
    }

    /** @return the number of instances I can create before I reach {@link #maxInstances}, or Integer.MAX_VALUE if this number is unlimited. */
    public int maxInstancesRemaining() {
        if (maxInstances == 0) return Integer.MAX_VALUE;
        return maxInstances - (instances.size() + recycled.size());
    }

    public T retrieve() throws NotEnoughMemoryException {
        if (recycled.size() > 0) return recycled.remove(0);
        if (maxInstances != 0 && instances.size() >= maxInstances) throw new NotEnoughMemoryException("maxInstances ("+maxInstances+") reached; cannot allocate more");

        T instance = factory.get();
        instances.add(instance);
        return instance;
    }

    /** Prepare an instance so that the next call to {@link #retrieve()} is fast (and does not need to instantiate a need object). */
    public void prepare() throws NotEnoughMemoryException { if (recycled.size() == 0) recycled.add(retrieve()); }

    public void recycle(T instance) {
        boolean removed = instances.remove(instance);
        if (!removed) Assertions.warn("factory recycle: did not contain the given instance: " + instance);
        instance.recycle();
        recycled.add(instance);
    }

    /** Remove an instance entirely, wherever it is (from instances or recycled instances). */
    public void erase(T instance) {
        if (!instances.remove(instance)) recycled.remove(instance);
    }

    /** Remove all instances entirely, wherever they are (from instances or recycled instances). */
    public void erase() { instances.clear(); recycled.clear(); }

}