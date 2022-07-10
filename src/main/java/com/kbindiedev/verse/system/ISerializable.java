package com.kbindiedev.verse.system;

import java.io.InputStream;
import java.io.OutputStream;

/** Mark something as serializable. */
public interface ISerializable {

    void serialize(OutputStream stream);

    void deserialize(InputStream stream);

}