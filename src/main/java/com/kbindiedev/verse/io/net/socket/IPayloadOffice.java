package com.kbindiedev.verse.io.net.socket;

import java.io.IOException;

/** An interface that packets of data (payloads) can be read from or written to. The packets are whole; not fragmented. */
public interface IPayloadOffice {

    /**
     * Retrieve a payload.
     * @param wait - If true, then this method should be blocking in order to receive a payload,
     *              otherwise if false and no payload is available, "null" is returned.
     * @return a payload, or null if "wait" is false and no payload is available. if "wait" is true, then
     *          the returned value must never be null.
     * @throws IOException - If a payload cannot be retrieved.
     */
    byte[] retrieve(boolean wait) throws IOException;

    /**
     * Send a payload.
     * @throws IOException - If the payload cannot be sent.
     */
    void send(byte[] payload) throws IOException;

    void close() throws IOException;

}