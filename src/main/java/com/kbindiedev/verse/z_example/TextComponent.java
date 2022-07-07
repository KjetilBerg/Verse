package com.kbindiedev.verse.z_example;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.ui.font.BitmapFont;
import com.kbindiedev.verse.ui.font.FlowMode;
import com.kbindiedev.verse.ui.font.GlyphSequence;

public class TextComponent implements IComponent {

    public GlyphSequence sequence       = new GlyphSequence();
    public FlowMode flowmode            = FlowMode.BOTTOM_LEFT;
    public int fontSize                 = 12;
    public BitmapFont font              = null;

    public boolean screenMode           = true; // false = draw in world space, true = screen space

}
