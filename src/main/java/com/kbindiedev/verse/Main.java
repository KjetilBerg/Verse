package com.kbindiedev.verse;

import com.kbindiedev.verse.animation.*;
import com.kbindiedev.verse.async.ThreadPool;
import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.RenderContext;
import com.kbindiedev.verse.ecs.Space;
import com.kbindiedev.verse.ecs.components.*;
import com.kbindiedev.verse.ecs.generators.TilemapToEntitiesGenerator;
import com.kbindiedev.verse.ecs.net.NetworkManager;
import com.kbindiedev.verse.ecs.systems.*;
import com.kbindiedev.verse.gfx.GraphicsEngineSettings;
import com.kbindiedev.verse.gfx.Pixel;
import com.kbindiedev.verse.gfx.Sprite;
import com.kbindiedev.verse.io.net.socket.TCPSocket;
import com.kbindiedev.verse.ui.font.BitmapFont;
import com.kbindiedev.verse.ui.font.BitmapFontLoader;
import com.kbindiedev.verse.gfx.impl.opengl_33.GEOpenGL33;
import com.kbindiedev.verse.gfx.impl.opengl_33.GLTexture;
import com.kbindiedev.verse.gfx.strategy.providers.ColoredPolygon;
import com.kbindiedev.verse.io.files.Files;
import com.kbindiedev.verse.maps.*;
import com.kbindiedev.verse.math.helpers.PolygonMaker;
import com.kbindiedev.verse.math.shape.Polygon;
import com.kbindiedev.verse.math.shape.Rectanglef;
import com.kbindiedev.verse.sfx.Sound;
import com.kbindiedev.verse.sfx.SoundEngineSettings;
import com.kbindiedev.verse.sfx.Source;
import com.kbindiedev.verse.sfx.impl.openal_10.SEOpenAL10;
import com.kbindiedev.verse.ui.font.GlyphSequence;
import com.kbindiedev.verse.util.condition.Condition;
import com.kbindiedev.verse.util.condition.ConditionEqual;
import com.kbindiedev.verse.util.condition.ConditionTrigger;
import com.kbindiedev.verse.z_example.*;
import org.joml.Vector3f;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    //TODO: note tag ordering (esp. @throws) https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
    //TODO: verse exceptions ?


    // TODO: add @kb.time O(x) to many methods


    public static void main(String[] args) {

        //GeneralTesting.run();
        runECSTest();

    }

    public static Sprite playerSprite = null; // TODO very temp
    public static BitmapFont TEMP_GLOBAL_FONT = null; // TODO very temp

    private static void runECSTest() {
        GEOpenGL33 gl33 = new GEOpenGL33();
        gl33.initialize(new GraphicsEngineSettings());

        SEOpenAL10 al10 = new SEOpenAL10();
        al10.initialize(new SoundEngineSettings());

        space = new Space(gl33);

        // TODO: something like applicationWindow.attachInputs(space) may be ideal.
        space.getInput().getKeyboardPipeline().setQueue(gl33.getApplicationWindow().getKeyboardQueue());

        /*
        String fontPath = "../arial.fnt";
        System.out.println("attempting to load font: " + fontPath);
        try {
            BitmapFont font = BitmapFontLoader.getInstance().load(new File(fontPath), "bmfont");
            System.out.println(font);
        } catch (IOException e) { e.printStackTrace(); }
        */


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
        Entity playerEntity = null;
        try {
            Tilemap testTileMap = TileMapLoader.loadTileMap(new File("./../spritepack_demo/paths.tmx"));
            TilemapToEntitiesGenerator.generateEntities(space.getEntityManager(), testTileMap);

            // TODO: somehow put inside some TilemapManager ?
            ObjectLayer soundmap = testTileMap.getLayerByName("soundmap2", ObjectLayer.class);
            if (soundmap != null) {
                HashMap<String, Source> sources = new HashMap<>();
                for (MapObject o : soundmap.getMapObjects().getAllObjects()) {
/*
                    if (!o.isContentOfType(MapObjectPolygon.class)) {
                        System.out.println("not polygon");
                        continue;
                    }
                    MapObjectPolygon content = o.getContentAs(MapObjectPolygon.class);
                    Polygon polygon = content.getPolygon();
                    */
                     // TODO: some "object autogen polygon thing"


                    int yOffset = 20 * 16; // very temp
                    List<Vector3f> list = new ArrayList<>();
                    list.add(new Vector3f(o.getX(), o.getY() + o.getHeight(), 0f));
                    list.add(new Vector3f(o.getX() + o.getWidth(), o.getY() + o.getHeight(), 0f));
                    list.add(new Vector3f(o.getX() + o.getWidth(), o.getY(), 0f));
                    list.add(new Vector3f(o.getX(), o.getY(), 0f));
                    list.forEach(v -> { v.y -=8; v.x -= 8; });
                    list.forEach(v -> v.y = yOffset - v.y);
                    Polygon polygon = new Polygon(list);

                    System.out.println("Generating soundmap: " + o.getName());

                    PolygonCollider2D collider = new PolygonCollider2D();
                    collider.isTrigger = true;
                    collider.polygon = polygon;

                    GroundNoiseComponent noise = new GroundNoiseComponent();
                    String filepath = "../" + o.getName() + "-walk.wav";
                    if (!sources.containsKey(filepath)) {
                        try {
                            Sound sound = al10.createSound(filepath);
                            Source source = al10.createSource(true);
                            source.setSound(sound);
                            sources.put(filepath, source);
                        }
                        catch (Exception e) { e.printStackTrace(); }
                    }

                    noise.source = sources.get(filepath);

                    space.getEntityManager().instantiate(collider, noise);

                }
            }



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
            playerTransform.position.z = 0.4f;
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

            exampleComponent.text = new GlyphSequence("Once upon a time...");
            exampleComponent.textSize = 6;

            PolygonCollider2D collider = new PolygonCollider2D();
            Polygon polygon = new Polygon();
            polygon.addPoint(new Vector3f(-7.5f, -12f, 0f).div(playerTransform.scale));
            polygon.addPoint(new Vector3f(8.5f, -12f, 0f).div(playerTransform.scale));
            polygon.addPoint(new Vector3f(8.5f, -6f, 0f).div(playerTransform.scale));
            polygon.addPoint(new Vector3f(-7.5f, -6f, 0f).div(playerTransform.scale));
            collider.polygon = polygon;

            // TODO: animator controls offset
            SpriteRenderer renderer = new SpriteRenderer();
            renderer.offset.set(0f, 6f);

            playerSprite = animator.controller.pickAnimation().getCurrentSprite();

            PlayerComponent playerComponent = new PlayerComponent();
            try {
                Sound x = al10.createSound("../grass-walk.wav");
                Source y = al10.createSource(true);
                y.setSound(x);
                playerComponent.walkSound = y;
                playerComponent.defaultWalkSound = y;
            } catch (Exception e) { throw new RuntimeException(e); }

            playerEntity = space.getEntityManager().instantiate(exampleComponent, animator, renderer, playerTransform, playerComponent, collider, new RigidBody2D());

            PolygonCollider2D collider2 = new PolygonCollider2D();
            Polygon polygon2 = new Polygon();
            polygon2.addPoint(new Vector3f(0f, 0f, 0f).div(16f));
            polygon2.addPoint(new Vector3f(96f, 0f, 0f).div(16f));
            polygon2.addPoint(new Vector3f(96f, 16f, 0f).div(16f));
            polygon2.addPoint(new Vector3f(0f, 16f, 0f).div(16f));
            collider2.polygon = polygon2;
            SpriteRenderer renderer2 = new SpriteRenderer();
            renderer2.sprite = TilesetResourceFetcher.getSprite(testTileMap.getTileset(), 347);
            Transform transform2 = new Transform();
            transform2.position.x = 20f;
            transform2.position.y = 20f;
            transform2.position.z = 1f;
            transform2.scale.x = 32f; transform2.scale.y = 32f;
            //space.getEntityManager().instantiate(collider2, renderer2, transform2);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera camera = new Camera();
        camera.aspectRatio = 16f/9;
        camera.zoom = 250f;
        camera.bounds = new Rectanglef(0 * 16f -8f, 1 * 16f -8f, 30 * 16f, 20 * 16f);
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

        ShapeRenderer shape1 = new ShapeRenderer();
        ConstantRotatorComponent rotatorComponent = new ConstantRotatorComponent();
        rotatorComponent.amountPerSecond = 1f;
        //ColoredPolygon polygon1 = ColoredPolygonGenerator.generatePolygon(Pixel.SOLID_WHITE, 5, 32f, 0f, false);
        ColoredPolygon polygon1 = ColoredPolygon.fromPolygon(PolygonMaker.generatePolygon(5, 32f, 0f), Pixel.SOLID_WHITE);

        polygon1.setColor(0, new Pixel(255, 255, 255));
        polygon1.setColor(1, new Pixel(255, 0, 0));
        polygon1.setColor(2, new Pixel(255, 255, 0));
        polygon1.setColor(3, new Pixel(0, 255, 0));
        polygon1.setColor(4, new Pixel(0, 255, 255));

        shape1.polygon = polygon1;

        polygon1.translateTo(new Vector3f(32f, 64f, 0f));
        Transform polygonTransform = new Transform();
        polygonTransform.position = polygon1.getCenter();

        space.getEntityManager().instantiate(shape1, rotatorComponent, polygonTransform);


        BitmapFont arialFont = null;
        try {
            arialFont = BitmapFontLoader.getInstance().load(new File("../arial.fnt"));
            TEMP_GLOBAL_FONT = arialFont;
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextChatComponent chat = new TextChatComponent();
        chat.target = playerTransform;
        chat.currentText = new GlyphSequence("Hello");
        TextRenderer text = new TextRenderer();
        text.fontSize = 14;
        text.font = arialFont;
        Transform textLocation = new Transform(); // 0,0
        space.getEntityManager().instantiate(chat, text, textLocation);

        // walls around stage TODO: constraints in physics ? (enforce body >= 0 (x,y) ?)
        float x1 = -8f, y1 = 8f, x2 = 30 * 16f - 8f, y2 = 20 * 16f, thickness = 20f;
        Polygon wallLeft = new Polygon(), wallTop = new Polygon(), wallRight = new Polygon(), wallBottom = new Polygon();
        wallLeft.addPoint(new Vector3f(x1-thickness, y1, 0f));
        wallLeft.addPoint(new Vector3f(x1-thickness, y2, 0f));
        wallLeft.addPoint(new Vector3f(x1, y2, 0f));
        wallLeft.addPoint(new Vector3f(x1, y1, 0f));
        wallRight.addPoint(new Vector3f(x2, y1, 0f));
        wallRight.addPoint(new Vector3f(x2, y2, 0f));
        wallRight.addPoint(new Vector3f(x2+thickness, y2, 0f));
        wallRight.addPoint(new Vector3f(x2+thickness, y1, 0f));
        wallBottom.addPoint(new Vector3f(x1, y1-thickness, 0f));
        wallBottom.addPoint(new Vector3f(x1, y1, 0f));
        wallBottom.addPoint(new Vector3f(x2, y1, 0f));
        wallBottom.addPoint(new Vector3f(x1, y1-thickness, 0f));
        wallTop.addPoint(new Vector3f(x1, y2, 0f));
        wallTop.addPoint(new Vector3f(x2, y2, 0f));
        wallTop.addPoint(new Vector3f(x2, y2+thickness, 0f));
        wallTop.addPoint(new Vector3f(x1, y2+thickness, 0f));
        Polygon[] walls = new Polygon[]{ wallLeft, wallRight, wallBottom, wallTop };
        for (Polygon p : walls) {
            PolygonCollider2D collider = new PolygonCollider2D();
            collider.polygon = p;
            space.getEntityManager().instantiate(collider);
        }

        // play ambient sound
        try {
            Source s = al10.createSource(true);
            Sound so = al10.createSound("../day-ambience.wav");
            s.setSound(so);
            s.play();
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }


        RenderContextPreparerSystem rcps = new RenderContextPreparerSystem(space);
        space.addSystem(new PlayerMovementSystem(space));
        space.addSystem(new PlayerNetSyncSystem(space));
        space.addSystem(new PhysicsManagerSystem(space));
        space.addSystem(new ConstantRotatorSystem(space));
        space.addSystem(rcps);
        space.addSystem(new CameraSystem(space));
        space.addSystem(new SpriteAnimatorSystem(space));
        space.addSystem(new SpriteRendererSystem(space));
        space.addSystem(new ShapeRendererSystem(space));
        space.addSystem(new ExampleSystem(space));
        space.addSystem(new TextChatSystem(space));
        space.addSystem(new TextRendererSystem(space));

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

            @Override
            public void shutdown() {
                System.out.println("Shutting down engine...");
                al10.close();
                ThreadPool.INSTANCE.shutdown();
                try {
                    if (socket != null) socket.disconnect();
                } catch (IOException e) { e.printStackTrace(); socket.terminate(); }
            }
        });
    }

    private static Space space;
    private static TCPSocket socket = null;

    public static void tempActivateServerMode(String text) {
        if (socket != null) return;

        URL url;
        try { url = new URL(text); } catch (MalformedURLException e) { return; }

        socket = new TCPSocket(url.getHost(), url.getPort());
        System.out.println("Attempting to connect to: " + url.getHost() + ":" + url.getPort());
        try { socket.connect(); } catch (IOException e) { e.printStackTrace(); }

        if (!socket.isConnected()) { socket = null; return; }

        NetworkManager networkManager = new NetworkManager(space, socket);

        ThreadPool.INSTANCE.submitContinous(() -> {
            try {
                networkManager.processStream();
            } catch (IOException e) {
                space.setNetworkManager(null);
                throw new RuntimeException(e);
            }
        });

        space.setNetworkManager(networkManager);
    }

}
