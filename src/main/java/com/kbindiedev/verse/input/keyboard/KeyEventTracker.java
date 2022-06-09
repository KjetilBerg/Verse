package com.kbindiedev.verse.input.keyboard;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A handler for keyboard inputs.
 * Takes in KeyboardInputEvents and generates KeyEvents.
 * You can query this class for states.
 *
 * All inputs are related to what you find in {@link Keys}.
 *
 * @see Keys
 * @see KeyEventDispatcher
 */
public class KeyEventTracker implements IKeyEventListener {

    private enum KeyState { DOWN, UP, TYPED, DOWNNOW, UPNOW }

    private HashMap<Integer, KeyState> keyStates = new HashMap<>();   // key states by key code. null = released

    /**
     * Check if a key is pressed.
     * @param keycode - The keycode {@see Keys}
     * @return true if key by keycode is pressed, false otherwise
     */
    public boolean isKeyDown(int keycode) {
        KeyState state = getKeyState(keycode);
        return (state == KeyState.DOWN || state == KeyState.TYPED || state == KeyState.DOWNNOW);
    }

    /**
     * Check if a key is 'typed'.
     * A key is 'typed' when a keyboard sends an event to OS system input.
     * @param keycode - The keycode {@see Keys}
     * @return true if key by keycode is pressed, false otherwise
     */
    public boolean isKeyTyped(int keycode) {
        return getKeyState(keycode) == KeyState.TYPED;
    }

    /**
     * Check if a key was pressed this iteration (generally frame).
     * @param keycode - The keycode {@see Keys}.
     * @return true if key by keycode was pressed this iteration.
     */
    public boolean wasKeyPressedThisIteration(int keycode) {
        return getKeyState(keycode) == KeyState.DOWNNOW;
    }

    /**
     * Check if a key was released this iteration (generally frame).
     * @param keycode - The keycode {@see Keys}.
     * @return true if key by keycode was released this iteration.
     */
    public boolean wasKeyReleasedThisIteration(int keycode) {
        return getKeyState(keycode) == KeyState.UPNOW;
    }

    private KeyState getKeyState(int keycode) { return keyStates.getOrDefault(keycode, KeyState.UP); }

    @Override
    public boolean keyDown(int keycode) {
        keyStates.put(keycode, KeyState.DOWN);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        keyStates.put(keycode, KeyState.UP);
        return false;
    }

    @Override
    public boolean keyTyped(int keycode) {
        keyStates.put(keycode, KeyState.TYPED);
        return false;
    }

    @Override
    public boolean keyDownNow(int keycode) {
        keyStates.put(keycode, KeyState.DOWNNOW);
        return false;
    }

    @Override
    public boolean keyUpNow(int keycode) {
        keyStates.put(keycode, KeyState.UPNOW);
        return false;
    }
}