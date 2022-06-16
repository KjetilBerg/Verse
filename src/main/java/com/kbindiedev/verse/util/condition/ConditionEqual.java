package com.kbindiedev.verse.util.condition;

import com.kbindiedev.verse.util.Properties;

/** Represents a condition about a boolean value being true. */
public class ConditionEqual<T> extends Condition {

    private String key;
    private T target;

    public ConditionEqual(String key, T mustEqual) { this.key = key; this.target = mustEqual; }

    @Override
    public boolean pass(Properties properties) {
        return target.equals(properties.getAs(key, target.getClass()));
    }

    @Override
    public void success(Properties properties) {}
}