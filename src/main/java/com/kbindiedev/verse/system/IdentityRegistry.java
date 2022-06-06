package com.kbindiedev.verse.system;

import java.util.HashMap;

/**
 * A registry that stores a lookup-registry by .equals to a single instance by identity (double equals).
 * @param <T> - The type to be a registry for.
 */
public class IdentityRegistry<T> {

    private HashMap<T, T> instances;

    public IdentityRegistry() { instances = new HashMap<>(); }

    /**
     * Get the identity instance of the given impostor type (get identity (==) by equality (.equals()).
     * If no instance is found, then the provided impostor is stored as the identity for that "object type"
     *      by .equals, and will be used in future retrievals of identities.
     * This method is used to have all instances of some type "across the board" reference the same
     *      instance on the heap memory (keep singular identity).
     *      This is for memory-optimization.
     * @param impostor - The object to get an "identity instance" for (by .equals()).
     * @return the (identity) instance that was first registered that is also matching the provided impostor by .equals().
     */
    public T getIdentityOf(T impostor) {
        instances.putIfAbsent(impostor, impostor);
        return instances.get(impostor);
    }

}