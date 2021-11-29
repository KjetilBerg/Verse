package com.kbindiedev.verse.gfx.strategy;

import com.kbindiedev.verse.profiling.exceptions.NotEnoughMemoryException;
import com.sun.istack.internal.Nullable;

public interface IBufferSlicable<T extends IBufferable & IBufferSlicable<T>> {

    /**
     * Creates some IBufferable that can be furtherly sliced.
     * @throws NotEnoughMemoryException - If a slice of the size 'numBytes' cannot be allocated.
     * @param numBytes - The size in bytes of the new slice.
     * @param occupant - The resource that will be occupying the new memory.
     */
    T slice(long numBytes, @Nullable IMemoryOccupant occupant) throws NotEnoughMemoryException;

    /** @return the biggest slice that can be formed currently (the highest value accepted by slice() as numBytes). Returns -1 if unlimited. May be 0. */
    long maxSliceSizeBytes();

}
