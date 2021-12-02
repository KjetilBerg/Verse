package com.kbindiedev.verse;

import com.kbindiedev.verse.gfx.*;
import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.input.keyboard.IKeyboardInputProcessor;
import com.kbindiedev.verse.input.keyboard.KeyboardInputManager;
import com.kbindiedev.verse.input.keyboard.Keys;
import com.kbindiedev.verse.input.mouse.IMouseInputProcessor;
import com.kbindiedev.verse.input.mouse.MouseButtons;
import com.kbindiedev.verse.input.mouse.MouseInputManager;
import com.kbindiedev.verse.net.rest.RestApiConnection;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class GeneralTesting implements GEOpenGL33.IRenderable, IKeyboardInputProcessor, IMouseInputProcessor {
    private SpriteBatch spriteBatch;


    /** Gets run from Main */
    public static void run() {
        //GraphicsEngine gfx = new GEOpenGL33();
        GEOpenGL33 gfx = new GEOpenGL33();
        GraphicsEngineSettings settings = new GraphicsEngineSettings();

        gfx.initialize(settings);


        System.out.println("Running checks...");
        ensureAllPredefinedSetup();
        System.out.println("Checks ok. Starting program");

        new GeneralTesting(gfx);
    }

    //TODO: should only check for "currently-in-use" implementations. as for other testing, I suppose all would have to be tested individually, or alternatively some other registry. maybe some .hasFor(ShaderKey)
    //check predefined shaders and materials
    private static void ensureAllPredefinedSetup() {
        List<Class<? extends GraphicsEngine>> implementations = Arrays.asList(GEOpenGL33.class);

        for (Class<? extends GraphicsEngine> impl : implementations) {
            for (Shader.Predefined shaderKey : Shader.Predefined.values()) {

                Shader.Reference reference = new Shader.Reference(impl, shaderKey);
                Shader shader = AssetPool.getShader(reference);
                if (shader == null) throw new IllegalStateException(String.format("missing shader for reference: %s", reference));

            }
        }

    }

    //TODO: decouple material and shader completely?

    //TODO: following https://www.glfw.org/docs/3.3/input_guide.html#input_keyboard, look into keyboard text input? (for special characters)
    //TODO: potentially other input systems, such as .onWindowFocused(), or cursor leave/enter window area
    //TODO: can poll / use glfwGetCursorPos to get mouse coordinates outside window area (tested, works)
    //TODO: glfw can set cursor position (have implement some cursor-lock?)

    public GeneralTesting(GEOpenGL33 gfx) {

        KeyboardInputManager.setProcessor(this);
        MouseInputManager.setProcessor(this);

        spriteBatch = new SpriteBatch(gfx, 1, 8);
        //spriteBatch.setColor(new Pixel(255, 255, 0));
        proj = new Matrix4f();
        proj.ortho(-2f, 2f, 2f, -2f, -1f, 1f);
        spriteBatch.setProjectionMatrix(proj);
        //spriteBatch.setGlobalFlipSettings(false, true);

        tex = new GLTexture("assets/img/smile.png");

        RestApiConnection api = new RestApiConnection("http://localhost:8080");
        try {
            //api.get("/hello");
            api.newRequest().method("GET").path("/hello").param("file","true").execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.exit(0);


        gfx.renderLoop(this);

    }

    @Override
    public void update(float dt) {
        KeyboardInputManager.handleEvents(); //TODO: this should be put elsewhere
        MouseInputManager.handleEvents();
        //System.out.println("Update: " + dt);
        doKeyStates();
        x += dx * dt;
        y += dy * dt;

        if (KeyboardInputManager.wasKeyPressedThisFrame(Keys.KEY_S)) y += 0.5f;

        //if (KeyboardInputManager.isKeyDown(Keys.KEY_W)) System.out.println("W IS DOWN");
        if (MouseInputManager.isButtonDown(MouseButtons.LEFT)) System.out.printf("LEFT is down at: %d %d\n", MouseInputManager.getMouseX(), MouseInputManager.getMouseY());
        if (MouseInputManager.wasButtonPressedThisFrame(MouseButtons.RIGHT)) System.out.println("RIGHT was pressed this frame");
    }

    private Matrix4f proj;
    private Texture tex;
    private float x = 0f, y = 0f;
    private float dx = 0f, dy = 0f;

    @Override
    public void render() {
        //mesh.render();

        spriteBatch.setProjectionMatrix(proj);

        spriteBatch.begin();
        //spriteBatch.draw(tex, 0, 0, 0, 0, 1f, 1f, 1f, 1f, 0, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
        spriteBatch.draw(tex, x, y, 1f, 1f);
        spriteBatch.end();

        //System.exit(0);
    }

    private boolean w,a,s,d;
    private void doKeyStates() {
        dx = 0; dy = 0;
        if (w) dy -= 1f;
        if (s) dy += 1f;
        if (a) dx -= 1f;
        if (d) dx += 1f;

    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case 87: w=true; break;
            case 65: a=true; break;
            case 83: s=true; break;
            case 68: d=true; break;
        }
        System.out.println("keydown " + keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case 87: w=false; break;
            case 65: a=false; break;
            case 83: s=false; break;
            case 68: d=false; break;
        }
        return true;
    }

    @Override
    public boolean keyTyped(int keycode) {
        return false;
    }

    @Override
    public boolean mouseDown(int screenX, int screenY, int button) {
        System.out.printf("BDOWN: x: %d, y: %d, btn: %d\n", screenX, screenY, button);
        return true;
    }

    @Override
    public boolean mouseUp(int screenX, int screenY, int button) {
        System.out.printf("BUP: x: %d, y: %d, btn: %d\n", screenX, screenY, button);
        return true;
    }

    @Override
    public boolean mouseClicked(int screenX, int screenY, int button, float holdDuration) {
        System.out.printf("CLICK: x: %d, y: %d, btn: %d, duration: %f\n", screenX, screenY, button, holdDuration);
        return true;
    }

    @Override
    public boolean mouseDragged(int screenX, int screenY) {
        System.out.printf("DRAGGED: x: %d, y: %d\n", screenX, screenY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        System.out.printf("MOVED: x: %d, y: %d\n", screenX, screenY);
        return true;
    }

    @Override
    public boolean mouseScrolled(float amountX, float amountY) {
        System.out.printf("SCROLLED: x: %f, y: %f\n", amountX, amountY);

        //ugly zoom function, but works for testing
        float scale = 1f;
        while (amountY < 0) { amountY += 1; scale *= 0.9; }
        while (amountY > 0) { amountY -= 1; scale *= 1.1; }
        proj.scale(scale, scale, 1f);
        return true;
    }


    private static class IndexData implements IIndexData {

        private ByteBuffer buffer;
        private int numIndices;

        public IndexData(short[] data) {
            buffer = BufferUtils.createByteBuffer(data.length * 2);
            for (short s : data) buffer.putShort(s);
            buffer.flip();
            numIndices = data.length;
        }

        public int getNumIndices() { return numIndices; }
        public ByteBuffer getBuffer() { return buffer; }
    }
}
