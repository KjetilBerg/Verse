package com.kbindiedev.verse.input.keyboard;

import java.util.ArrayList;
import java.util.List;

/** A KeyEventListener that distributes events across many other KeyEventListeners (in order), until handled. */
public class KeyEventMultiplexerListener implements IKeyEventListener {

    private List<IKeyEventListener> listeners;

    public KeyEventMultiplexerListener() {
        listeners = new ArrayList<>();
    }

    public void addListener(IKeyEventListener listener) { listeners.add(listener); }
    public boolean removeListener(IKeyEventListener listener) { return listeners.remove(listener); }

    @Override
    public boolean keyDown(int keycode) {
        for (IKeyEventListener listener : listeners) {
            boolean handled = listener.keyDown(keycode);
            if (handled) return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (IKeyEventListener listener : listeners) {
            boolean handled = listener.keyUp(keycode);
            if (handled) return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(int keycode) {
        for (IKeyEventListener listener : listeners) {
            boolean handled = listener.keyTyped(keycode);
            if (handled) return true;
        }
        return false;
    }

}