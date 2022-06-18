package com.kbindiedev.verse.sfx;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

/** The overarching definition of implementations that create sound/audio. */
public abstract class SoundEngine {

    /** Will be called before the engine is utilized. */
    public abstract void initialize(SoundEngineSettings settings);

    public abstract Sound createSound(String filepath) throws UnsupportedAudioFileException, IOException; // TODO: temp??

    public abstract Source createSource();

    public abstract Listener createListener();

}
