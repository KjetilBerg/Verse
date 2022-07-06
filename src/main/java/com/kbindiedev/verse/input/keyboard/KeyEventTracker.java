package com.kbindiedev.verse.input.keyboard;

import com.kbindiedev.verse.input.keyboard.event.KeyEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Keeps track of KeyEvents and allows you to check key states.
 * Also allows you to check for several key presses during a single iteration (very niche use case).
 *
 * All inputs are related to what you find in {@link Keys}.
 *
 * @see Keys
 * @see KeyEventDispatcher
 */
public class KeyEventTracker implements IKeyEventListener {

    private HashMap<Integer, Boolean> keyStates = new HashMap<>();          // key states by key code. true = down, false = up, null = up
    private HashMap<Integer, Integer> timesTypedThisIteration = new HashMap<>(); // key code to "number of times typed this iteration".
    private HashMap<Integer, Integer> timesDownThisIteration = new HashMap<>();  // key code to "number of times down  this iteration".
    private HashMap<Integer, Integer> timesUpThisIteration = new HashMap<>();    // key code to "number of times up    this iteration".
    private List<KeyEvent> allEventsThisIteration = new LinkedList<>();

    /**
     * Check if a key is pressed.
     * @param keycode - The keycode {@see Keys}.
     * @return true if key by keycode is pressed, false otherwise.
     */
    public boolean isKeyDown(int keycode) {
        return keyStates.getOrDefault(keycode, false);
    }

    /**
     * Check if a key was pressed this iteration (generally frame).
     * @param keycode - The keycode {@see Keys}.
     * @return true if key by keycode was pressed this iteration.
     */
    public boolean wasKeyPressedThisIteration(int keycode) {
        return timesKeyDownThisIteration(keycode) > 0;
    }

    /**
     * Check if a key was released this iteration (generally frame).
     * @param keycode - The keycode {@see Keys}.
     * @return true if key by keycode was released this iteration.
     */
    public boolean wasKeyReleasedThisIteration(int keycode) {
        return timesKeyUpThisIteration(keycode) > 0;
    }

    /**
     * Check if a key is 'typed'.
     * A key is 'typed' when a keyboard sends an event to OS system input.
     * @param keycode - The keycode {@see Keys}
     * @return true if key by keycode is pressed, false otherwise
     */
    public boolean wasKeyTypedThisIteration(int keycode) {
        return timesKeyTypedThisIteration(keycode) > 0;
    }

    /**
     * Get the number of times a key was pressed this iteration.
     * @param keycode - The keycode {@link Keys}.
     * @return the number of times a key was pressed this iteration, 0 or more.
     */
    public int timesKeyDownThisIteration(int keycode) {
        return timesDownThisIteration.getOrDefault(keycode, 0);
    }

    /**
     * Get the number of times a key was released this iteration.
     * @param keycode - The keycode {@link Keys}.
     * @return the number of times a key was released this iteration, 0 or more.
     */
    public int timesKeyUpThisIteration(int keycode) {
        return timesUpThisIteration.getOrDefault(keycode, 0);
    }

    /**
     * Get the number of times a key was typed this iteration.
     * @param keycode - The keycode {@link Keys}.
     * @return the number of times a key was typed this iteration, 0 or more.
     */
    public int timesKeyTypedThisIteration(int keycode) {
        return timesTypedThisIteration.getOrDefault(keycode, 0);
    }

    public List<KeyEvent> getAllEventsThisIteration() { return allEventsThisIteration; }

    @Override
    public boolean keyDown(int keycode) {
        keyStates.put(keycode, true);
        timesDownThisIteration.put(keycode, timesDownThisIteration.getOrDefault(keycode, 0) + 1);
        allEventsThisIteration.add(new KeyEvent(KeyEvent.KeyEventType.KEYDOWN, keycode));
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyStates.put(keycode, false);
        timesUpThisIteration.put(keycode, timesUpThisIteration.getOrDefault(keycode, 0) + 1);
        allEventsThisIteration.add(new KeyEvent(KeyEvent.KeyEventType.KEYUP, keycode));
        return false;
    }

    @Override
    public boolean keyTyped(int keycode) {
        timesTypedThisIteration.put(keycode, timesTypedThisIteration.getOrDefault(keycode, 0) + 1);
        allEventsThisIteration.add(new KeyEvent(KeyEvent.KeyEventType.KEYTYPED, keycode));
        return false;
    }

    /** Reset current iteration. */
    public void iterate() {
        timesDownThisIteration.clear();
        timesUpThisIteration.clear();
        timesTypedThisIteration.clear();
        allEventsThisIteration.clear();
    }

}