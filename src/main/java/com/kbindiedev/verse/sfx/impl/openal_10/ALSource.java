package com.kbindiedev.verse.sfx.impl.openal_10;

import com.kbindiedev.verse.sfx.Sound;
import com.kbindiedev.verse.sfx.Source;
import org.lwjgl.openal.AL10;

public class ALSource extends Source {

    private boolean loop;
    private boolean playing;

    private int id;
    private ALSound sound;

    public ALSource(boolean loop) {
        this.loop = loop;
        playing = false;
        sound = null;

        id = AL10.alGenSources();
        AL10.alSource3f(id, AL10.AL_POSITION, 0f, 0f, 0f);
        AL10.alSource3f(id, AL10.AL_VELOCITY, 0f, 0f, 0f);
        AL10.alSourcef(id, AL10.AL_PITCH, 1);
        AL10.alSourcef(id, AL10.AL_GAIN, 1f);
        AL10.alSourcei(id, AL10.AL_LOOPING, loop ? AL10.AL_TRUE : AL10.AL_FALSE);
    }

    @Override
    public void setSound(Sound s) {
        if (!(s instanceof ALSound)) throw new RuntimeException("sound must be an ALSound");

        ALSound alSound = (ALSound)s;
        if (sound == alSound) return;

        AL10.alSourcei(id, AL10.AL_BUFFER, alSound.getId());
    }

    @Override
    public void play() {
        playing = true;
        AL10.alSourcePlay(id);
    }

    @Override
    public void stop() {
        playing = false;        // TODO: set false when audio end
        AL10.alSourceStop(id);
    }

    // TODO disposable
    public void dispose() {
        AL10.alDeleteSources(id);
    }

}
