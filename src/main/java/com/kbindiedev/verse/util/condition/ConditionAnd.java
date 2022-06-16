package com.kbindiedev.verse.util.condition;

import com.kbindiedev.verse.util.Properties;

/** Represents a condition about two other conditions having to be met. */
public class ConditionAnd extends Condition {

    private Condition c1, c2;

    public ConditionAnd(Condition c1, Condition c2) { this.c1 = c1; this.c2 = c2; }

    @Override
    public boolean pass(Properties properties) { return c1.pass(properties) && c2.pass(properties); }

    @Override
    public void success(Properties properties) { c1.success(properties); c2.success(properties); }

}
