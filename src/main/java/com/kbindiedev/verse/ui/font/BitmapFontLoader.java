package com.kbindiedev.verse.ui.font;

import com.kbindiedev.verse.util.FormatLoader;

/** Loads BitmapFonts from some stream of data. */
public class BitmapFontLoader extends FormatLoader<BitmapFont> {

    private static final BitmapFontLoader instance = new BitmapFontLoader();

    public BitmapFontLoader() {
        putLoader("bmf", new BMFontLoader());
        putLoader("fnt", getLoader("bmf"));
        putLoader("bmfont", getLoader("bmf"));
    }

    public static BitmapFontLoader getInstance() { return instance; }

}