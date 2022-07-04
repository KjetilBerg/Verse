package com.kbindiedev.verse.ecs.event;

import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.physics.Collision;

/**
 * Listens to ECS collision events.
 *
 * All collision events are passed onto every {@link com.kbindiedev.verse.ecs.systems.ComponentSystem}.
 * For {@link com.kbindiedev.verse.ecs.components.HECSScript}, only collisions containing the entity that the script sits on will be called for.
 */
public interface ICollisionListener {

    void onCollisionBegin(Entity entity1, Entity entity2, Collision collision); // TODO: for now, direct Collision from physics system

    void onCollisionEnd(Entity entity1, Entity entity2, Collision collision);

}