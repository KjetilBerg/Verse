package com.kbindiedev.verse.input.keyboard;

/** Used to describe classes that can handle keyboard input (or set as keyboard input processor) */
public interface IKeyEventListener {

    /**
     * Called when a certain key was pressed.
     * @param keycode - The keycode, {@link Keys}.
     * @return true if the event was handled, false otherwise.
     */
    boolean keyDown(int keycode);

    /**
     * Called when a certain key was released.
     * @param keycode - The keycode, {@link Keys}.
     * @return true if the event was handled, false otherwise.
     */
    boolean keyUp(int keycode);

    /**
     * Called when a certain key was typed.
     * A key is 'typed' when a keyboard sends an event to OS system input.
     * @param keycode - The keycode, {@link Keys}.
     * @return true if the event was handled, false otherwise.
     */
    boolean keyTyped(int keycode);

}
