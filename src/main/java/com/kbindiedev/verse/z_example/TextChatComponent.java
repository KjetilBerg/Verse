package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ui.font.GlyphSequence;

public class TextChatComponent implements IComponent {

    public Transform target = null;
    public GlyphSequence currentText = new GlyphSequence();
    public boolean active = false;

}