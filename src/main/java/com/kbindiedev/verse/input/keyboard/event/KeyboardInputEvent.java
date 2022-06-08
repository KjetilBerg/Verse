package com.kbindiedev.verse.input.keyboard.event;

/** Defines an event that a keyboard did something. */
public class KeyboardInputEvent {

    public enum KeyboardInputEventType { KEYDOWN, KEYUP, KEYTYPED }

    private KeyboardInputEventType type;
    private int keycode;

    public KeyboardInputEvent(KeyboardInputEventType type, int keycode) {
        this.type = type;
        this.keycode = keycode;
    }

    public KeyboardInputEventType getType() { return type; }
    public int getKeycode() { return keycode; }

}
