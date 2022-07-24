package com.kbindiedev.verse.io.comms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** An interface representing a communication channel. */
public interface ICommunicationChannel {

    /** @return this channel's stream to where I can write data. The stream may be blocking. */
    OutputStream getWritingStream() throws IOException;

    /** @return this channel's stream from where I can read data. The stream may be blocking. */
    InputStream getReadingStream() throws IOException;

    /**
     * Close this office.
     * After closing, no further data may be written to the stream from {@link #getWritingStream()}.
     * The implementor may allow further data to be read from {@link #getReadingStream()}, however
     *      there is no guarantee of this.
     */
    void close() throws IOException;

}