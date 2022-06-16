package com.kbindiedev.verse.util;

/** A class that can be "triggered". */
public class Trigger<T> {

    private boolean triggered;

    public Trigger() {
        triggered = false;
    }

    /** Trigger this trigger. */
    public void trigger() { triggered = true; }

    /** @return whether or not this Trigger is triggered. */
    public boolean poll() { return triggered; }

    /**
     * Indicate to this trigger that it contributed in "doing something".
     * Different from {@link #reset()} as in this method should only be called if the call
     *      to {@link #poll()} caused something to happen.
     */
    public void sprung() { reset(); }

    /** Reset this Trigger. */
    public void reset() { triggered = false; }

}