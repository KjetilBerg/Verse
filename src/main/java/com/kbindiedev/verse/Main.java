package com.kbindiedev.verse;

import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.EntityManager;
import com.kbindiedev.verse.ecs.RenderContext;
import com.kbindiedev.verse.ecs.Space;
import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.ecs.systems.CameraSystem;
import com.kbindiedev.verse.ecs.systems.SpriteRendererSystem;
import com.kbindiedev.verse.gfx.GraphicsEngineSettings;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import org.lwjgl.opengl.GL30;

public class Main {

    //TODO: note tag ordering (esp. @throws) https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
    //TODO: verse exceptions ?


    // TODO: add @kb.time O(x) to many methods


    public static void main(String[] args) {

        //GeneralTesting.run();
        runECSTest();

    }

    private static void runECSTest() {
        GEOpenGL33 gl33 = new GEOpenGL33();
        gl33.initialize(new GraphicsEngineSettings());

        Space space = new Space(gl33);

        // TODO: something like applicationWindow.attachInputs(space) may be ideal.
        space.getInput().getKeyboardPipeline().setQueue(gl33.getApplicationWindow().getKeyboardQueue());

        // TODO next: viewport shenanigans

        SpriteRenderer sprite = new SpriteRenderer();
        sprite.color = new Pixel(1f, 1f, 1f, 1f);
        //sprite.sprite = new Sprite(new GLTexture("./../example.png"));
        sprite.sprite = new Sprite(new GLTexture("assets/img/smile.png"));   // TODO: renders same image as below
        space.getEntityManager().instantiate(sprite);

        SpriteRenderer sprite2 = new SpriteRenderer();
        sprite2.color = new Pixel(1f, 1f, 0f, 1f);
        sprite2.sprite = new Sprite(new GLTexture("./../example.png"));
        Transform transform = new Transform();
        transform.position.x = 0.5f;
        transform.position.y = 0.3f;
        space.getEntityManager().instantiate(sprite2, transform);

        ExampleComponent c1 = new ExampleComponent();
        ExampleComponent c2 = new ExampleComponent();
        c1.data = "c1 here";
        c2.data = "I am c2";

        space.getEntityManager().instantiate(c1);
        space.getEntityManager().instantiate(c2);

        Camera camera = new Camera();
        camera.aspectRatio = 16f/9;
        GL30.glViewport(0, 0, 1920, 1080); //TODO temp, until RenderingStrategy
        //camera.viewportWidth = 1920; camera.viewportHeight = 1080;
        /*
        camera.viewportWidth = 4f; camera.viewportHeight = camera.viewportWidth * 9 / 16;
        float right = camera.zoom * camera.viewportWidth / 2;
        float bottom = camera.zoom * camera.viewportHeight / 2;
        camera.projectionMatrix.ortho(-right, right, bottom, -bottom, camera.nearPlane, camera.farPlane); */
        Entity cameraEntity = space.getEntityManager().instantiate(camera, new Transform());

        space.addSystem(new ExampleSystem(space));
        space.addSystem(new CameraSystem(space));
        space.addSystem(new SpriteRendererSystem(space));

        RenderContext context = new RenderContext(cameraEntity, gl33.getApplicationWindow());
        gl33.renderLoop(new GEOpenGL33.IRenderable() {
            @Override
            public void update(float dt) {
                //space.tick(); // TODO: take in deltatime??
            }

            @Override
            public void render() {
                space.tick(); // TODO: render MUST happen during .render. throw assertion if called at another time
                space.render(context);
            } // TODO: remove render?
        });
    }

}
