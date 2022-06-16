package com.kbindiedev.verse.state;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.util.condition.Condition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A collection of transitions between a set of possible states.
 * @param <T> - The state types.
 * @param <C> - The context type for transitions that manage the given types.
 */
public class StateMap<T, C extends StateContext> {

    private HashMap<T, List<StateTransition<T, C>>> transitions;

    public StateMap() {
        transitions = new HashMap<>();
    }

    public void addTransition(StateTransition<T, C> transition) {
        T stateFrom = transition.getStateFrom();
        if (!transitions.containsKey(stateFrom)) transitions.put(stateFrom, new ArrayList<>());
        transitions.get(stateFrom).add(transition);
    }

    public void setEntryState(T state) {
        transitions.remove(null);
        addTransition(new StateTransition<>(null, state, Condition.NONE));
    }

    public boolean hasState(T state) { return transitions.containsKey(state); }

    public T pickState(T currentState, C context) {

        int maxIterations = 50;
        while (maxIterations-- > 0) {
            if (!transitions.containsKey(currentState)) return currentState;

            boolean changed = false;
            for (StateTransition<T, C> transition : transitions.get(currentState)) {
                if (transition.canTransition(context)) {
                    currentState = transition.makeTransition(context, true);
                    changed = true;
                    break;
                }
            }

            if (!changed) return currentState;
        }

        Assertions.warn("exceeded maximum number of iterations (possible infinite loop)");
        return currentState;

    }

}