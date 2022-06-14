package com.kbindiedev.verse.util.condition;

import com.kbindiedev.verse.util.Properties;

/** Represents a condition about a boolean value being true. */
public class ConditionBoolean extends Condition {

    private String key;
    private boolean target;

    public ConditionBoolean(String key) { this(key, true); }
    public ConditionBoolean(String key, boolean target) { this.key = key; this.target = target; }

    @Override
    public boolean pass(Properties properties) {
        return properties.getAsOrDefault(key, Boolean.class, Boolean.FALSE) == target;
    }
}