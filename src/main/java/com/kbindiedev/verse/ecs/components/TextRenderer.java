package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.Main;
import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.system.ISerializable;
import com.kbindiedev.verse.ui.font.BitmapFont;
import com.kbindiedev.verse.ui.font.FlowMode;
import com.kbindiedev.verse.ui.font.GlyphSequence;
import com.kbindiedev.verse.util.StreamUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public class TextRenderer implements IComponent, ISerializable {

    public GlyphSequence sequence       = new GlyphSequence();
    public FlowMode flowmode            = FlowMode.BOTTOM_LEFT;
    public boolean screenMode           = true; // false = draw in world space, true = screen space
    public int fontSize                 = 12;
    public BitmapFont font              = null;


    @Override
    public void serialize(OutputStream stream) throws IOException {
        // temp impl
        int mask = 0; FlowMode[] values = FlowMode.values();
        for (int i = 0; i < values.length; ++i) {
            if (flowmode == values[i]) { mask = i; break; } // 0 <= mask <= 8 (4 bits)
        }
        if (screenMode) mask |= 0b00010000; // 5th bit

        StreamUtil.writeString(sequence.getStringValue(), stream);
        stream.write((byte)fontSize);
        stream.write((byte)mask);
    }

    @Override
    public void deserialize(InputStream stream) throws IOException {
        String text = StreamUtil.readString(stream);
        sequence = new GlyphSequence(text);

        fontSize = stream.read();
        int mask = stream.read();
        screenMode = (mask & 0b00010000) > 0;

        mask &= 0b00001111;
        flowmode = FlowMode.values()[mask];

        font = Main.TEMP_GLOBAL_FONT;
    }
}
