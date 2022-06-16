package com.kbindiedev.verse.util.condition;

import com.kbindiedev.verse.util.Properties;
import com.kbindiedev.verse.util.Trigger;

// TODO: nevermind, use AnimationStateMachine

/** A Trigger must be active for this condition to be met. */
public class ConditionTrigger extends Condition {

    private String name;

    public ConditionTrigger(String name) { this.name = name; }

    @Override
    public boolean pass(Properties properties) {
        Trigger trigger = properties.getAs(name, Trigger.class);
        if (trigger == null) return false;
        return trigger.poll();
    }

    @Override
    public void success(Properties properties) {
        Trigger trigger = properties.getAs(name, Trigger.class);
        if (trigger != null) trigger.sprung();
    }
}