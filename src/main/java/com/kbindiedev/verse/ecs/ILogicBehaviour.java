package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.systems.ComponentSystem;
import com.kbindiedev.verse.system.SequenceRunner;

/**
 * Describes that any implementor of this interface has some behaviour associated to running.
 *
 * @see com.kbindiedev.verse.ecs.components.HECSScript // TODO: append to HECSScript
 * @see ComponentSystem
 */
public interface ILogicBehaviour {

    default void onCreate(SequenceRunner runner) {}

    default void awake() {}

    default void start() {}

    default void update() {}

    default void fixedUpdate() {}    // TODO: registration? how long is "fixed" ?

}
