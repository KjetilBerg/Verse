package com.kbindiedev.verse.input.keyboard.event;

/**
 * Defines an event that was generated as a result of a keyboard doing something.
 *
 * @see com.kbindiedev.verse.input.keyboard.KeyboardInputEventProcessor
 */
public class KeyEvent {

    public enum KeyEventType { KEYDOWN, KEYUP, KEYTYPED, KEYDOWNNOW, KEYUPNOW }

    private KeyEventType type;
    private int keycode;

    public KeyEvent(KeyEventType type, int keycode) {
        this.type = type;
        this.keycode = keycode;
    }

    public KeyEventType getType() { return type; }
    public int getKeycode() { return keycode; }

}