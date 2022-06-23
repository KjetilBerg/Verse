package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.AssetPool;
import com.kbindiedev.verse.gfx.*;
import com.kbindiedev.verse.gfx.impl.opengl_33.window.GLApplicationWindow;
import com.kbindiedev.verse.gfx.impl.opengl_33.window.GLApplicationWindowSettings;
import com.kbindiedev.verse.io.files.Files;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

/** Graphics Engine impl for OpenGl version 3.3 */
public class GEOpenGL33 extends GraphicsEngine {

    public static GL33 gl33;    //TODO: move, but keep internal to this package
    private static long window;
    private static final float FPS = 1/60f;
    private int DISPLAY_WIDTH = 1000, DISPLAY_HEIGHT = 800;

    @Override
    public void initialize(GraphicsEngineSettings settings) {   //TODO: use settings
        System.out.println("Initializing GEOpenGL33 implementation...");
        gl33 = new GL33Standard();
        gl33 = new GL33Profiler(gl33);  //TODO: profiler if settings debug

        System.out.println("GEOpenGL33: setting up GLFW bindings and GL capabilities...");
        window = setupCapabilities();
        System.out.println("GEOpenGL33: GLFW bindings and GL capabilities finished setup");

        System.out.println("GEOpenGL33: Preparing default shaders...");
        int numShaders = setupDefaultShaders();
        System.out.printf("GEOpenGL33: %d default shaders prepared\n", numShaders);

        Pixel background = settings.getBackgroundColor();
        glClearColor(background.rn(), background.gn(), background.bn(), background.an());
    }

    // TODO: temp
    private GLApplicationWindow applicationWindow;
    public GLApplicationWindow getApplicationWindow() { return applicationWindow; }

    //TODO: probably move this along with setupCapabilities
    public void renderLoop(IRenderable core) {
        float deltaTime = 0f;
        long start = System.currentTimeMillis();

        while (!glfwWindowShouldClose(window)) {
            long millis = System.currentTimeMillis();
            deltaTime += (millis - start)/1000f;
            start = millis;

            // TODO: update loop not needed. ECS handles this
            float oldDeltaTime = deltaTime;
            while (deltaTime >= FPS) {
                deltaTime -= FPS;
                core.update(FPS);
            }

            if (oldDeltaTime >= FPS) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                core.render(); //render once
                glfwSwapBuffers(window);
            }

            //todo: read: https://www.glfw.org/docs/3.3/input_guide.html
            glfwPollEvents();
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        GLFWErrorCallback c = glfwSetErrorCallback(null);
        if (c != null) c.free();
    }

    @Override
    public GLMesh createMesh(Material material, long numVertices) {
        return new GLMesh(material, numVertices);
    }

    //TODO: probably move this elsewhere
    private long setupCapabilities() {

        GLFWErrorCallback.createPrint();
        //GLFWErrorCallback.createPrint(System.err).set(); //TODO: which one?
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        //TODO: is this 3.3 or 3.2 ?
        GLApplicationWindowSettings.GLVersionSettings glVersion = new GLApplicationWindowSettings.GLVersionSettings(3, 2, GLFW_OPENGL_CORE_PROFILE, GLFW_TRUE);
        GLApplicationWindowSettings windowSettings = new GLApplicationWindowSettings("Dance to the verse", DISPLAY_WIDTH, DISPLAY_HEIGHT, glVersion);

        //windowSettings.setMaximized(true);
        //windowSettings.setDecorated(false);

        // TODO temp
        applicationWindow = GLApplicationWindow.createWindow(windowSettings);

        window = applicationWindow.getWindowGLID();
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        // TODO: hook up to new system
        /*
        // NOTE: keys have been hooked up to new system, see GLApplicationWindow
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true);
            //System.out.println("EVENT KEY: " + key);
            if (action == GLFW_PRESS) {
                KeyEventTracker.notifyKeyDown(key);
            } else if (action == GLFW_RELEASE) {
                KeyEventTracker.notifyKeyUp(key);
            } else if (action == GLFW_REPEAT) {
                KeyEventTracker.notifyKeytyped(key);
            } else {
                Assertions.warn("unknown action: %d", action);
            }
        });

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> MouseInputManager.notifyMouseMove((int)xpos, (int)ypos));
        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            if (action == GLFW_PRESS) MouseInputManager.notifyButtonDown(button);
            else if (action == GLFW_RELEASE) MouseInputManager.notifyButtonUp(button);
            else Assertions.warn("unknown action: %d", action);
        });
        glfwSetScrollCallback(window, (window, xoffset, yoffset) -> MouseInputManager.notifyMouseScrolled((float)xoffset, (float)yoffset));
*/

        /*
        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            DISPLAY_WIDTH = width;
            DISPLAY_HEIGHT = height;
            System.out.printf("RESIZE: %d x %d\n", width, height);
            // TODO temp: go via IMPL
            updateViewport(width, height);
        });
        */




        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();

        //TODO: check https://www.tabnine.com/web/assistant/code/rs/5c659c5a1095a5000166ad2a#L33. How relevant is this?
        if (GL.getCapabilities().GL_ARB_debug_output) {
            System.out.println("Verse: openGL GL_ARB_debug_output enabled");
            ARBDebugOutput.glDebugMessageCallbackARB(new GLDebugMessageARBCallback() {
                @Override
                public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
                    System.out.printf("Verse: openGL GL_ARB_debug_output message: source: %d, type: %d, id: %d, severity: %d, length: %d, message: %d, userParam: %d\n", source, type, id, severity, length, message, userParam);
                    System.out.println(GLDebugMessageCallback.getMessage(length, message));
                }
            }, 160298);
        }

        return window;
    }

    // TODO TEMP
    private void updateViewport(int width, int height) {

        /*
        //note: still has stretching
        //inspired from LibGDX. look for custom fit solution

        float worldWidth = (float)DISPLAY_WIDTH;
        float worldHeight = (float)DISPLAY_HEIGHT;

        float screenWidth = (float)width;
        float screenHeight = (float)height;

        float targetRatio = screenHeight / screenWidth;
        float sourceRatio = worldHeight / worldWidth;

        float scale = (targetRatio < sourceRatio ? screenWidth / worldWidth : screenHeight / worldHeight);

        int viewportWidth = Math.round(worldWidth * scale);
        int viewportHeight = Math.round(worldHeight * scale);

        int x = (int)((screenWidth - viewportWidth) / 2);
        int y = (int)((screenHeight - viewportHeight) / 2);
        int w = (int)viewportWidth;
        int h = (int)viewportHeight;

        GL30.glViewport(x, y, w, h);
        System.out.println("Viewport: width=" + width + " height=" + height + " = x=" + x + " y=" + y + " w=" + w + " h=" + h);
*/
        //GL30.glViewport(0, 0, width, height); // same result??
        //GL30.glViewport(0, 0, 300, 300); // think I understand viewports now. just need to center camera too (in example, anyway)

    }

    //TODO: store shader somewhere
    //TODO: localized predefined uniform values
    private int setupDefaultShaders() {
        GLShader spritebatchShader = new GLShader(Shader.PredefinedAttributes.BASIC_SPRITEBATCH,
                UniformLayout.Predefined.SPRITEBATCH, Files.getExternalPath("assets/shaders/basic_spritebatch.glsl"));
        spritebatchShader.compile();
        AssetPool.registerShader(new Shader.Reference(this.getClass(), Shader.Predefined.BASIC_SPRITEBATCH), spritebatchShader);

        GLShader pos3dandcolorShader = new GLShader(Shader.PredefinedAttributes.POS_3D_AND_COLOR,
                UniformLayout.Predefined.MVP_LAYOUT, Files.getExternalPath("assets/shaders/pos_3d_and_color.glsl"));
        pos3dandcolorShader.compile();
        AssetPool.registerShader(new Shader.Reference(this.getClass(), Shader.Predefined.POS_3D_AND_COLOR), pos3dandcolorShader);

        return 2;
    }



    //TODO: insignificant, but first frame is white
    //TODO: look into https://stackoverflow.com/questions/4052940/how-to-make-an-opengl-rendering-context-with-transparent-background

    //TODO: move this
    public interface IRenderable {
        void update(float dt);
        void render();
    }


    //TODO: look into FBOs
    /*
    private void makeFBO() {
        fboID = glGenFramebuffers();

        texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 1920, 1080, 0, GL_RGB, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        int rbo = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rbo);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, 1920, 1080);

        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboID);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texID, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "ERROR::FRAMEBUFFER:: Framebuffer is not complete!";
        }

        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }
    */

}
