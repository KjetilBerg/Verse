package com.kbindiedev.verse;

import com.kbindiedev.verse.gfx.*;
import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class GeneralTesting implements GEOpenGL33.IRenderable {
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
        System.out.println("Update: " + dt);
    }

    private Texture tex;
    private float y = 0f;

    @Override
    public void render() {
        //mesh.render();

        spriteBatch.begin();
        //spriteBatch.draw(tex, 0, 0, 0, 0, 1f, 1f, 1f, 1f, 0, 0, 0, tex.getWidth(), tex.getHeight(), false, false);
        spriteBatch.draw(tex, 0, y, 1f, 1f);
        spriteBatch.end();

        y = (y + 0.01f) % 1;

        //System.exit(0);
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
