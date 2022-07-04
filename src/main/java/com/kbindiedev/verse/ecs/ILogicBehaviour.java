package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.event.ICollisionListener;
import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.physics.Collision;
import com.kbindiedev.verse.system.SequenceRunner;

/**
 * Describes that any implementor of this interface has some behaviour associated to running.
 *
 * @see com.kbindiedev.verse.ecs.components.HECSScript // TODO: append to HECSScript
 * @see ComponentSystem
 */
public interface ILogicBehaviour extends ICollisionListener {

    default void onCreate(SequenceRunner runner) {}

    default void awake() {}

    default void start() {}

    default void update(float dt) {}

    default void fixedUpdate(float dt) {}    // TODO: registration? how long is "fixed" ?

    default void render(RenderContext context) {}

    default void onDrawGizmos(RenderContext context) {}

    default void onDrawGizmosSelected(RenderContext context, Entity entity) {}

    default void onCollisionBegin(Entity entity1, Entity entity2, Collision collision) {}

    default void onCollisionEnd(Entity entity1, Entity entity2, Collision collision) {}

}