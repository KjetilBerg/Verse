package com.kbindiedev.verse.ecs;

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
public class Space {

    private EntityManager entityManager;
    private FastList<ComponentSystem> systems;
    private long lastTimestamp;
    private float accumulator;

    private Thread runner;

    public Space() { this(new EntityManager()); }
    public Space(EntityManager entityManager) { // TODO: remove manager ?
        this.entityManager = entityManager;
        systems = new FastList<>();
    }

    public EntityManager getEntityManager() { return entityManager; }

    public void addSystem(ComponentSystem system) {
        systems.add(system);
        system.onCreate(null);
        system.awake();
        system.start();
    }  // TODO enable/disable ? TODO: activate, awake etc

    public synchronized void start() {
        if (runner != null) return;
        runner = new Thread(this::run);
        lastTimestamp = System.currentTimeMillis();
        runner.start();
    }

    public synchronized void stop() { runner = null; }

    private void run() { while (runner != null) tick(); }

    private void tick() {
        long timestamp = System.currentTimeMillis();
        float dt = (timestamp - lastTimestamp) / 1000f;
        lastTimestamp = timestamp;

        accumulator += dt;
        while (accumulator >= 1/60f) {
            accumulator -= 1/60f;
            for (ComponentSystem system : systems) system.fixedUpdate(1/60f);
        }

        for (ComponentSystem system : systems) system.update(dt);
    }

}
