package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.event.ICollisionListener;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.gfx.GraphicsEngine;
import com.kbindiedev.verse.gfx.Mesh;
import com.kbindiedev.verse.gfx.PolygonBatch;
import com.kbindiedev.verse.gfx.ShapeDrawer;
import com.kbindiedev.verse.input.InputSystem;
import com.kbindiedev.verse.input.keyboard.KeyEventTracker;
import com.kbindiedev.verse.physics.Collision;
import com.kbindiedev.verse.physics.PhysicsEnvironment;
import com.kbindiedev.verse.physics.PhysicsSystem2D;
import com.kbindiedev.verse.system.FastList;

// TODO: integration tests

/**
 * An overarching ECS instance.
 * Typically referred to as "World" in other games or game engines.
 *
 * Consists of an EntityManager and a set of ComponentSystems. //TODO javadoc verify
 *
 * Spaces are created by SceneLoaders. // TODO verify
 *
 * // TODO change name?? World? Reality? Realm? Universe?
 *
 * "Entities manifest themselves in a Space".
 */
public class Space implements ICollisionListener {

    private PhysicsEnvironment physicsEnvironment;
    private InputSystem input;
    private GraphicsEngine gfxImplementation;
    private EntityManager entityManager;
    private FastList<ComponentSystem> systems;
    private long lastTimestamp;
    private float accumulator;

    private Thread runner;

    private ShapeDrawer shapeDrawer;

    public Space(GraphicsEngine gfxImplementation) { this(gfxImplementation, new InputSystem()); }
    public Space(GraphicsEngine gfxImplementation, InputSystem input) { this(gfxImplementation, input, new EntityManager()); }
    public Space(GraphicsEngine gfxImplementation, InputSystem input, EntityManager entityManager) { // TODO: remove manager ?
        physicsEnvironment = PhysicsSystem2D.newEnvironment(); // TODO on unload, remove environment
        this.gfxImplementation = gfxImplementation;
        this.input = input;
        this.entityManager = entityManager;
        systems = new FastList<>();
        lastTimestamp = System.currentTimeMillis();

        // TODO numbers
        PolygonBatch triangleBatch = new PolygonBatch(gfxImplementation, 2048, 2048, Mesh.RenderMode.TRIANGLES);
        PolygonBatch lineBatch = new PolygonBatch(gfxImplementation, 2048, 2048, Mesh.RenderMode.LINES);
        PolygonBatch pointBatch = new PolygonBatch(gfxImplementation, 2048, 2048, Mesh.RenderMode.POINTS);
        shapeDrawer = new ShapeDrawer(triangleBatch, lineBatch, pointBatch);
    }

    public PhysicsEnvironment getPhysicsEnvironment() { return physicsEnvironment; }

    /**
     * A Space may only have a single GraphicsEngine implementation. This defines how things are rendered.
     * @return the GraphicsEngine implementation that this Space uses.
     */
    public GraphicsEngine getGfxImplementation() { return gfxImplementation; }

    public EntityManager getEntityManager() { return entityManager; }

    public ShapeDrawer getShapeDrawer() { return shapeDrawer; }

    public void addSystem(ComponentSystem system) {
        systems.add(system);
        system.onCreate(null);
        system.awake();
        system.start();
    }  // TODO enable/disable ? TODO: activate, awake etc

    // TODO: consider remove start and stop (self management unnecessary)
    public synchronized void start() {
        if (runner != null) return;
        runner = new Thread(this::run);
        lastTimestamp = System.currentTimeMillis();
        runner.start();
    }

    public synchronized void stop() { runner = null; }

    private void run() { while (runner != null) tick(); }

    /***
     * Do a single tick of this space.
     * This may call "fixedUpdate" several times.
     * This will call "update" once.
     */
    public void tick() {
        long timestamp = System.currentTimeMillis();
        float dt = (timestamp - lastTimestamp) / 1000f;
        lastTimestamp = timestamp;

        input.iterate();

        accumulator += dt;
        while (accumulator >= 1/60f) {
            accumulator -= 1/60f;
            for (ComponentSystem system : systems) system.fixedUpdate(1/60f);
        }

        for (ComponentSystem system : systems) system.update(dt);
    }

    /**
     * Render the scene to/by the given context.
     * @param context - The context (details) to take into account when rendering the scene.
     */
    public void render(RenderContext context) {
        context.prepare();
        shapeDrawer.setProjectionMatrix(context.getCameraComponent().projectionMatrix);
        shapeDrawer.setViewMatrix(context.getCameraComponent().viewMatrix);

        shapeDrawer.begin();
        for (ComponentSystem system : systems) system.render(context);
        for (ComponentSystem system : systems) system.onDrawGizmos(context);
        shapeDrawer.end();
    }

    public InputSystem getInput() { return input; }

    // short-hands below (TODO: consider move)
    /** @return an object describing the current state of the keyboard(s) associated with this Space, and also any events that have been passed by it. */
    public KeyEventTracker getKeyboardTracker() { return input.getKeyboardPipeline().getTracker(); }

    @Override
    public void onCollisionBegin(Entity entity1, Entity entity2, Collision collision) {
        for (ComponentSystem system : systems) system.onCollisionBegin(entity1, entity2, collision);
    }

    @Override
    public void onCollisionEnd(Entity entity1, Entity entity2, Collision collision) {
        for (ComponentSystem system : systems) system.onCollisionEnd(entity1, entity2, collision);
    }
}
