package com.kbindiedev.verse.gfx;

/**
 * A sprite defines a portion of a Texture (texture with uv).
 *
 * @see Texture
 */
public class Sprite {

    private Texture texture;
    private float u1, u2, v1, v2;

    public Sprite(Texture texture) { this(texture, 0f, 1f, 0f, 1f); }
    public Sprite(Texture texture, float u1, float v1, float u2, float v2) {
        this.texture = texture;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

    public Texture getTexture() { return texture; }

    public float getU1() { return u1; }
    public float getU2() { return u2; }
    public float getV1() { return v1; }
    public float getV2() { return v2; }

    //public int getWidth() { return Math.round((u2 - u1) * texture.getWidth()); }
    //public int getHeight() { return Math.round((v2 - v1) * texture.getHeight()); }
    public int getWidth() { return (int)((u2 - u1) * texture.getWidth() + 0.5f); }
    public int getHeight() { return (int)((v2 - v1) * texture.getHeight() + 0.5f); }

}