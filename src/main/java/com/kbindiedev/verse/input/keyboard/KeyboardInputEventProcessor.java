package com.kbindiedev.verse.input.keyboard;

import com.kbindiedev.verse.input.keyboard.event.KeyEvent;
import com.kbindiedev.verse.input.keyboard.event.KeyboardInputEvent;
import com.kbindiedev.verse.profiling.Assertions;

import java.util.HashMap;
import java.util.Queue;

/**
 * Takes in {@link KeyboardInputEvent} and generates {@link KeyEvent}.
 * Keeps key states for internal used that can also be queried by the user.
 *
 * If a key was "pressed this iteration" then a KeyEvent = KEYDOWN, as well as a KeyEvent = KEYDOWNNOW will be generated.
 * If a key was "released this iteration" then a KeyEvent = KEYUP, as well as a KeyEvent = KEYUPNOW will be generated.
 *
 * @see Keys
 * // TODO: more see
 */
public class KeyboardInputEventProcessor {

    private HashMap<Integer, Boolean> keyStates;   // key states by key code. null = released
    private Queue<KeyEvent> outputEvents;          // generated events by "iterate"

    public KeyboardInputEventProcessor() {
        keyStates = new HashMap<>();
    }

    public Queue<KeyEvent> getOutputEvents() { return outputEvents; }

    /**
     * Perform a single iteration.
     * Will transform the given queue of {@link KeyboardInputEvent} into another queue of {@link KeyEvent}
     * All events are handled "in-order".
     */
    public void iterate(Queue<KeyboardInputEvent> events) {
        outputEvents.clear();
        for (KeyboardInputEvent event : events) handleSingleEvent(event);
    }

    /**
     * Handle a single KeyboardInputEvent by adjusting the registry.
     * The event is validated before being handled. See: {@link #validateEventTowardsRegistry(KeyboardInputEvent)}.
     */
    private void handleSingleEvent(KeyboardInputEvent event) {
        if (!validateEventTowardsRegistry(event)) return;

        // apply event itself
        // these have the same names, but correspond to different classes. unfortunate.
        KeyEvent.KeyEventType type = null;
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYDOWN) type = KeyEvent.KeyEventType.KEYDOWN;
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYUP) type = KeyEvent.KeyEventType.KEYUP;
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYTYPED) type = KeyEvent.KeyEventType.KEYTYPED;
        outputEvents.add(new KeyEvent(type, event.getKeycode()));

        // was key pressed/released this iteration?
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYDOWN && !isKeyDown(event.getKeycode()))
            outputEvents.add(new KeyEvent(KeyEvent.KeyEventType.KEYDOWNNOW, event.getKeycode()));
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYUP && isKeyDown(event.getKeycode()))
            outputEvents.add(new KeyEvent(KeyEvent.KeyEventType.KEYUPNOW, event.getKeycode()));

        // adjust registry
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYDOWN) keyStates.put(event.getKeycode(), true);
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYUP) keyStates.put(event.getKeycode(), false);

    }

    /**
     * Check that the given event does not introduce a conflicting keystate (in other words, cannot "press" if already pressed and vice versa).
     * @param event - The event.
     * @return true if event is "ok", false otherwise.
     */
    private boolean validateEventTowardsRegistry(KeyboardInputEvent event) {
        boolean ok = true;
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYDOWN && isKeyDown(event.getKeycode())) ok = false;
        if (event.getType() == KeyboardInputEvent.KeyboardInputEventType.KEYUP && !isKeyDown(event.getKeycode())) ok = false;

        if (!ok)
            Assertions.warn("keycode: '%d' got event: '%s', but is already in that state (this should not happen). ignoring event...",
                    event.getKeycode(), event.getType().name());

        return ok;
    }

    /**
     * Check if a key is pressed.
     * @param keycode - The keycode {@see Keys}
     * @return true if key by keycode is pressed, false otherwise
     */
    public boolean isKeyDown(int keycode) { return keyStates.getOrDefault(keycode, false); }

}
