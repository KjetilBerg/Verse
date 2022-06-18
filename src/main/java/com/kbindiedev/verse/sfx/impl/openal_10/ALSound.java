package com.kbindiedev.verse.sfx.impl.openal_10;

import com.kbindiedev.verse.sfx.Sound;

public class ALSound extends Sound {

    // TODO: self-generate, set volume etc
    private int id;

    public ALSound(int id) {
        this.id = id;
    }

    public int getId() { return id; }

}
