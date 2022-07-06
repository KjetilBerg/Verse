package com.kbindiedev.verse.input.keyboard.event;

/**
 * Defines an event that was generated as a result of a keyboard doing something.
 *
 * @see com.kbindiedev.verse.input.keyboard.KeyboardInputEventQueue
 */
public class KeyEvent {

    public enum KeyEventType { KEYDOWN, KEYUP, KEYTYPED }

    private KeyEventType type;
    private int keycode;

    public KeyEvent(KeyEventType type, int keycode) {
        this.type = type;
        this.keycode = keycode;
    }

    public KeyEventType getType() { return type; }
    public int getKeycode() { return keycode; }

    @Override
    public String toString() {
        return "KeyEvent[ type=" + type.name() + ", keycode=" + keycode + " ]";
    }

}