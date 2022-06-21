package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.profiling.Assertions;

import java.util.Random;

public class Pixel {

    public static final Pixel SOLID_WHITE = new Pixel(255, 255, 255, 255);

    private static final Random STATIC_RANDOM = new Random();

    public static Pixel random() { return random(STATIC_RANDOM); }
    public static Pixel random(Random random) { return new Pixel(random.nextInt(256), random.nextInt(256), random.nextInt(256)); }

    public static Pixel randomWithAlpha() { return randomWithAlpha(STATIC_RANDOM); }
    public static Pixel randomWithAlpha(Random random) { return new Pixel(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256)); }

    private int value;

    /** Create a pixel with floats defined between 0.0f - 1.0f, where 1.0f is 100% light. Alpha is assumed to be 1.0f (opaque) */
    public Pixel(float r, float g, float b) { this(r, g, b, 1.0f); }

    /** Create a pixel with floats defined between 0.0f - 1.0f, where 1.0f is 100% light, or opaque for alpha (alpha = 0.0f means transparent). */
    public Pixel(float r, float g, float b, float a) { this((int)(r*255), (int)(g*255), (int)(b*255), (int)(a*255)); }

    /** Create a pixel with ints defined between 0 - 255, where 255 is 100% light. Alpha is assumed to be 255 (opaque) */
    public Pixel(int r, int g, int b) { this(r, g, b, 255); }

    /** Create a pixel with ints defined between 0 - 255, where 255 is 100% light, or opaque for alpha (alpha = 0 means transparent) */
    public Pixel(int r, int g, int b, int a) {
        testValueWithinRange(r, g, b, a);
        value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
    }

    private void testValueWithinRange(int r, int g, int b, int a) {
        if ((r > 255 || r < 0) || (g > 255 || g < 0) || (b > 255 || b < 0) || (a > 255 || a < 0)) {
            Assertions.error("pixel value outside allowed range");
        }
    }

    /** Get the alpha value */
    public int a() { return (value >> 24) & 0xFF; }

    /** Get the red value */
    public int r() { return (value >> 16) & 0xFF; }

    /** Get the green value */
    public int g() { return (value >> 8) & 0xFF; }

    /** Get the blue value */
    public int b() { return (value >> 0) & 0xFF; }

    /** Get the packed value */
    public int packed() { return value; }

}
