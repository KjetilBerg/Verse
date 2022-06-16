package com.kbindiedev.verse.state;

import com.kbindiedev.verse.profiling.Assertions;

/**
 * Describes something being in a particular state.
 *
 * @param <T> - The state types.
 * @param <C> - The context for the given transitions.
 */
public class StateMachine<T, C extends StateContext> {

    private StateMap<T, C> states;
    private C context;
    private T currentState;

    public StateMachine(StateMap<T, C> states, C context) {
        this.states = states;
        this.context = context;
        currentState = states.pickState(null, context);
        if (currentState == null) Assertions.warn("states has no entry state");
    }

    public T pickState() {
        currentState = states.pickState(currentState, context);
        return currentState;
    }

    public T getLastTransitionedState() {
        return currentState;
    }

    public C getContext() { return context; }

}
