package com.kbindiedev.verse.state;

import com.kbindiedev.verse.util.condition.Condition;

/**
 * Describes a transition between two states.
 *
 * @param <T> - The state type.
 */
public class StateTransition<T, C extends StateContext> {

    private T stateFrom;
    private T stateTo;
    private Condition condition;

    public StateTransition(T stateFrom, T stateTo, Condition condition) {
        this.stateFrom = stateFrom;
        this.stateTo = stateTo;
        this.condition = condition;
    }

    public T getStateFrom() { return stateFrom; }
    public T getStateTo() { return stateTo; }

    /** @return whether or not this transition can be performed. */
    public boolean canTransition(C context) {
        return condition.pass(context.getProperties());
    }

    /**
     * Perform this transition. The transition may occur regardless of the value of {@link #canTransition(C)}.
     * @param context - The caller's context.
     * @param clean - True means this method was called because {@link #canTransition(StateContext)} returned true.
     *                  Otherwise, false means that this method was called disregarding the result of {@link #canTransition(StateContext)}.
     * @return the state that will be transitioned into.
     */
    public T makeTransition(C context, boolean clean) {
        if (clean) condition.success(context.getProperties());
        return stateTo;
    }

}