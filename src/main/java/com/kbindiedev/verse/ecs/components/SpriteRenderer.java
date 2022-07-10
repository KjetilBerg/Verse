package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.system.ISerializable;
import org.joml.Vector2f;

import java.io.InputStream;
import java.io.OutputStream;

public class SpriteRenderer implements IComponent, ISerializable {

    public Sprite sprite    = null; // TODO: DefaultSprite (or in system)
    public Pixel color      = new Pixel(1f, 1f, 1f);
    public boolean flipX    = false;
    public boolean flipY    = false;

    public Vector2f offset  = new Vector2f();


    // TODO: empty for now
    @Override
    public void serialize(OutputStream stream) {

    }

    @Override
    public void deserialize(InputStream stream) {

    }

}