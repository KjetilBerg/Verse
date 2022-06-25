package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.Sprite;
import org.joml.Vector2f;

public class SpriteRenderer implements IComponent {

    public Sprite sprite    = null; // TODO: DefaultSprite (or in system)
    public Pixel color      = new Pixel(1f, 1f, 1f);
    public boolean flipX    = false;
    public boolean flipY    = false;

    public Vector2f offset  = new Vector2f();

}