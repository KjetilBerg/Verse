package com.kbindiedev.verse.util;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompressedLongTest {

    private List<Long> longs;

    @BeforeEach
    public void setup() {
         longs = new ArrayList<>();

        longs.add(0L);
        longs.add(1L);
        longs.add(22L);
        longs.add(62L);
        longs.add(63L);
        longs.add(64L);
        longs.add(128L);
        longs.add(120784L);
        longs.add(0xFFFFFF00L);
    }

    @Test
    public void testValues() throws IOException {

        for (long l : longs) {
            ByteOutputStream output = new ByteOutputStream();
            CompressedLong.serialize(l, output);

            InputStream input = new ByteArrayInputStream(output.getBytes());
            long v = CompressedLong.deserialize(input);

            assertEquals(l, v, "serialize -> deserialize should produce same value, but got different");
        }

    }

    @Test
    public void testSizes() throws IOException {

        for (long l : longs) {
            ByteOutputStream output = new ByteOutputStream();
            CompressedLong.serialize(l, output);

            InputStream input = new ByteArrayInputStream(output.getBytes());
            int size = ((input.read() << 6) & 0b11) + 1;

            if (l <= 0x3FL) assertEquals(size, 1, "size should be 1 when <= 6 bits");
            if (l <= 0x3FFFL) assertEquals(size, 1, "size should be 2 when <= 14 bits");
            if (l <= 0x3FFFFFFFL) assertEquals(size, 1, "size should be 3 when <= 30 bits");
            if (l <= 0x3FFFFFFFFFFFFFFFL) assertEquals(size, 1, "size should be 4 when <= 62 bits");
        }

    }

}