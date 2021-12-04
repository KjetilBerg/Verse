package com.kbindiedev.verse.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtil {

    //see research notes https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java?page=1&tab=votes#tab-top

    /**
     * Read an InputStream, until the end, and convert the result to a string by using the provided charset.
     *      This is done via a ByteArrayOutputStream.
     * The stream will be read until the end, but the stream will <em>not</em> be closed.
     * @param stream - The stream to read.
     * @param charset - The charset to convert the read data with.
     * @param buffer - Whether or not I should create a BufferedInputStream around the received stream.
     *               This may boost performance if enabled, though there is no benefit to do so multiple times
     *               (for example if already a BufferedInputStream).
     * @return the string gotten from converting all bytes of the stream according to the charset.
     * @throws IOException - if an I/O error occurs with the stream reading process.
     * @throws java.io.UnsupportedEncodingException (sub to IOException) - if the provided charset is not supported.
     */
    public static String streamToString(InputStream stream, String charset, boolean buffer) throws IOException {
        if (buffer) stream = new BufferedInputStream(stream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int d;
        while ((d = stream.read()) != -1) buf.write((byte)d);
        return buf.toString(charset);
    }

}
