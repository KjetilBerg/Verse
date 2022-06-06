package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.Sprite;

public class SpriteRenderer implements IComponent {

    public Sprite sprite    = null; // TODO: DefaultSprite (or in system)
    public Pixel color      = new Pixel(1f, 1f, 1f);

}