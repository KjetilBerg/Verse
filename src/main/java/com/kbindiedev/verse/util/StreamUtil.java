package com.kbindiedev.verse.util;

import com.kbindiedev.verse.async.ThreadPool;
import com.kbindiedev.verse.profiling.Assertions;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Transmit "numBytes" from "from" to "to", by copying at most "chunkSize" bytes at a time.
     * If the end of the "from" stream is reached, then this method will stop passing bytes (and thereby a smaller amount of bytes may be passed).
     * @return the number of passed bytes.
     */
    public static int pass(InputStream from, OutputStream to, int numBytes, int chunkSize) throws IOException {
        byte[] chunk = new byte[chunkSize];

        int count;
        int totalCount = 0;

        while (numBytes > 0) {

            int maxRead = Math.min(numBytes, chunkSize);
            count = from.read(chunk, 0, maxRead);
            if (count == -1) break;

            to.write(chunk, 0, count);
            numBytes -= count;
            totalCount += count;

        }

        return totalCount;
    }

    /** Apply a new {@link Runnable} to Verse's Multithreading system so that the from-stream's .read() method is continually passed onto the to-stream's .write() method. */
    public static void passForever(InputStream from, OutputStream to, boolean printException) {
        Runnable runnable = () -> {
            try {
                while (true) to.write(from.read());
            } catch (IOException e) {
                if (printException) e.printStackTrace();
            }
        };
        ThreadPool.INSTANCE.submitOnce(runnable);
        // TODO: better integration with ThreadPool (also needs way of stopping)
    }

    // TODO: more general writing methods

    /** Write a string to the given OutputStream, that is null-terminated. */
    public static void writeString(String value, OutputStream to) throws IOException { writeString(value, to, StandardCharsets.UTF_8); }
    /** Write a string to the given OutputStream, that is null-terminated. */
    public static void writeString(String value, OutputStream to, Charset charset) throws IOException {
        to.write(value.getBytes(charset));
        to.write(0);
    }

    public static String readString(InputStream from) throws IOException { return readString(from, StandardCharsets.UTF_8); }
    public static String readString(InputStream from, Charset charset) throws IOException {
        List<Integer> ints = new ArrayList<>();
        int d;
        while ((d = from.read()) != 0) {
            if (d == -1) { Assertions.warn("unexpected end of stream, did not reach null-terminator value (0)"); break; }
            ints.add(d);
        }
        byte[] data = new byte[ints.size()];
        for (int i = 0; i < data.length; ++i) data[i] = (byte)(int)ints.get(i);
        return new String(data, charset);
    }

    public static void writeInt(int value, OutputStream to) throws IOException {
        to.write((byte)(value >> 24)); to.write((byte)(value >> 16)); to.write((byte)(value >> 8)); to.write((byte)(value));
    }

    public static int readInt(InputStream from) throws IOException {
        int b1 = from.read(), b2 = from.read(), b3 = from.read(), b4 = from.read();
        if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1) throw new IOException("readInt received -1 (end of stream)");
        return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
    }

    public static void writeFloat(float value, OutputStream to) throws IOException {
        writeInt(Float.floatToIntBits(value), to);
    }

    public static float readFloat(InputStream from) throws IOException {
        return Float.intBitsToFloat(readInt(from));
    }

    public static void writeVector3f(Vector3f value, OutputStream to) throws IOException {
        writeFloat(value.x, to); writeFloat(value.y, to); writeFloat(value.z, to);
    }

    public static Vector3f readVector3f(InputStream from) throws IOException {
        return new Vector3f(readFloat(from), readFloat(from), readFloat(from));
    }

    public static void writeQuaternionf(Quaternionf value, OutputStream to) throws IOException {
        writeFloat(value.x, to); writeFloat(value.y, to); writeFloat(value.z, to); writeFloat(value.w, to);
    }

    public static Quaternionf readQuaternionf(InputStream from) throws IOException {
        return new Quaternionf(readFloat(from), readFloat(from), readFloat(from), readFloat(from));
    }

}