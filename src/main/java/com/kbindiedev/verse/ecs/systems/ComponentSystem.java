package com.kbindiedev.verse.ecs.systems;

import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.ecs.ILogicBehaviour;
import com.kbindiedev.verse.ecs.RenderContext;
import com.kbindiedev.verse.ecs.Space;
import com.kbindiedev.verse.system.SequenceRunner;

/**
 * The "system" part of the Entity Component System.
 *
 * Systems are registered onto {@link Space} and are run by them.
 */
public abstract class ComponentSystem implements ILogicBehaviour {

    private Space space;

    public ComponentSystem(Space space) { this.space = space; }

    public Space getSpace() { return space; }

    public void onCreate(SequenceRunner runner) {}

    public void awake() {}

    public void start() {}

    public void update(float dt) {}

    public void fixedUpdate(float dt) {}    // TODO: registration? how long is "fixed" ?

    public void render(RenderContext context) {}

    public void onDrawGizmos(RenderContext context) {}

    public void onDrawGizmosSelected(RenderContext context, Entity entity) {}

    // TODO: some registration method. systems may "run once", be "self managed" or "run on update"
    // TODO: with respect to awake/start/run, .dependsOn() should maybe exist

}