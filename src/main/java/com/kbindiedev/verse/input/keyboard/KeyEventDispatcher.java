package com.kbindiedev.verse.input.keyboard;

import com.kbindiedev.verse.input.keyboard.event.KeyEvent;
import com.kbindiedev.verse.profiling.Assertions;

import java.util.List;

/**
 * Dispatches {@link com.kbindiedev.verse.input.keyboard.event.KeyEvent}.
 *
 * @see Keys
 */
public class KeyEventDispatcher {

    private static final IKeyEventListener BLANK_LISTENER = new IKeyEventListener() {
        @Override public boolean keyDown(int keycode) { return false; }
        @Override public boolean keyUp(int keycode) { return false; }
        @Override public boolean keyTyped(int keycode) { return false; }
    };

    private IKeyEventListener listener;

    public KeyEventDispatcher() {
        this.listener = BLANK_LISTENER;
    }

    public void setListener(IKeyEventListener listener) {
        this.listener = listener;
    }

    /** Dispatch all events from this iteration. The same events will be dispatched again if this method is called before the processor's next iteration. */
    public void dispatch(List<KeyEvent> events) {
        if (listener == BLANK_LISTENER) return;

        for (KeyEvent event : events) dispatchSingleEvent(event);
    }

    /** Dispatch a single event */
    private void dispatchSingleEvent(KeyEvent event) {
        switch (event.getType()) {
            case KEYDOWN:
                listener.keyDown(event.getKeycode());
                break;
            case KEYUP:
                listener.keyUp(event.getKeycode());
                break;
            case KEYTYPED:
                listener.keyTyped(event.getKeycode());
                break;
            default: Assertions.warn("unknown event type: %s", event.getType().name());
        }
    }

}