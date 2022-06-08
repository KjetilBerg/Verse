package com.kbindiedev.verse;

import com.kbindiedev.verse.gfx.*;
import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.input.keyboard.IKeyEventListener;
import com.kbindiedev.verse.input.keyboard.KeyEventTracker;
import com.kbindiedev.verse.input.keyboard.Keys;
import com.kbindiedev.verse.input.mouse.IMouseInputProcessor;
import com.kbindiedev.verse.input.mouse.MouseButtons;
import com.kbindiedev.verse.input.mouse.MouseInputManager;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class GeneralTesting implements GEOpenGL33.IRenderable, IKeyEventListener, IMouseInputProcessor {
    private SpriteBatch spriteBatch;


    /** Gets run from Main */
    public static void run() {

        /*
        RESTClientRequestSettings requestSettings = new RESTClientRequestSettings();
        requestSettings.setEnforceSSL(false);
        RESTClient.setGlobalRequestSettings(requestSettings);

        RESTClientSettings apiSettings = new RESTClientSettings();
        apiSettings.setPreserveCookies(true);
        RESTClient api = new RESTClient("http://localhost:8080");
        api.setClientSettings(apiSettings);

        RESTClientSettings apiSettings2 = new RESTClientSettings();
        apiSettings2.setPreserveCookies(false);
        //api.setClientSettings(apiSettings2);
        try {
            //api.get("/hello");
            System.out.println("request 1:");
            RESTClientResponse response = api.newRequest().method("GET").path("/hello").param("file","true").execute();
            System.out.println("Got length: " + response.contentAsString().length());
            response.disconnect();

            System.out.println("request 2 (stream):");
            RESTClientResponse response2 = api.newRequest().method("GET").path("/hello").execute();
            try (InputStream stream = response2.stream()) {
                byte[] arr = new byte[8192];
                int amountRead = stream.read(arr, 0, arr.length);
                System.out.printf("Read '%d' bytes from request 2 body\n", amountRead);
            }
            response2.disconnect();

            System.out.println("request 3:");
            RESTClientResponse response3 = api.newRequest().method("GET").path("/file").execute();
            //System.out.println("response 3: " + response3.contentAsString());
            try (InputStream stream = response3.stream()) {

                FileOutputStream fos = new FileOutputStream("../downloaded-file.txt");

                int d;
                while ((d = stream.read()) != -1) fos.write(d);
                fos.close();

            }
            response3.disconnect();

            System.out.println("request 4:");
            RESTClientResponse response4 = api.newRequest().method("GET").path("/redirect").execute();
            System.out.println("response 4: " + response4.contentAsString());
            System.out.println("response 4 destination url: " + response4.getDestinationURL());
            System.out.println("response 4 destination uri: " + response4.getDestinationURI());
            response4.disconnect();

            System.out.println("request 5:");
            RESTClientResponse response5 = api.newRequest().method("PUT").path("/put-test").param("hello", "okay").execute();
            System.out.println("response 5: " + response5.contentAsString());
            response5.disconnect();

            System.out.println("request 6:");
            RESTClientResponse response6 = api.newRequest().method("POST").path("/reflect").param("code", "205").execute();
            System.out.println("response 6: " + response6.getStatusCode() + " " + response6.contentAsString());
            response6.disconnect();

            System.out.println("request 7:");
            RESTClientRequest request7 = api.newRequest().method("GET").path("/reflect").param("code", "408");
            RESTClientResponse response7 = request7.execute();
            System.out.println("response 7: " + response7.getStatusCode() + " " + response7.contentAsString());
            response7.disconnect();

            System.out.println("request 8:");
            RESTClientResponse response8 = request7.clone().param("code", "409").execute();
            System.out.println("response 8: " + response8.getStatusCode() + " " + response8.contentAsString());
            response8.disconnect();


        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("CHECKING COOKIES...");
        try {
            URI uri = new URI("http://localhost:8080/banana");
            List<HttpCookie> cookies = api.getCookies(uri);
            System.out.println(cookies);
        } catch (Exception e) { e.printStackTrace(); }

        System.exit(0);
        */

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

        //KeyEventTracker.setProcessor(this);
        //MouseInputManager.setProcessor(this);

        spriteBatch = new SpriteBatch(gfx, 1, 8);
        //spriteBatch.setColor(new Pixel(255, 255, 0));
        proj = new Matrix4f();
        proj.ortho(-2f, 2f, 2f, -2f, -1f, 1f);
        spriteBatch.setProjectionMatrix(proj);
        //spriteBatch.setGlobalFlipSettings(false, true);

        tex = new GLTexture("assets/img/smile.png");

        gfx.renderLoop(this);

    }

    @Override
    public void update(float dt) {
        //KeyEventTracker.handleEvents(); //TODO: this should be put elsewhere
        //MouseInputManager.handleEvents();
        //System.out.println("Update: " + dt);
        doKeyStates();
        x += dx * dt;
        y += dy * dt;

        //if (KeyEventTracker.wasKeyPressedThisFrame(Keys.KEY_S)) y += 0.5f;

        //if (KeyEventTracker.isKeyDown(Keys.KEY_W)) System.out.println("W IS DOWN");
        //if (MouseInputManager.isButtonDown(MouseButtons.LEFT)) System.out.printf("LEFT is down at: %d %d\n", MouseInputManager.getMouseX(), MouseInputManager.getMouseY());
        //if (MouseInputManager.wasButtonPressedThisFrame(MouseButtons.RIGHT)) System.out.println("RIGHT was pressed this frame");
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
    public boolean keyDownNow(int keycode) {
        return false;
    }

    @Override
    public boolean keyUpNow(int keycode) {
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
