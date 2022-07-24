package com.kbindiedev.verse.util;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.system.memory.IRecyclable;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/** Accumulates bytes to create a {@link CompressedLong}. */
public class CompressedLongAccumulator implements IRecyclable {

    private byte[] bytes;
    private int index;

    public CompressedLongAccumulator() {
        bytes = new byte[8];
        index = 0;
    }

    public void addByte(byte b) {
        if (isFinished()) throw new IndexOutOfBoundsException("builder has finished");
        bytes[index++] = b;
    }

    /** @return true if I have received enough bytes to build a proper CompressedLong. */
    public boolean isFinished() {
        if (index == 0) return false;

        int size = ((bytes[0] >> 6) & 0b11) + 1;
        switch (size) {
            case 1: return index >= 1;
            case 2: return index >= 2;
            case 3: return index >= 4;
            case 4: return index >= 8;
            default: throw new IllegalStateException("unreachable statement: (((b >> 6) & 0b11) + 1) > 4");
        }
    }

    public long getValue() {
        if (!isFinished()) throw new IllegalStateException("builder is not finished");
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes)) {
            return CompressedLong.deserialize(stream);
        } catch (IOException e) {
            Assertions.error("ByteArrayInputStream threw IOException on read() or close() despite that being impossible"); // TODO Assertions.fatal or Assertions.impossible
            return 0;
        }
    }

    /** Reset this builder so that it can be used again. */
    @Override
    public void recycle() {
        index = 0;
    }

}