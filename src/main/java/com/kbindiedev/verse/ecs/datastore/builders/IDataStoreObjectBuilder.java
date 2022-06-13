package com.kbindiedev.verse.ecs.datastore.builders;

/**
 * Implementors of this interface must produce instances of some class from the 'datastore' package.
 * Any such implementor must be reusable.
 *
 * @param <T> - The class that this builder produces.
 *
 * @see com.kbindiedev.verse.ecs.datastore
 */
public interface IDataStoreObjectBuilder<T> {

    /**
     * @param reset - Whether or not {@link #reset()} should be called after building the object.
     * @return the built object. if called multiple times, then a new instance must be built each time.
     */
    T build(boolean reset);

    /** Reset this builder back to it's default starting state. */
    void reset();

}
