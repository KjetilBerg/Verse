package com.kbindiedev.verse.gfx;

public abstract class Texture {

    /**
     * Bind this texture to a slot.
     * If the texture is bound to a different slot, it should be unbound first.
     * If another texture is bound to the provided slot, that texture should be unbound first.
     * If the user needs a texture to be bound to multiple slots, make a copy of this texture.  //TODO: make some .duplicate or something?
     * Any uses of this texture should refer to it using MaterialTemplate texture id "slot".
     * @throws IndexOutOfBoundsException - If the slot exceeds the limit of texture slots provided by the implementation.
     * Potentially deprecated, as implementations may decide texture bindings themselves. TODO: needs looking into
     */
    @Deprecated
    public abstract void bind(int slot) throws IndexOutOfBoundsException;

    /**
     * Unbind this texture from a previously bound slot. If unbound, does nothing.
     * Potentially deprecated, as implementations may decide texture bindings themselves. TODO: needs looking into
     */
    @Deprecated
    public abstract void unbind();

    /** @return the width of the texture in pixels */
    public abstract int getWidth();

    /** @return the height of the texture in pixels */
    public abstract int getHeight();

}
