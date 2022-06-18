package com.kbindiedev.verse.sfx;

/** A "sound player". Describes position and velocity, amongst different playback settings. */
public abstract class Source {

    public abstract void play();

    public abstract void stop();

    public abstract void setSound(Sound sound); // TODO: throws WrongImplementationException ?

}
