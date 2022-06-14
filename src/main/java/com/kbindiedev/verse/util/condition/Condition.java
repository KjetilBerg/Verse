package com.kbindiedev.verse.util.condition;

import com.kbindiedev.verse.util.Properties;

/** Describes a condition. */
public abstract class Condition {

    /** @return whether or not the condition is met. */
    public abstract boolean pass(Properties properties);

}