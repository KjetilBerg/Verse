package com.kbindiedev.verse.util.condition;

import com.kbindiedev.verse.util.Properties;

/** Describes a condition. */
public abstract class Condition {

    public static final Condition NONE = new Condition() {
        @Override public boolean pass(Properties properties) { return true; }
        @Override public void success(Properties properties) {}
    };

    /** @return whether or not the condition is met. */
    public abstract boolean pass(Properties properties);

    /** indicates that as a result of this condition passing, that something happened. */
    public abstract void success(Properties properties);

}