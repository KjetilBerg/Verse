package com.kbindiedev.verse.util;

import com.kbindiedev.verse.util.InsertionStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A long worth of data, but compressed to fit into a smaller byte size.
 *
 * All CompressedLongs are unsigned, and have a max value of 2^62 - 1.
 * CompressedLongs are encoded into a minimum of 1 byte, up to a maximum of 8 bytes.
 *
 * The first two bits of the encoded value represents the "number of bytes".
 * 00 - 1 byte total, 01 - 2 bytes total, 10 - 4 bytes total, 11 - 8 bytes total.
 * The remaining bits in the set of bytes are used to encode the number (leftmost bit is MSB, rightmost bit is LSB).
 *
 * If it is possible to encode the long in <=6 bits, then byte size 1 must be used, otherwise
 * if it is possible to encode the long in <=14 bits, then byte size 2 must be used, otherwise
 * if it is possible to encode the long in <=30 bits, then byte size 4 must be used, otherwise
 * if it is possible toe ncode the long in <=62 bits, then byte size 8 must be used, otherwise
 * an exception is to be thrown.
 */
public class CompressedLong {

    private static final long MAX = 0x3FFFFFFFFFFFFFFFL; // 62 bits = 1

    public static void serialize(long value, OutputStream stream) throws IOException {
        if (value < 0 || value > MAX)
            throw new IllegalArgumentException("value must be an unsigned long of max 62 bits ("+MAX+"), but got: " + value);

        int size = 1;
        if (value > 0x0000003FL) size = 2; // > 6 bits
        if (value > 0x00003FFFL) size = 3; // > 14 bits
        if (value > 0x3FFFFFFFL) size = 4; // > 30 bits

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putLong(value);

        //if (size == 1) buffer.put(7, (byte)(buffer.get(7) | 0b00000000));
        if (size == 2) buffer.put(6, (byte)(buffer.get(6) | 0b01000000));
        if (size == 3) buffer.put(4, (byte)(buffer.get(4) | 0b10000000));
        if (size == 4) buffer.put(0, (byte)(buffer.get(0) | 0b11000000));

        if (size >= 4) {
            stream.write(buffer.get(0));
            stream.write(buffer.get(1));
            stream.write(buffer.get(2));
            stream.write(buffer.get(3));
        }
        if (size >= 3) {
            stream.write(buffer.get(4));
            stream.write(buffer.get(5));
        }
        if (size >= 2) {
            stream.write(buffer.get(6));
        }
        stream.write(buffer.get(7));

    }

    public static long deserialize(InputStream stream) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.BIG_ENDIAN);

        int firstByte = stream.read();
        int size = ((firstByte >> 6) & 0b11) + 1;
        firstByte &= 0b00111111;

        stream = new InsertionStream((byte)firstByte, stream);

        if (size >= 4) {
            buffer.put(0, (byte)stream.read());
            buffer.put(1, (byte)stream.read());
            buffer.put(2, (byte)stream.read());
            buffer.put(3, (byte)stream.read());
        }
        if (size >= 3) {
            buffer.put(4, (byte)stream.read());
            buffer.put(5, (byte)stream.read());
        }
        if (size >= 2) {
            buffer.put(6, (byte)stream.read());
        }
        buffer.put(7, (byte)stream.read());

        return buffer.getLong(0);
    }

}