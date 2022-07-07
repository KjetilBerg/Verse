package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.animation.SpriteAnimation;
import com.kbindiedev.verse.sfx.Source;
import com.kbindiedev.verse.ui.font.GlyphSequence;
import com.kbindiedev.verse.util.Properties;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class ExampleComponent implements IComponent {

    public Source slashSoundSource;
    public Source genericSoundSource;

    public Vector2f textPos = new Vector2f();
    public GlyphSequence text = new GlyphSequence();
    public int textSize = 12;

}
