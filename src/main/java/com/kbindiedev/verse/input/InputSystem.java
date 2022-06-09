package com.kbindiedev.verse.input;

import com.kbindiedev.verse.input.keyboard.KeyboardInputPipeline;

/** An overarching class for managing all Inputs. */
public class InputSystem {

    private KeyboardInputPipeline keyboardPipeline;

    public InputSystem() { this (new KeyboardInputPipeline()); }
    public InputSystem(KeyboardInputPipeline keyboardPipeline) {
        this.keyboardPipeline = keyboardPipeline;
    }

    public KeyboardInputPipeline getKeyboardPipeline() { return keyboardPipeline; }

    /** Iterate all input systems. */
    public void iterate() {
        keyboardPipeline.iterate();
    }

}