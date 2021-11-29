package com.kbindiedev.verse.gfx.impl.opengl_33;

import com.kbindiedev.verse.AssetPool;
import com.kbindiedev.verse.gfx.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;
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
    private int DISPLAY_WIDTH = 300, DISPLAY_HEIGHT = 300;

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

        glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
    }

    //TODO: probably move this along with setupCapabilities
    public void renderLoop(IRenderable core) {
        float deltaTime = 0f;
        long start = System.currentTimeMillis();

        while (!glfwWindowShouldClose(window)) {
            long millis = System.currentTimeMillis();
            deltaTime += (millis - start)/1000f;
            start = millis;

            float oldDeltaTime = deltaTime;
            while (deltaTime >= FPS) {
                deltaTime -= FPS;
                core.update(FPS);
            }
            if (oldDeltaTime >= FPS) {
                System.out.println("new frame");
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
        glfwSetErrorCallback(null).free();
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


        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);                  //TODO: is this 3.3 or 3.2 ?
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        window = glfwCreateWindow(DISPLAY_WIDTH, DISPLAY_HEIGHT, "Dance to the verse", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true);
            System.out.println("EVENT KEY: " + key);
        });

        glfwSetWindowSizeCallback(window, (window, width, height) -> {
            DISPLAY_WIDTH = width;
            DISPLAY_HEIGHT = height;
            System.out.printf("RESIZE: %d x %d\n", width, height);
        });



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

    //TODO: store shader somewhere
    //TODO: localized predefined uniform values
    private int setupDefaultShaders() {
        GLShader spritebatchShader = new GLShader(Shader.PredefinedAttributes.BASIC_SPRITEBATCH, UniformLayout.Predefined.SPRITEBATCH, "assets/shaders/basic_spritebatch.glsl");
        spritebatchShader.compile();
        AssetPool.registerShader(new Shader.Reference(this.getClass(), Shader.Predefined.BASIC_SPRITEBATCH), spritebatchShader);

        return 1;
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
