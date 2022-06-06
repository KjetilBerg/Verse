package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.math.logic.InfiniteBitmask;

/**
 * Entity Query Description.
 *
 * Used to generate EntityQuery objects
 * @see EntityQuery
 */
public class EntityQueryDesc {

    private ComponentTypeGroup all;
    private ComponentTypeGroup any;
    private ComponentTypeGroup none;

    /**
     * Create an EntityQueryDescription with the given component states.
     * Any given state (all / any / none) will be skipped if they are empty.
     * @param all - All matching entities will have all these component types.
     * @param any - All matching entities will have at least one of these component types.
     * @param none - All matching entities will have none of these components.
     */
    public EntityQueryDesc(ComponentTypeGroup all, ComponentTypeGroup any, ComponentTypeGroup none) {
        this.all = all;
        this.any = any;
        this.none = none;
    }

    /**
     * Compile this description into an actual EntityQuery for a particular EntityManager.
     * @param manager - The manager to compile this query for.
     * @return a compiled EntityQuery that can be used to query for entities in the given EntityManager.
     * @see EntityManager
     */
    public EntityQuery compile(EntityManager manager) {
        InfiniteBitmask allMask = ComponentTypeRegistry.createBitmaskFromGroup(all);
        InfiniteBitmask anyMask = ComponentTypeRegistry.createBitmaskFromGroup(any);
        InfiniteBitmask noneMask = ComponentTypeRegistry.createBitmaskFromGroup(none);
        return new EntityQuery(manager, allMask, anyMask, noneMask);
    }

    // TODO: move to EntityManager
    // TODO: disabled
    /*
    public static EntityQueryDesc all(Class<? extends IComponent>[] all) {
        return new EntityQueryDesc(all, null, null);
    }

    public static EntityQueryDesc any(Class<? extends IComponent>[] any) {
        return new EntityQueryDesc(null, any, null);
    }

    public static EntityQueryDesc none(Class<? extends IComponent>[] none) {
        return new EntityQueryDesc(null, null, none);
    }
    */

}
