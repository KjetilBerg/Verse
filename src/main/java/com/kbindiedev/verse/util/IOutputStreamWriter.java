package com.kbindiedev.verse.util;

import java.io.IOException;
import java.io.OutputStream;

public interface IOutputStreamWriter {

    /** Write to the provided stream. The stream should not be closed by this implementation.
     *      The implementation may flush the stream as it sees fit, however is under no obligation to so do. */
    void write(OutputStream stream) throws IOException;

}
