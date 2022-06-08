package com.kbindiedev.verse.input.keyboard;
import com.kbindiedev.verse.input.keyboard.event.KeyEvent;
import com.kbindiedev.verse.profiling.Assertions;

/**
 * Dispatches {@link com.kbindiedev.verse.input.keyboard.event.KeyEvent}.
 *
 * These events are generated by {@link KeyboardInputEventProcessor}.
 *
 * @see Keys
 */
public class KeyEventDispatcher {

    private static final IKeyEventListener BLANK_LISTENER = new IKeyEventListener() {
        @Override public boolean keyDown(int keycode) { return false; }
        @Override public boolean keyUp(int keycode) { return false; }
        @Override public boolean keyTyped(int keycode) { return false; }
        @Override public boolean keyDownNow(int keycode) { return false; }
        @Override public boolean keyUpNow(int keycode) { return false; }
    };

    private KeyboardInputEventProcessor processor;
    private IKeyEventListener listener;

    public KeyEventDispatcher(KeyboardInputEventProcessor processor) {
        this.processor = processor;
        this.listener = BLANK_LISTENER;
    }

    public void setListener(IKeyEventListener listener) {
        this.listener = listener;
    }

    /** Dispatch all events from this iteration. The same events will be dispatched again if this method is called before the processor's next iteration. */
    public void dispatch() {
        if (listener == BLANK_LISTENER) return;

        for (KeyEvent event : processor.getOutputEvents()) dispatchSingleEvent(event);
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
            case KEYDOWNNOW:
                listener.keyDownNow(event.getKeycode());
                break;
            case KEYUPNOW:
                listener.keyUpNow(event.getKeycode());
                break;
            default: Assertions.warn("unknown event type: %s", event.getType().name());
        }
    }

}