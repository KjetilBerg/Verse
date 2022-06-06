package com.kbindiedev.verse.ecs.components;

import com.kbindiedev.verse.ecs.Entity;
import com.kbindiedev.verse.profiling.Assertions;

/**
 * Hybrid Entity Component Script. All entities implementing some functionality that is not system-driven,
 *      MUST extend this class.
 */
public abstract class HECSScript implements IComponent {

    private Entity entity;
    //TODO: start, update, fixedUpdate, render(?) etc

    public HECSScript() {
        entity = null;
    }

    //TODO: .instantiate MUST setEntity somehow. should HECSScript exist outside components? setEntity should be package-private. reflection??
    void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * Get this component/script's parent entity entity.
     * @return this component/script's parent entity.
     */
    public Entity getEntity() {
        if (entity == null) Assertions.error("entity missing for HECSScript");
        return entity;
    }

}
