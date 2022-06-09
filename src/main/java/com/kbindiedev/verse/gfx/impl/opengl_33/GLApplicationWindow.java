package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.gfx.ApplicationWindow;
import com.kbindiedev.verse.input.keyboard.KeyboardInputEventQueue;
import com.kbindiedev.verse.profiling.Assertions;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLApplicationWindow extends ApplicationWindow {

    private long windowGLID;
    private KeyboardInputEventQueue keyboardQueue; // TODO: more general class; want other queues too. make part of ApplicationWindow instead?

    private GLApplicationWindow(long windowGLID, String title) {
        super(title);
        this.windowGLID = windowGLID;
        queryGlfwAboutDetails();

        keyboardQueue = new KeyboardInputEventQueue();
        setupCallbacks();
    }

    // TODO TEMP:
    public KeyboardInputEventQueue getKeyboardQueue() { return keyboardQueue; }

    public static GLApplicationWindow createWindow(GLApplicationWindowSettings settings) {

        setWindowHints(settings);
        long monitor = settings.getFullscreenSettings().getMonitor();

        long windowGLID = glfwCreateWindow(settings.getWindowWidth(), settings.getWindowHeight(), settings.getWindowTitle(), monitor, NULL);

        return new GLApplicationWindow(windowGLID, settings.getWindowTitle());

    }

    private static void setWindowHints(GLApplicationWindowSettings settings) {
        // TODO FUTURE: glfw tracker like gl tracker
        glfwDefaultWindowHints();
        GLApplicationWindowSettings.GLVersionSettings glVersion = settings.getGLVersion();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, glVersion.getGLMajor());
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, glVersion.getGLMinor());
        glfwWindowHint(GLFW_OPENGL_PROFILE, glVersion.getGLProfile());
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, glVersion.getGLForwardCompat());

        if (!settings.isResizable()) glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // default = true
        if (!settings.isVisible()) glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // default = true
        if (!settings.isDecorated()) glfwWindowHint(GLFW_DECORATED, GLFW_FALSE); // default = true
        if (!settings.isFocused()) glfwWindowHint(GLFW_FOCUSED, GLFW_FALSE); // default = true
        if (settings.isFloating()) glfwWindowHint(GLFW_FLOATING, GLFW_TRUE); // default = false
        if (settings.isTransparent()) glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE); // default = false
        if (settings.isMaximized()) glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); // default = false
        if (settings.isMinimized()) { throw new NotImplementedException(); } // TODO FUTURE (or remove?)
        if (!settings.isFocusOnShow()) glfwWindowHint(GLFW_FOCUS_ON_SHOW, GLFW_FALSE); // default = true
        if (!settings.isAutoMinimize()) glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_FALSE); // default = true

        GLFWVidMode vidMode = settings.getFullscreenSettings().getVidMode();
        if (vidMode != null) {
            glfwWindowHint(GLFW_RED_BITS, vidMode.redBits());
            glfwWindowHint(GLFW_GREEN_BITS, vidMode.greenBits());
            glfwWindowHint(GLFW_BLUE_BITS, vidMode.blueBits());
            glfwWindowHint(GLFW_REFRESH_RATE, vidMode.refreshRate());
        }
    }

    public long getWindowGLID() { return windowGLID; }

    // TODO: remove these two?
    /** Called by the creator of this object when the window is resized. */
    protected void eventResizeWindow(int width, int height) {
        windowWidth = width;
        windowHeight = height;
    }
    /** Called by the creator of this object when the window is moved. */
    protected void eventMoveWindow(int windowX, int windowY) {
        this.windowX = windowX;
        this.windowY = windowY;
    }

    private void setupCallbacks() {
        glfwSetKeyCallback(windowGLID, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true); // TODO: this
            if (action == GLFW_PRESS) {
                keyboardQueue.queueKeyDown(key);
            } else if (action == GLFW_RELEASE) {
                keyboardQueue.queueKeyUp(key);
            } else if (action == GLFW_REPEAT) {
                keyboardQueue.queueKeyTyped(key);
            } else {
                Assertions.warn("GLFW: unknown keyboard action: %d", action);
            }
        });
    }

    @Override
    public void setWindowTitle(String title) {
        glfwSetWindowTitle(windowGLID, title);
    }

    @Override
    public void setPosition(int windowX, int windowY) {
        throw new NotImplementedException(); //TODO
    }

    @Override
    public void setSize(int width, int height) {
        throw new NotImplementedException(); //TODO
    }

    @Override
    public void close() {
        glfwDestroyWindow(windowGLID);
    }

    // TODO: glfwWindowShouldClose, pollEvents, swapBuffers, input stuff

    // TODO: window content scale (DPI)
    // TODO: window get opacity ?

    /** Ask glfw about the actual state of the window. */
    private void queryGlfwAboutDetails() {
        try (MemoryStack stack = stackPush()) {
            IntBuffer winX = stack.mallocInt(1);
            IntBuffer winY = stack.mallocInt(1);
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);

            glfwGetWindowPos(windowGLID, winX, winY);
            glfwGetWindowSize(windowGLID, width, height);

            windowX = winX.get(0);
            windowY = winY.get(0);
            windowWidth = width.get(0);
            windowHeight = height.get(0);

        }
        isMinimized = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_ICONIFIED));
        isMaximized = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_MAXIMIZED));
        isVisible = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_VISIBLE));
        isFocused = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_FOCUSED));
        isTransparent = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_TRANSPARENT_FRAMEBUFFER));
        isFullscreen = (NULL != glfwGetWindowMonitor(windowGLID));

        isDecorated = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_DECORATED));
        isResizable = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_RESIZABLE));
        isFloating = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_FLOATING));
        isFocusOnShow = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_FOCUS_ON_SHOW));
        isAutoMinimize = (GLFW_TRUE == glfwGetWindowAttrib(windowGLID, GLFW_AUTO_ICONIFY));
    }
}
