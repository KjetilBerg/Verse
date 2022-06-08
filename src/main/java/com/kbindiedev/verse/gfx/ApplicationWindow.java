package com.kbindiedev.verse.gfx;

import com.kbindiedev.verse.input.keyboard.IKeyEventListener;
import com.kbindiedev.verse.input.mouse.IMouseInputProcessor;

/** Represents an operating system "Application" window. */
public abstract class ApplicationWindow { //implements IKeyEventListener, IMouseInputProcessor { //TODO: not entirely sure of relationship between window implementation and input serving

    protected String windowTitle;
    protected int windowX, windowY;
    protected int windowWidth, windowHeight;

    protected boolean isResizable = true;
    protected boolean isVisible = true;
    protected boolean isDecorated = true;
    protected boolean isFocused = true;
    protected boolean isFloating = false;
    protected boolean isTransparent = false;
    protected boolean isMaximized = false;
    protected boolean isMinimized = false;
    protected boolean isFocusOnShow = true;
    protected boolean isAutoMinimize = true;
    protected boolean isFullscreen = false;

    public ApplicationWindow(String windowTitle) {
        this.windowTitle = windowTitle;
        windowX = 0; windowY = 0;
        windowWidth = 0; windowHeight = 0;
    }

    public String getWindowTitle() { return windowTitle; }
    public int getWindowX() { return windowX; }
    public int getWindowY() { return windowY; }
    public int getWindowWidth() { return windowWidth; }
    public int getWindowHeight() { return windowHeight; }

    public boolean isResizable() { return isResizable; }
    public boolean isVisible() { return isVisible; }
    public boolean isDecorated() { return isDecorated; } // has borders etc
    public boolean isFocused() { return isFocused; }
    public boolean isFloating() { return isFloating; } // "always on top of other windows"
    public boolean isTransparent() { return isTransparent; }
    public boolean isMaximized() { return isMaximized; }
    public boolean isMinimized() { return isMinimized; }
    public boolean isFocusOnShow() { return isFocusOnShow; }
    public boolean isAutoMinimize() { return isAutoMinimize; }
    public boolean isFullscreen() { return isFullscreen; }

    // TODO: minimize, maximize etc

    /**
     * Change the window's title.
     * @param title - The new title.
     */
    public abstract void setWindowTitle(String title);

    /**
     * Set the window's location on the user's screen by using native methods.
     * @param windowX - The x coordinate in pixels.
     * @param windowY - The y coordinate in pixels.
     */
    public abstract void setPosition(int windowX, int windowY);

    /**
     * Set the window's size on the user's screen by using native methods.
     * @param width - The width in pixels.
     * @param height - The height in pixels.
     */
    public abstract void setSize(int width, int height);

    /** Close the window. */
    public abstract void close();

}