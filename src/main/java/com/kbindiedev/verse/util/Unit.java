package com.kbindiedev.verse.util;

/** Describe units, example metric SI units and IEC units */
public class Unit {

    public static final int SIZE_KIB = 1024;
    public static final int SIZE_MIB = 1024 * 1024;

    public static long fromKiB(long kib) { return kib * SIZE_KIB; }
    public static float toKiB(long bytes) { return (float)bytes / SIZE_KIB; }

    public static long fromMiB(long mib) { return mib * SIZE_MIB; }
    public static float toMiB(long bytes) { return (float)bytes / SIZE_MIB; }


}
