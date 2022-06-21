package com.kbindiedev.verse;

import com.kbindiedev.verse.animation.*;
import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.RenderContext;
import com.kbindiedev.verse.ecs.Space;
import com.kbindiedev.verse.ecs.components.*;
import com.kbindiedev.verse.ecs.generators.LayeredTileMapToEntitiesGenerator;
import com.kbindiedev.verse.ecs.systems.*;
import com.kbindiedev.verse.gfx.GraphicsEngineSettings;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.io.files.Files;
import com.kbindiedev.verse.maps.LayeredTileMap;
import com.kbindiedev.verse.maps.TileMapLoader;
import com.kbindiedev.verse.maps.TilesetResourceFetcher;
import com.kbindiedev.verse.math.helpers.Point2Df;
import com.kbindiedev.verse.math.shape.Polygon;
import com.kbindiedev.verse.math.shape.Rectanglef;
import com.kbindiedev.verse.sfx.Sound;
import com.kbindiedev.verse.sfx.SoundEngineSettings;
import com.kbindiedev.verse.sfx.Source;
import com.kbindiedev.verse.sfx.impl.openal_10.SEOpenAL10;
import com.kbindiedev.verse.util.condition.Condition;
import com.kbindiedev.verse.util.condition.ConditionEqual;
import com.kbindiedev.verse.util.condition.ConditionTrigger;
import com.kbindiedev.verse.z_example.ExampleComponent;
import com.kbindiedev.verse.z_example.ExampleSystem;
import com.kbindiedev.verse.z_example.PlayerComponent;
import com.kbindiedev.verse.z_example.PlayerMovementSystem;

import java.io.File;
import java.io.IOException;

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

        SEOpenAL10 al10 = new SEOpenAL10();
        al10.initialize(new SoundEngineSettings());

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

        Transform playerTransform = new Transform();
        try {
            LayeredTileMap testTileMap = TileMapLoader.loadTileMap(new File("./../spritepack_demo/paths.tmx"));
            LayeredTileMapToEntitiesGenerator.generateEntities(space.getEntityManager(), testTileMap);

            SpriteAnimation playerIdle = TilesetResourceFetcher.getAnimation(testTileMap.getTileset(), 346);
            SpriteAnimation playerRun = TilesetResourceFetcher.getAnimation(testTileMap.getTileset(), 346 + 6);
            SpriteAnimation playerSlash = TilesetResourceFetcher.getAnimation(testTileMap.getTileset(), 346 + 12);
            SpriteAnimation playerDie = TilesetResourceFetcher.getAnimation(testTileMap.getTileset(), 346 + 24);

            playerSlash.setLooping(false);

            SpriteAnimationMap map = new SpriteAnimationMap();

            Condition next = new ConditionTrigger("next");
            Condition prev = new ConditionTrigger("prev");

            map.setEntryState(playerIdle);

            /*
            map.addTransition(new AnimationTransition<>(playerWalk, playerRun, next));
            map.addTransition(new AnimationTransition<>(playerRun, playerSlash, next));
            map.addTransition(new AnimationTransition<>(playerSlash, playerDie, next, 1f, false, AnimationTransition.ExitTimeStrategy.AFTER_LOCAL));
            map.addTransition(new AnimationTransition<>(playerDie, playerWalk, next));

            map.addTransition(new AnimationTransition<>(playerRun, playerWalk, prev));
            map.addTransition(new AnimationTransition<>(playerSlash, playerRun, prev, 1f, false, AnimationTransition.ExitTimeStrategy.AFTER_LOCAL));
            map.addTransition(new AnimationTransition<>(playerDie, playerSlash, prev));
            map.addTransition(new AnimationTransition<>(playerWalk, playerDie, prev));
            */

            map.addTransition(new AnimationTransition<>(playerIdle, playerRun, new ConditionEqual<>("moving", true)));
            map.addTransition(new AnimationTransition<>(playerRun, playerIdle, new ConditionEqual<>("moving", false)));
            map.addTransition(new AnimationTransition<>(playerIdle, playerSlash, new ConditionTrigger("attack")));
            map.addTransition(new AnimationTransition<>(playerRun, playerSlash, new ConditionTrigger("attack")));
            map.addTransition(new AnimationTransition<>(playerSlash, playerIdle, Condition.NONE, 1f, false, AnimationTransition.ExitTimeStrategy.AFTER_LOCAL));

            playerTransform.position.x = 100f;
            playerTransform.position.y = 100f;
            playerTransform.position.z = 0.8f;
            playerTransform.scale.x = playerIdle.getFrames().get(0).getSprite().getWidth();
            playerTransform.scale.y = playerIdle.getFrames().get(0).getSprite().getHeight();

            AnimationController<SpriteAnimation> controller = new AnimationController<>(map, new AnimatorContext());

            SpriteAnimator animator = new SpriteAnimator();
            animator.controller = controller;

            ExampleComponent exampleComponent = new ExampleComponent();

            Sound sound1, sound2;
            Source source1, source2;

            try { sound1 = al10.createSound("../sound.wav"); } catch (Exception e) { throw new RuntimeException(e); }
            source1 = al10.createSource();
            source1.setSound(sound1);
            exampleComponent.genericSoundSource = source1;

            try { sound2 = al10.createSound("../slash.wav"); } catch (Exception e) { throw new RuntimeException(e); }
            source2 = al10.createSource();
            source2.setSound(sound2);
            exampleComponent.slashSoundSource = source2;

            PolygonCollider2D collider = new PolygonCollider2D();
            Polygon polygon = new Polygon(new Point2Df(8f, 8f));
            polygon.addPoint(new Point2Df(0f, 0f));
            polygon.addPoint(new Point2Df(16f, 0f));
            polygon.addPoint(new Point2Df(16f, 16f));
            polygon.addPoint(new Point2Df(0f, 16f));
            collider.polgyon = polygon;

            space.getEntityManager().instantiate(exampleComponent, animator, new SpriteRenderer(), playerTransform, new PlayerComponent(), collider, new RigidBody2D());

            PolygonCollider2D collider2 = new PolygonCollider2D();
            Polygon polygon2 = new Polygon(new Point2Df(8f, 8f));
            polygon2.addPoint(new Point2Df(0f, 0f));
            polygon2.addPoint(new Point2Df(16f, 0f));
            polygon2.addPoint(new Point2Df(16f, 16f));
            polygon2.addPoint(new Point2Df(0f, 16f));
            collider2.polgyon = polygon2;
            SpriteRenderer renderer2 = new SpriteRenderer();
            renderer2.sprite = TilesetResourceFetcher.getSprite(testTileMap.getTileset(), 347);
            Transform transform2 = new Transform();
            transform2.position.x = 20f;
            transform2.position.y = 20f;
            transform2.position.z = 1f;
            transform2.scale.x = 32f; transform2.scale.y = 32f;
            space.getEntityManager().instantiate(collider2, renderer2, transform2);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera camera = new Camera();
        camera.aspectRatio = 16f/9;
        camera.zoom = 250f;
        //camera.bounds = new Rectanglef(0 * 16f, 1 * 16f, 30 * 16f, 20 * 16f);
        //camera.bounds = new Rectanglef(-100f, -100f, 200f, 200f);
        camera.target = playerTransform;
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

        space.getEntityManager().instantiate(new ShapeRenderer());

        RenderContextPreparerSystem rcps = new RenderContextPreparerSystem(space);
        space.addSystem(new ExampleSystem(space));
        space.addSystem(new PlayerMovementSystem(space));
        //space.addSystem(new PolygonCollider2DSystem(space));
        space.addSystem(rcps);
        space.addSystem(new CameraSystem(space));
        space.addSystem(new SpriteAnimatorSystem(space));
        space.addSystem(new SpriteRendererSystem(space));
        space.addSystem(new ShapeRendererSystem(space));

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
