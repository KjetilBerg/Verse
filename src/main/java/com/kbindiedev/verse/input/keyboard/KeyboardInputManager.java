package com.kbindiedev.verse.input.keyboard;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * All inputs from varying implementations are ultimately sent here, for this class to then dispatch
 * All keycode events are dispatched right before the game .update function is run.
 */
public class KeyboardInputManager {

    private static HashMap<Integer, Boolean> keyStates = new HashMap<>();
    private static ArrayList<KeyEvent> unhandledEvents = new ArrayList<>();

    /** Initialize processor to blank */
    private static IKeyboardInputProcessor processor = new IKeyboardInputProcessor() {
        @Override public boolean keyDown(int keycode) { return false; }
        @Override public boolean keyUp(int keycode) { return false; }
        @Override public boolean keyTyped(int keycode) { return false; }
    };

    public static void setProcessor(IKeyboardInputProcessor p) { processor = p; }


    /**
     * Handle all unhandled events. Should happen once per frame.
     * This will also notify the processor of all events that have happened since last call to this function.
     * Note that all events are handled "in-order".
     *      This means if there are several input events during a single frame, the .isKeyPressed and such functions
     *      that depend on the event registry, will NOT be "up-to-date" with events that are yet to be dispatched this frame.
     */
    public static void handleEvents() {
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
     * Notify that a certain key was pressed. MUST not be called again before .notifyKeyup has been called.
     * The keycode is in accordance to whatever is described in this file {@see Keys}.
     *      Keycodes are inspired by the GLFW standard https://www.glfw.org/docs/3.3/group__keys.html.
     * @param keycode - The keycode.
     */
    public static void notifyKeydown(int keycode) {
        unhandledEvents.add(new KeyEvent(KeyEvent.KeyEventType.KEYDOWN, keycode));
    }

    /**
     * Notify that a certain key was released. MUST not be called again before .notifyKeydown has been called.
     * The keycode is in accordance to whatever is described in this file {@see Keys}.
     *      Keycodes are inspired by the GLFW standard https://www.glfw.org/docs/3.3/group__keys.html.
     * @param keycode - The keycode.
     */
    public static void notifyKeyup(int keycode) {
        unhandledEvents.add(new KeyEvent(KeyEvent.KeyEventType.KEYUP, keycode));
    }

    //Note: if future implementations cause trouble, this can be emulated
    /**
     * Notify that a certain key was typed. A key is 'typed' when a keyboard sends an event to OS system input.
     *                                          (may vary between keyboard, and maybe some operating systems)
     * The keycode is in accordance to whatever is described in this file {@see Keys}.
     *      Keycodes are inspired by the GLFW standard https://www.glfw.org/docs/3.3/group__keys.html.
     * @param keycode - The keycode.
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
