package com.kbindiedev.verse.system;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/** Mark something as serializable. */
public interface ISerializable {

    void serialize(OutputStream stream) throws IOException;

    void deserialize(InputStream stream) throws IOException;

}