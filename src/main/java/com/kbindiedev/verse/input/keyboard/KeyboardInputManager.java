package com.kbindiedev.verse.input.keyboard;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * All keyboard inputs from varying implementations are ultimately sent here, for this class to then dispatch
 * All events are dispatched right before the game .update function is run.
 * Note: comments and method names describe everything on a per-frame-basis, though the actual definition is
 *      per-handleEvents-method-is-run-basis. This SHOULD happen once per frame, right before the global game .update
 *      method is executed.
 */
//TODO: maybe make non-static
public class KeyboardInputManager {

    private static HashMap<Integer, Boolean> keyStates = new HashMap<>();
    private static HashSet<Integer> keyChangesThisFrame = new HashSet<>(); //map of keys that changed their state this frame
    private static ArrayList<KeyEvent> unhandledEvents = new ArrayList<>(); //events are piled and handled once per frame

    /** Initialize processor to blank */
    private static IKeyboardInputProcessor processor = new IKeyboardInputProcessor() {
        @Override public boolean keyDown(int keycode) { return false; }
        @Override public boolean keyUp(int keycode) { return false; }
        @Override public boolean keyTyped(int keycode) { return false; }
    };

    /** Set the IKeyboardInputProcessor system wide. */
    public static void setProcessor(IKeyboardInputProcessor p) { processor = p; }

    /**
     * Check whether a key is pressed.
     * Note that if checked during event dispatch, registry may not be "up-to-date" with current state of final frame.
     *      Things are guaranteed to be "up-to-date" once the global game .update method is executed, though.
     * @param keycode - The keycode {@see Keys}
     * @return true if key by keycode is pressed, false otherwise
     */
    public static boolean isKeyDown(int keycode) { return keyStates.getOrDefault(keycode, false); }

    /**
     * Check whether a key was pressed this frame.
     * Note that if checked during event dispatch, registry may not be "up-to-date" with current state of final frame.
     *      Things are guaranteed to be "up-to-date" once the global game .update method is executed, though.
     * @param keycode - The keycode {@see Keys}
     * @return true if key by keycode was pressed this frame
     */
    public static boolean wasKeyPressedThisFrame(int keycode) {
        return keyChangesThisFrame.contains(keycode) && keyStates.get(keycode);
    }

    /**
     * Check whether a key was released this frame.
     * Note that if checked during event dispatch, registry may not be "up-to-date" with current state of final frame.
     *      Things are guaranteed to be "up-to-date" once the global game .update method is executed, though.
     * @param keycode - The keycode {@see Keys}
     * @return true if key by keycode was released this frame
     */
    public static boolean wasKeyReleasedThisFrame(int keycode) {
        return keyChangesThisFrame.contains(keycode) && !keyStates.get(keycode);
    }


    /**
     * Handle all unhandled events. Should happen once per frame.
     * This will also notify the processor of all events that have happened since last call to this function.
     * Note that all events are handled "in-order".
     *      This means if there are several events during a single frame, the .isKeyPressed and such functions
     *      that depend on the event registry, may not be "up-to-date" with events that are yet to be dispatched this frame.
     */
    public static void handleEvents() {
        keyChangesThisFrame.clear();

        for (KeyEvent event : unhandledEvents) {

            //check registry
            if (keyStates.containsKey(event.keycode)) {
                boolean bad = false;
                if (event.type == KeyEvent.KeyEventType.KEYDOWN && keyStates.get(event.keycode)) bad = true;
                if (event.type == KeyEvent.KeyEventType.KEYUP && !keyStates.get(event.keycode)) bad = true;

                if (bad) {
                    Assertions.warn("keycode: '%d' got event: '%s', but is already in that state (this should not happen). ignoring event...", event.keycode, event.type.name());
                    continue;
                }
            }

            //update keyState registry
            if (event.type == KeyEvent.KeyEventType.KEYDOWN) keyStates.put(event.keycode, true);
            else if (event.type == KeyEvent.KeyEventType.KEYUP) keyStates.put(event.keycode, false);

            //update keyChangesThisFrame registry
            if (event.type == KeyEvent.KeyEventType.KEYDOWN || event.type == KeyEvent.KeyEventType.KEYUP)
                keyChangesThisFrame.add(event.keycode);

            //dispatch events
            switch (event.type) {
                case KEYDOWN:
                    processor.keyDown(event.keycode);
                    break;
                case KEYUP:
                    processor.keyUp(event.keycode);
                    break;
                case KEYTYPED:
                    processor.keyTyped(event.keycode);
                    break;
                default: Assertions.error("unknown event type: %s", event.type.name());
            }
        }

        unhandledEvents.clear();
    }

    /**
     * Notify that a certain key was pressed. MUST not be called again before .notifyKeyUp has been called.
     * The keycode is in accordance to the Keys class {@see Keys}.
     * @param keycode - The keycode {@see Keys}
     */
    public static void notifyKeyDown(int keycode) {
        unhandledEvents.add(new KeyEvent(KeyEvent.KeyEventType.KEYDOWN, keycode));
    }

    /**
     * Notify that a certain key was released. MUST not be called again before .notifyKeyDown has been called.
     * The keycode is in accordance to the Keys class {@see Keys}.
     * @param keycode - The keycode {@see Keys}
     */
    public static void notifyKeyUp(int keycode) {
        unhandledEvents.add(new KeyEvent(KeyEvent.KeyEventType.KEYUP, keycode));
    }

    //Note: if future implementations cause trouble, this can be emulated
    /**
     * Notify that a certain key was typed. A key is 'typed' when a keyboard sends an event to OS system input.
     *                                          (may vary between keyboard, and maybe some operating systems)
     * The keycode is in accordance to the Keys class {@see Keys}.
     * @param keycode - The keycode {@see Keys}
     */
    public static void notifyKeytyped(int keycode) {
        unhandledEvents.add(new KeyEvent(KeyEvent.KeyEventType.KEYTYPED, keycode));
    }


    private static class KeyEvent {
        enum KeyEventType { KEYDOWN, KEYUP, KEYTYPED }

        private KeyEventType type;
        private int keycode;

        KeyEvent(KeyEventType type, int keycode) { this.type = type; this.keycode = keycode; }
    }

}
