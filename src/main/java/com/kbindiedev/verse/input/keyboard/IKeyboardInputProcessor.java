package com.kbindiedev.verse.input.keyboard;

/** Used to describe classes that can handle keyboard input (or set as keyboard input processor) */
public interface IKeyboardInputProcessor {

    /**
     * Called when a certain key was pressed.
     * The keycode is in accordance the Keys class {@see Keys}.
     * @param keycode - The keycode.
     * @return true if the event was handled, false otherwise.
     */
    boolean keyDown(int keycode);

    /**
     * Called when a certain key was released.
     * The keycode is in accordance the Keys class {@see Keys}.
     * @param keycode - The keycode.
     * @return true if the event was handled, false otherwise.
     */
    boolean keyUp(int keycode);

    /**
     * Called when a certain key was typed.
     * A key is 'typed' when a keyboard sends an event to OS system input.
     * The keycode is in accordance the Keys class {@see Keys}.
     * @param keycode - The keycode.
     * @return true if the event was handled, false otherwise.
     */
    boolean keyTyped(int keycode);

}
