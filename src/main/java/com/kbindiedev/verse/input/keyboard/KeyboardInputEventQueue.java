package com.kbindiedev.verse.input.keyboard;

import com.kbindiedev.verse.input.keyboard.event.KeyboardInputEvent;

import java.util.LinkedList;
import java.util.Queue;

/** Responsible for making and queueing {@link com.kbindiedev.verse.input.keyboard.event.KeyboardInputEvent} */
public class KeyboardInputEventQueue {

    private Queue<KeyboardInputEvent> queue;

    public KeyboardInputEventQueue() {
        queue = new LinkedList<>();
    }

    /** Get all queued events. */
    public Queue<KeyboardInputEvent> getQueuedEvents() { return queue; }

    /** Empty the queue of all events */
    public void clear() { queue.clear(); }

    /**
     * Queue a KeyEvent that a certain key was pressed.
     * This method must not be called again with the same keycode before .queueKeyUp has been called with that keycode.
     * @param keycode - The keycode {@see Keys}
     */
    public void queueKeyDown(int keycode) {
        queue.add(new KeyboardInputEvent(KeyboardInputEvent.KeyboardInputEventType.KEYDOWN, keycode));
    }

    /**
     * Queue a KeyEvent that a certain key was released.
     * This method must not be called again with the same keycode before .queueKeyDown has been called with that keycode.
     * @param keycode - The keycode {@see Keys}
     */
    public void queueKeyUp(int keycode) {
        queue.add(new KeyboardInputEvent(KeyboardInputEvent.KeyboardInputEventType.KEYUP, keycode));
    }

    /**
     * Queue a KeyEvent that a certain key was typed. A key is 'typed' when a keyboard sends an event to OS system input.
     *                                          (may vary between keyboards, and maybe some operating systems)
     * @param keycode - The keycode {@see Keys}
     */
    public void queueKeyTyped(int keycode) {
        queue.add(new KeyboardInputEvent(KeyboardInputEvent.KeyboardInputEventType.KEYTYPED, keycode));
    }

}