package com.kbindiedev.verse;

import com.kbindiedev.verse.gfx.*;
import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.input.keyboard.IKeyboardInputProcessor;
import com.kbindiedev.verse.input.keyboard.KeyboardInputManager;
import com.kbindiedev.verse.input.keyboard.Keys;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class GeneralTesting implements GEOpenGL33.IRenderable, IKeyboardInputProcessor {
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

    public GeneralTesting(GEOpenGL33 gfx) {

        KeyboardInputManager.setProcessor(this);

        spriteBatch = new SpriteBatch(gfx, 1, 8);
        //spriteBatch.setColor(new Pixel(255, 255, 0));
        Matrix4f proj = new Matrix4f();
        proj.ortho(-2f, 2f, 2f, -2f, -1f, 1f);
        spriteBatch.setProjectionMatrix(proj);
        //spriteBatch.setGlobalFlipSettings(false, true);

        tex = new GLTexture("assets/img/smile.png");

        gfx.renderLoop(this);

    }

    @Override
    public void update(float dt) {
        KeyboardInputManager.handleEvents(); //TODO: this should be put elsewhere
        System.out.println("Update: " + dt);
        doKeyStates();
        x += dx * dt;
        y += dy * dt;

        if (KeyboardInputManager.wasKeyPressedThisFrame(Keys.KEY_S)) y += 0.5f;

        if (KeyboardInputManager.isKeyDown(Keys.KEY_W)) System.out.println("W IS DOWN");
    }

    private Texture tex;
    private float x = 0f, y = 0f;
    private float dx = 0f, dy = 0f;

    @Override
    public void render() {
        //mesh.render();

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
