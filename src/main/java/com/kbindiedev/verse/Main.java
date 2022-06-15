package com.kbindiedev.verse;

import com.kbindiedev.verse.animation.SpriteAnimationMap;
import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.RenderContext;
import com.kbindiedev.verse.ecs.Space;
import com.kbindiedev.verse.ecs.components.Camera;
import com.kbindiedev.verse.ecs.components.SpriteAnimator;
import com.kbindiedev.verse.ecs.components.SpriteRenderer;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.animation.SpriteAnimation;
import com.kbindiedev.verse.ecs.generators.LayeredTileMapToEntitiesGenerator;
import com.kbindiedev.verse.ecs.systems.CameraSystem;
import com.kbindiedev.verse.ecs.systems.RenderContextPreparerSystem;
import com.kbindiedev.verse.ecs.systems.SpriteAnimatorSystem;
import com.kbindiedev.verse.ecs.systems.SpriteRendererSystem;
import com.kbindiedev.verse.gfx.GraphicsEngineSettings;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.io.files.Files;
import com.kbindiedev.verse.maps.LayeredTileMap;
import com.kbindiedev.verse.maps.TileMapLoader;
import com.kbindiedev.verse.maps.TilesetResourceFetcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
        sprite.sprite = new Sprite(new GLTexture(Files.getExternalPath("assets/img/smile.png")));

        SpriteRenderer sprite2 = new SpriteRenderer();
        sprite2.color = new Pixel(1f, 1f, 0f, 1f);
        sprite2.sprite = new Sprite(new GLTexture("./../example.png"));
        Transform transform = new Transform();
        transform.position.x = 0.5f;
        transform.position.y = 0.3f;

        //space.getEntityManager().instantiate(sprite);
        //space.getEntityManager().instantiate(sprite2, transform);


        try {
            LayeredTileMap testTileMap = TileMapLoader.loadTileMap(new File("./../spritepack_demo/paths.tmx"));
            LayeredTileMapToEntitiesGenerator.generateEntities(space.getEntityManager(), testTileMap);

            SpriteAnimation playerWalk = TilesetResourceFetcher.getAnimation(testTileMap.getTileset(), 346);
            SpriteAnimation playerRun = TilesetResourceFetcher.getAnimation(testTileMap.getTileset(), 346 + 6);
            SpriteAnimation playerSlash = TilesetResourceFetcher.getAnimation(testTileMap.getTileset(), 346 + 12);
            SpriteAnimation playerDie = TilesetResourceFetcher.getAnimation(testTileMap.getTileset(), 346 + 24);

            SpriteAnimationMap map = new SpriteAnimationMap();
            map.addAnimation(playerWalk);
            map.addAnimation(playerRun);
            map.addAnimation(playerSlash);
            map.addAnimation(playerDie);

            ExampleComponent c1 = new ExampleComponent();
            Transform c1Loc = new Transform();
            c1Loc.position.x = 0f;
            c1Loc.position.y = 0f;
            c1Loc.position.z = 0.8f;
            c1Loc.scale.x = playerWalk.getFrames().get(0).getSprite().getWidth();
            c1Loc.scale.y = playerWalk.getFrames().get(0).getSprite().getHeight();

            SpriteAnimator animator = new SpriteAnimator();
            animator.map = map;
            space.getEntityManager().instantiate(c1, animator, new SpriteRenderer(), c1Loc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera camera = new Camera();
        camera.aspectRatio = 16f/9;
        camera.zoom = 250f;
        //GL30.glViewport(0, 0, 1920, 1080); //TODO temp, until RenderingStrategy
        //camera.viewportWidth = 1920; camera.viewportHeight = 1080;
        /*
        camera.viewportWidth = 4f; camera.viewportHeight = camera.viewportWidth * 9 / 16;
        float right = camera.zoom * camera.viewportWidth / 2;
        float bottom = camera.zoom * camera.viewportHeight / 2;
        camera.projectionMatrix.ortho(-right, right, bottom, -bottom, camera.nearPlane, camera.farPlane); */
        Transform cameraTransform = new Transform();
        cameraTransform.position.x = -160f;
        cameraTransform.position.y = -200f;
        Entity cameraEntity = space.getEntityManager().instantiate(camera, cameraTransform);

        RenderContextPreparerSystem rcps = new RenderContextPreparerSystem(space);
        space.addSystem(new ExampleSystem(space));
        space.addSystem(rcps);
        space.addSystem(new CameraSystem(space));
        space.addSystem(new SpriteAnimatorSystem(space));
        space.addSystem(new SpriteRendererSystem(space));

        RenderContext context = new RenderContext(cameraEntity, gl33.getApplicationWindow(), true);
        rcps.addRenderContext(context);
        gl33.renderLoop(new GEOpenGL33.IRenderable() {
            @Override
            public void update(float dt) {
                //space.tick(); // TODO: take in deltatime??
            }

            @Override
            public void render() {
                space.tick();
                space.render(context); // TODO: render MUST happen during .render. throw assertion if called at another time
            } // TODO: remove render?
        });
    }

}
