package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.profiling.Assertions;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

/** An object that buffers index data to a ByteBuffer. */
public class IndexDataBuffer {

    private static final int INDEX_SIZE = Short.BYTES;

    private ByteBuffer buffer;
    private int baseVertex;
    private boolean writeMode;   // true = I may be written to, false = I may be read from.

    private int oldWritePos;

    public IndexDataBuffer(int numIndices, boolean direct) {
        this(direct ? BufferUtils.createByteBuffer(numIndices * 2) : ByteBuffer.allocate(numIndices * 2));
    }

    public IndexDataBuffer(ByteBuffer buffer) {
        this.buffer = buffer;

        writeMode = true;
    }

    public int positionInBytes() { return buffer.position(); }
    public int limitInBytes() { return buffer.limit(); }
    public int capacityInBytes() { return buffer.capacity(); }

    public int getPosition() { return positionInBytes() / INDEX_SIZE; }
    public int getLimit() { return limitInBytes() / INDEX_SIZE; }
    public int getCapacity() { return capacityInBytes() / INDEX_SIZE; }

    /** Set the position of this IndexDataBuffer to the first index. */
    public void beginning() { position(0); }
    /** Set the position of this IndexDataBuffer, by index number. */
    public void position(int newPosition) { buffer.position(newPosition * INDEX_SIZE); }
    /** Set the limit of this IndexDataBuffer equal to its capacity. */
    public void unlimit() { limit(buffer.capacity() / INDEX_SIZE); }
    /** Set the limit of this IndexDataBuffer, by index number. */
    public void limit(int newLimit) { buffer.limit(newLimit * INDEX_SIZE); }

    /** Reset this IndexDataBuffer to have 0 stored indices. This will unframe the buffer if it is framed. maxIndices remain unchanged. */
    public void reset() {
        if (!writeMode) Assertions.warn("not in write mode");
        beginning(); unlimit();
    }

    /** Opposite of {@link #isInWriteMode()}. */
    public boolean isInReadMode() { return !writeMode; }
    /** @return if this buffer is in writing mode (true = this buffer may be written to, false = this buffer may be read from). */
    public boolean isInWriteMode() { return writeMode; }

    /** Switch to read mode and flip this buffer. if already in read mode, then nothing happens. */
    public void setToReadMode() {
        if (!writeMode) return;
        writeMode = false;
        oldWritePos = getPosition();
        buffer.flip();
    }

    /** Switch to write mode. if already in write mode, then nothing happens. set position to beginning and limit to end. */
    public void setToWriteMode() { setToWriteMode(true); }
    /** Switch to write mode. if already in write mode, then nothing happens. set limit to end.
     * if reset == true, then set position to 0, otherwise set position to its value before transitioning to read mode. */
    public void setToWriteMode(boolean reset) {
        if (writeMode) return;
        writeMode = true;
        if (reset) beginning(); else position(oldWritePos);
        unlimit();
    }

    /** Set the base vertex, to be added to every index that go through {@link #addLocalIndexEntry(int)}. */
    public void setBaseVertex(int baseVertex) {
        this.baseVertex = baseVertex;
    }

    /**
     * Add an index entry to the underlying buffer, applying the currently set baseVertex in the process.
     * This buffer must be in write mode ({@link #isInWriteMode()}) for this action to be successful.
     */
    public void addLocalIndexEntry(int index) {
        if (!writeMode) { Assertions.warn("cannot write to buffer in read mode"); return; }
        buffer.putShort((short)(index + baseVertex));
    }

    /**
     * @return the underlying ByteBuffer for the sake of reading.
     *              this buffer must be in read mode ({@link #isInReadMode()} for this action to be successful.
     */
    public ByteBuffer getBufferForReading() {
        if (writeMode) Assertions.warn("should read buffer in write mode. returning regardless...");
        return buffer;
    }

}