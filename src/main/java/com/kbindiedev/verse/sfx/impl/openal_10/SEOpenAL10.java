package com.kbindiedev.verse.sfx.impl.openal_10;

import com.kbindiedev.verse.io.files.Files;
import com.kbindiedev.verse.sfx.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.EXTEfx.ALC_MAX_AUXILIARY_SENDS;

/** Sound Engine Open AL 1.0. */
public class SEOpenAL10 extends SoundEngine {

    // TODO extract. Most of this is based off of //https://michaelericoberlin.wordpress.com/2016/07/04/effective-openal-with-lwjgl-3/

    // TODO: support mp3

    private long device; // TODO TEMP (1 device)

    @Override
    public void initialize(SoundEngineSettings settings) {

        // create device
        device = ALC10.alcOpenDevice((ByteBuffer)null);
        ALCCapabilities capabilities = ALC.createCapabilities(device);

        // setup context
        IntBuffer contextAttributeList = BufferUtils.createIntBuffer(16);
        contextAttributeList.put(ALC_REFRESH);
        contextAttributeList.put(60);
        contextAttributeList.put(ALC_SYNC);
        contextAttributeList.put(ALC_FALSE);
        contextAttributeList.put(ALC_MAX_AUXILIARY_SENDS);
        contextAttributeList.put(2);
        contextAttributeList.put(0);
        contextAttributeList.flip();
        long context = ALC10.alcCreateContext(device, contextAttributeList);
        if (!ALC10.alcMakeContextCurrent(context)) throw new RuntimeException("failed to make audio context current");
        AL.createCapabilities(capabilities);

        // set listener.
        AL10.alListener3f(AL10.AL_VELOCITY, 0f, 0f, 0f);
        AL10.alListener3f(AL10.AL_ORIENTATION, 0f, 0f, -1f); // TODO: 1f value3?
    }

    @Override
    public Sound createSound(String filepath) throws UnsupportedAudioFileException, IOException {
        IntBuffer buffer = BufferUtils.createIntBuffer(1);
        AL10.alGenBuffers(buffer);

        long time = createBufferData(buffer.get(0), filepath);

        return new ALSound(buffer.get(0));
    }

    @Override
    public Source createSource(boolean looping) {
        return new ALSource(looping);
    }

    @Override
    public Listener createListener() {
        return null;
    }

    public void testrun() {
        try {
            long device = ALC10.alcOpenDevice((ByteBuffer)null);
            ALCCapabilities alc = ALC.createCapabilities(device);
            IntBuffer contextAttribList = BufferUtils.createIntBuffer(16);
            contextAttribList.put(ALC_REFRESH);
            contextAttribList.put(60);
            contextAttribList.put(ALC_SYNC);
            contextAttribList.put(ALC_FALSE);
            contextAttribList.put(ALC_MAX_AUXILIARY_SENDS);
            contextAttribList.put(2);
            contextAttribList.put(0);
            contextAttribList.flip();
            long context = ALC10.alcCreateContext(device, contextAttribList);
            if (!ALC10.alcMakeContextCurrent(context)) throw new RuntimeException("failed to make audio context current");
            AL.createCapabilities(alc);

            AL10.alListener3f(AL10.AL_VELOCITY, 0f, 0f, 0f);
            AL10.alListener3f(AL10.AL_ORIENTATION, 0f, 0f, -1f);

            IntBuffer buffer = BufferUtils.createIntBuffer(1);
            AL10.alGenBuffers(buffer);

            long time = createBufferData(buffer.get(0), "../sound.wav");

            int source = AL10.alGenSources();
            AL10.alSourcei(source, AL10.AL_BUFFER, buffer.get(0));
            AL10.alSource3f(source, AL10.AL_POSITION, 0f, 0f, 0f);
            AL10.alSource3f(source, AL10.AL_VELOCITY, 0f, 0f, 0f);
            AL10.alSourcef(source, AL10.AL_PITCH, 1);
            AL10.alSourcef(source, AL10.AL_GAIN, 1f);
            AL10.alSourcei(source, AL10.AL_LOOPING, AL10.AL_FALSE);

            AL10.alSourcePlay(source);

            try { Thread.sleep(2000); } catch (InterruptedException e) { e.printStackTrace(); }
            AL10.alSourceStop(source);
            AL10.alDeleteSources(source);

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }


    //TODO temp
    private long createBufferData(int p, String filepath) throws UnsupportedAudioFileException, IOException {
        //shortcut finals:
        final int MONO = 1, STEREO = 2;

        //AudioInputStream stream = AudioSystem.getAudioInputStream(this.getClass().getResource("../test.wav"));
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filepath));

        AudioFormat format = stream.getFormat();
        if(format.isBigEndian()) throw new UnsupportedAudioFileException("Can't handle Big Endian formats yet");

        //load stream into byte buffer
        int openALFormat = -1;
        switch(format.getChannels()) {
            case MONO:
                switch(format.getSampleSizeInBits()) {
                    case 8:
                        openALFormat = AL10.AL_FORMAT_MONO8;
                        break;
                    case 16:
                        openALFormat = AL10.AL_FORMAT_MONO16;
                        break;
                }
                break;
            case STEREO:
                switch(format.getSampleSizeInBits()) {
                    case 8:
                        openALFormat = AL10.AL_FORMAT_STEREO8;
                        break;
                    case 16:
                        openALFormat = AL10.AL_FORMAT_STEREO16;
                        break;
                }
                break;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int d;
        while ((d = stream.read(buf)) != -1) bos.write(buf, 0, d);

        byte[] b = bos.toByteArray();
        ByteBuffer data = BufferUtils.createByteBuffer(b.length).put(b);
        data.flip();

        //load audio data into appropriate system space....
        AL10.alBufferData(p, openALFormat, data, (int)format.getSampleRate());

        //and return the rough notion of length for the audio stream!
        return (long)(1000f * stream.getFrameLength() / format.getFrameRate());
    }
}
