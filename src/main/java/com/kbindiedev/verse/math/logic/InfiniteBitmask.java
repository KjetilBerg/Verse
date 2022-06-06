package com.kbindiedev.verse.math.logic;

import com.kbindiedev.verse.util.Nullable;

import java.util.HashMap;
import java.util.Set;

/**
 * Describes a bitmask of an arbitrary length (from 0 bits to infinite number of bits).
 *
 * Every InfiniteBitmask is divided into "parts" that can be addressed by indices.
 *      Every part is 64 bits long.
 *      As an example, index 2 would represents bits 65 - 128 of the mask.
 *          Internally, this logic is all shifted by 1 (0 - 63, 64 - 127), but the use case is as described.
 *
 * Includes several useful bitwise operations that are extremely fast.
 */
public class InfiniteBitmask {

    public static final InfiniteBitmask EMPTY = new InfiniteBitmask(0);

    private HashMap<Long, Long> parts;          // maps "part-indices" to their part values.
    private @Nullable InfiniteBitmask next;     // next in my sequence.

    public InfiniteBitmask() { this(0); }

    /**
     * Create an "InfiniteBitmask" with a starting value.
     * @param value - The starting value, as bitmask.
     */
    public InfiniteBitmask(long value) {
        parts = new HashMap<>();
        next = null;
        setForIndex(0, value);
    }

    /**
     * Set a value given a certain index into this InfiniteBitmask.
     *      If you want to set an index greater than Long.MAX_VALUE, then you have to use
     *          .nextStore to retrieve the next section of this InfiniteBitmask.
     * @param index - The index into this InfiniteBitmask. Every index stores 64 bits. Must be positive.
     * @param value - The value to set for the given index. Value is 64 bits.
     */
    public void setForIndex(long index, long value) {
        if (value == 0) { parts.remove(index); return; } // empty is 0.
        parts.put(index, value);
    }

    /**
     * Get the value for a certain index into this InfiniteBitmask.
     * Every index in an InfiniteBitmask represent 64 bits.
     * Note that the returned value is on the form of the exact bit representation that is stored.
     *      The value returned will be negative if the MSB is set.
     * If you want to set an index greater than Long.MAX_VALUE, then you have to use
     *      .nextStore to retrieve the next section of this InfiniteBitmask.
     * @param index - The index into this InfiniteBitmask to get the value from.
     * @return the value stored at the given index.
     */
    public long getForIndex(long index) {
        return parts.getOrDefault(index, 0L); // empty is 0.
    }

    /**
     * Set the value for a single bit.
     * Note that this method is limited by the capacity of a single long.
     *      If you want to set the bit of a later index, use one of the other .setBit methods.
     * @param bitIndex - The index of the bit to set.
     * @param value - The value to set for the given bitIndex (true=1, false=0).
     */
    public void setBit(long bitIndex, boolean value) {
        long partIndex = (bitIndex / 64);
        byte bitInPartIndex = (byte)(bitIndex % 64);
        setBit(partIndex, bitInPartIndex, value);
    }

    /**
     * Set the value for a single bit.
     * This method involves manual work of looking into "part-indices".
     * @param partIndex - The index of the part to look into (every part is 64 bits).
     * @param bitInPartIndex - The index of the bit in the given "part" (must be 0 - 63).
     * @param value - The value to set for the bit to be indexed (true=1, false=0).
     */
    public void setBit(long partIndex, byte bitInPartIndex, boolean value) {
        long currentValue = getForIndex(partIndex);

        // https://stackoverflow.com/questions/4674006/set-specific-bit-in-byte
        if (value) {
            currentValue |= (1 << bitInPartIndex);
        } else {
            currentValue &= ~(1 << bitInPartIndex);
        }

        setForIndex(partIndex, currentValue);
    }

    /**
     * Test the value for a single bit.
     * Note that this method is limited by the capacity of a single long.
     *      If you want to set the bit of a later index, use one of the other .setBit methods.
     * @param bitIndex - The index of the bit to test.
     * @return whether or not the bit at the given bitIndex is set (true=1, false=0).
     */
    public boolean testBit(long bitIndex) {
        long partIndex = (bitIndex / 64);
        byte bitInPartIndex = (byte)(bitIndex % 64);
        return testBit(partIndex, bitInPartIndex);
    }

    /**
     * Test the value for a single bit.
     * This method involves manual work of looking into "part-indices".
     * @param partIndex - The index of the part to look into (every part is 64 bits).
     * @param bitInPartIndex - The index of the bit in the given "part" (must be 0 - 63).
     * @return whether or not the bit at the given bitIndex is set (true=1, false=0).
     */
    public boolean testBit(long partIndex, byte bitInPartIndex) {
        long currentValue = getForIndex(partIndex);

        return (currentValue & (1 << bitInPartIndex)) != 0;
    }

    /**
     * Check if the set of my bits is a superset of the provided InfiniteBitmask's set of bits.
     *      (equivalent to their set of bits being a subset of my set of bits).
     * Equivalent to them being a subset of me.
     * If I have a bit at some index that is 0, and the given InfiniteBitmask has the bit at the same index
     *      set to 1, then this method will return false. Otherwise, if none such cases occur,
     *      then this method will return true.
     * Note that the fact of the provided InfiniteBitmask having a bit set to 0 where I have a 1 is disregarded.
     * Mathematically equivalent to ((them & me) == them).
     *      "I have all bits that they do".
     * @param i - The other InfiniteBitmask to compare against.
     * @return whether or not I am a superset of the provided InfiniteBitmask (I have a 1 in every bit position that they do).
     */
    public boolean isBitSupersetOf(InfiniteBitmask i) {
        return i.isBitSubsetOf(this);
    }

    /**
     * Check if the set of my bits is a subset of the provided InfiniteBitmask's set of bits.
     *      (equivalent to their set of bits being a superset of my set of bits).
     * Equivalent to them being a superset of me.
     * If I have a bit at some index that is 1, and the given InfiniteBitmask does not have the bit at the
     *      same index set to 1, then this method will return false. Otherwise, if none such cases occur,
     *      then this method will return true.
     * Note that the fact of the provided InfiniteBitmask having a bit set to 1 where I have a 0 is disregarded.
     * Mathematically equivalent to ((them & me) == me).
     *      "they have all bits that I do".
     * @param i - The other InfiniteBitmask to compare against.
     * @return whether or not I am a subset of the provided InfiniteBitmask (has a 1 in every bit position that I do).
     */
    public boolean isBitSubsetOf(InfiniteBitmask i) {
        if (i == this) return true;

        Set<Long> myIndices = getIndices();
        Set<Long> theirIndices = i.getIndices();

        // I have indices that they do not = I have a bit that they do not.
        if (!theirIndices.containsAll(myIndices)) return false;

        // check that all indices match.
        for (Long index : myIndices) {
            long myValue = getForIndex(index);
            long theirValue = i.getForIndex(index);
            if ((theirValue & myValue) != myValue) return false;
        }

        // check next section
        if (nextStore() == null) return true;       // we have no more bits
        if (i.nextStore() == null) return false;    // we have more bits and they do not
        return nextStore().isBitSubsetOf(i.nextStore()); // compare next in chain.
    }

    /**
     * Check if the provided InfiniteBitmask and myself have any bits in common.
     * If I have a bit at some index that is 1, and the given InfiniteBitmask also has the bit at the same index
     *      set to 1, then this method will return true. Otherwise, if none such cases occur, then this method will return false.
     * Mathematically equivalent to ((them & me) != 0).
     *      "we have some bits in common".
     * @param i - The other InfiniteBitmask to compare against.
     * @return whether or not the provided InfiniteBitmask and I have at least 1 bit in common.
     */
    public boolean isBitOverlappingSet(InfiniteBitmask i) {
        if (i == this) return true;

        Set<Long> myIndices = getIndices();
        Set<Long> theirIndices = i.getIndices();

        // consider the set that require the least comparisons.
        Set<Long> indices = (myIndices.size() < theirIndices.size() ? myIndices : theirIndices);

        for (Long index : indices) {
            long myValue = getForIndex(index);
            long theirValue = i.getForIndex(index);
            if ((theirValue & myValue) != 0) return true;   // > 0 could cause problems with how sign works
        }

        // check next section
        if (nextStore() == null) return false;      // we have no more bits
        if (i.nextStore() == null) return false;    // they have no more bits
        return nextStore().isBitOverlappingSet(i.nextStore());    // compare next in chain.
    }

    /**
     * Check if the provided InfiniteBitmask and myself have no bits in common.
     * If I have a bit at some index that is 1, and the given InfiniteBitmask also has the bit at the same index
     *      set to 1, then this method will return false. Otherwise, if none such cases occur, then this method will return true.
     * Mathematically equivalent to ((them & me) == 0).
     *      "we have no bits in common".
     * @param i - The other InfiniteBitmask to compare against.
     * @return whether or not the provided InfiniteBitmask and I have no bits in common.
     */
    public boolean isBitDisjointSet(InfiniteBitmask i) {
        if (i == this) return false;

        Set<Long> myIndices = getIndices();
        Set<Long> theirIndices = i.getIndices();

        // consider the set that require the least comparisons.
        Set<Long> indices = (myIndices.size() < theirIndices.size() ? myIndices : theirIndices);

        for (Long index : indices) {
            long myValue = getForIndex(index);
            long theirValue = i.getForIndex(index);
            if ((theirValue & myValue) != 0) return false;
        }

        if (nextStore() == null) return true;   // we have no more bits
        if (i.nextStore() == null) return true; // they have no more bits
        return nextStore().isBitDisjointSet(i.nextStore()); // compare next in chain.
    }

    /**
     * Check if the provided InfiniteBitmask and myself have the exact same arrangement of bits.
     * If I have a bit at some index that is some value, and the given InfiniteBitmask does not have the bit at the
     *      same index set to the same value, then this method will return false. Otherwise, if none such cases occur,
     *      then this method will return true.
     * Mathematically equivalent to (them == me).
     *      "we are the same".
     * @param i - The other InfiniteBitmask to compare against.
     * @return whether or not the provided InfiniteBitmask and I are identical.
     */
    public boolean isBitIdenticalSet(InfiniteBitmask i) {
        if (i == this) return true;

        Set<Long> myIndices = getIndices();
        Set<Long> theirIndices = i.getIndices();

        if (myIndices.size() != theirIndices.size()) return false;  // not same parts-size.

        for (Long index : myIndices) {
            long myValue = getForIndex(index);
            long theirValue = i.getForIndex(index);
            if (myValue != theirValue) return false;
        }

        InfiniteBitmask myNext = nextStore();
        InfiniteBitmask theirNext = i.nextStore();

        if (myNext == null && theirNext == null) return true;   // neither of us have more bits to compare
        if (myNext == null) return false;                       // we have no more bits but they do
        if (theirNext == null) return false;                    // we have more bits but they do not
        return myNext.isBitIdenticalSet(theirNext);             // compare next in chain
    }


    /**
     * Get the next "store" for this InfiniteBitmask.
     * The returned value is an InfiniteBitmask that is unaware of its "parent".
     * These "stores" exist in a chain, thus allowing the set of bits to be infinite.
     * Every "store" may represent {@code 64*Long.MAX_VALUE} number of bits. Any further bits
     *      must be placed into "the next store".
     * Because it is extremely unlikely you will need to support more than {@code 64*Long.MAX_VALUE} bits,
     *      you would probably never use this.
     * May return null.
     * @return the next "store" for this InfiniteBitmask (may be null).
     */
    public @Nullable InfiniteBitmask nextStore() { return next; }

    /**
     * Set the next "store" for this InfiniteBitmask.
     * These "stores" exist in a chain, thus allowing the set of bits to be infinite.
     * Every "store" may represent {@code 64*Long.MAX_VALUE} number of bits. Any further bits
     *      must be placed into "the next store".
     * Because it is extremely unlikely you will need to support more than {@code 64*Long.MAX_VALUE} bits,
     *      you would probably never use this.
     * "Store" may be null.
     * The provided store must not contain this InfiniteBitmask anywhere in its "store"-chain.
     *      (may not contain this InfiniteBitmask in it such that is causes an infinite loop).
     * @param next - The next "store" for this InfiniteBitmask (may be null).
     */
    public void setNextStore(InfiniteBitmask next) {

        // actually set the store
        this.next = next;

        if (next == null) return;

        // sanity check
        int counter = 0;
        InfiniteBitmask head = next;
        do {
            if (head == this) throw new IllegalArgumentException("\"next store\" contains self in chain at index: " + counter + ". this would cause an infinite loop");
            head = head.nextStore();
            counter++;
        } while (head != null);

    }

    /**
     * Get indices of all parts that are currently being stored.
     * Non-existent indices are considered "empty" and therefore equal 0.
     * @return indices of all parts that are currently being stored.
     */
    private Set<Long> getIndices() { return parts.keySet(); }

    @Override
    public int hashCode() {
        int val = parts.hashCode();
        if (nextStore() != null) val += 31 * nextStore().hashCode();
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!o.getClass().equals(InfiniteBitmask.class)) return false;
        return isBitIdenticalSet((InfiniteBitmask)o); // or parts.equal(o.parts) && nextStore().equals(o.nextStore())
    }

}
