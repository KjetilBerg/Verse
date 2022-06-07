package com.kbindiedev.verse.gfx.impl.opengl_33;

// TODO: consider moving this to some "opengl_common" package.

import com.kbindiedev.verse.profiling.Assertions;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Define settings for creating a GLApplicationWindow.
 * @see GLApplicationWindow
 */
public class GLApplicationWindowSettings {

    private String windowTitle;
    private int windowWidth;
    private int windowHeight;
    private GLVersionSettings glVersion;
    private GLFullscreenSettings fullscreenSettings;

    private boolean resizable = true;
    private boolean visible = true;
    private boolean decorated = true;
    private boolean focused = true;
    private boolean floating = false;
    private boolean transparent = false;
    private boolean maximized = false;
    private boolean minimized = false;
    private boolean focusOnShow = true;
    private boolean autoMinimize = true;

    public GLApplicationWindowSettings(String windowTitle, int windowWidth, int windowHeight, GLVersionSettings glVersion) {
        this.windowTitle = windowTitle;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.glVersion = glVersion;
        fullscreenSettings = new GLFullscreenSettings(NULL);
    }

    public String getWindowTitle() { return windowTitle; }
    public int getWindowWidth() { return windowWidth; }
    public int getWindowHeight() { return windowHeight; }
    public GLVersionSettings getGLVersion() { return glVersion; }
    public boolean isResizable() { return resizable; }
    public boolean isVisible() { return visible; }
    public boolean isDecorated() { return decorated; }
    public boolean isFocused() { return focused; }
    public boolean isFloating() { return floating; }
    public boolean isTransparent() { return transparent; }
    public boolean isMaximized() { return maximized; }
    public boolean isMinimized() { return minimized; }
    public boolean isFocusOnShow() { return focusOnShow; }
    public boolean isAutoMinimize() { return autoMinimize; }
    public GLFullscreenSettings getFullscreenSettings() { return fullscreenSettings; }
    // TODO: forceAspectRatio

    public void setResizable(boolean resizable) { this.resizable = resizable; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setDecorated(boolean decorated) { this.decorated = decorated; }
    public void setFocused(boolean focused) { this.focused = focused; }
    public void setFloating(boolean floating) { this.floating = floating; }
    public void setTransparent(boolean transparent) { this.transparent = transparent; }
    public void setMaximized(boolean maximized) { this.maximized = maximized; }
    public void setMinimized(boolean minimized) { this.minimized = minimized; }
    public void setFocusOnShow(boolean focusOnShow) { this.focusOnShow = focusOnShow; }
    public void setAutoMinimize(boolean autoMinimize) { this.autoMinimize = autoMinimize; }
    public void setFullscreenSettings(GLFullscreenSettings settings) {
        if (settings == null) {
            Assertions.warn("GLFullscreenSettings is null, assuming default object (not fullscreen)");
            settings = new GLFullscreenSettings();
        }
        fullscreenSettings = settings;
    }

    public static class GLVersionSettings {

        private int glMajor;
        private int glMinor;
        private int glProfile;
        private int glForwardCompat;

        public GLVersionSettings(int glMajor, int glMinor) { this(glMajor, glMinor, GLFW_OPENGL_CORE_PROFILE, GLFW_TRUE); }
        public GLVersionSettings(int glMajor, int glMinor, int glProfile, int glForwardCompat) {
            this.glMajor = glMajor;
            this.glMinor = glMinor;
            this.glProfile = glProfile;
            this.glForwardCompat = glForwardCompat;
        }

        public int getGLMajor() { return glMajor; }
        public int getGLMinor() { return glMinor; }
        public int getGLProfile() { return glProfile; }
        public int getGLForwardCompat() { return glForwardCompat; }
    }

    /** Warning: GLFW fullscreening is buggy; At least on my system. I blame Nvidia. */
    public static class GLFullscreenSettings {

        private long monitor;           // NULL = not fullscreen (glfw default)
        private GLFWVidMode vidMode;    // null = use default

        public GLFullscreenSettings() { this(NULL); }
        public GLFullscreenSettings(long monitor) { this(monitor, null); }
        public GLFullscreenSettings(long monitor, GLFWVidMode vidMode) {
            this.monitor = monitor;
            this.vidMode = vidMode;
        }

        public long getMonitor() { return monitor; }
        public GLFWVidMode getVidMode() { return vidMode; }

        public void setMonitorToPrimaryMonitor() { monitor = glfwGetPrimaryMonitor(); }
        public void setVidModeKeepCurrent() { vidMode = glfwGetVideoMode(monitor); }

    }

}