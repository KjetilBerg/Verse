package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.math.logic.InfiniteBitmask;
import com.kbindiedev.verse.system.IdentityRegistry;
import com.kbindiedev.verse.system.ImmutableHashMap;
import com.kbindiedev.verse.util.NotNull;

/**
 * An entity's "Archetype" is defined by the list of types of components it is made up of (including count).
 *      The order of component types is irrelevant.
 * This class tracks a single archetype, and does not describe any data for its relevant component types.
 * Internally in Verse, all entities are grouped and stored by their "Archetype".
 *
 * You can create EntityArchetypes using an EntityArchetypeBuilder.
 *
 * @see EntityArchetypeBuilder
 */
public class EntityArchetype {

    // package-private constant
    static final @NotNull EntityArchetype BLANK;

    private static final IdentityRegistry<EntityArchetype> archetypeIdentities = new IdentityRegistry<>();

    static {
        EntityArchetypeBuilder builder = new EntityArchetypeBuilder();

        BLANK = builder.toIdentity();
    }

    private ImmutableHashMap<Class<? extends IComponent>, Integer> componentTypes;    // components to count;
    private InfiniteBitmask bitmask;

    // optimization, see hashCode
    private int cachedHashcode;
    private boolean hashcodeDirty;

    // TODO: private constructor with some .getOrMake() ??
    public EntityArchetype(ImmutableHashMap<Class<? extends IComponent>, Integer> componentTypes) {
        this.componentTypes = componentTypes;

        bitmask = new InfiniteBitmask();
        for (Class<? extends IComponent> clazz : componentTypes.keySet())
            bitmask.setBit(ComponentTypeRegistry.getBitIndexForClass(clazz), true); // TODO: use bitmask builder from ComponentTypeRegistry

        cachedHashcode = 0;
        hashcodeDirty = true;

        // register identity if none exist
        archetypeIdentities.getIdentityOf(this); // TODO: rename method .registerIfNoneExist or something
    }

    /**
     * Get the "identity" of this archetype.
     * The "identity" for any given archetype is defined as the archetype that matches it by equality (.equals())
     *      and that was constructed first.
     * This is used to have all archetypes that are equal reference the same archetype on the heap memory.
     *      Storing component-data internally in Verse is not affected by duplicate archetypes existing. This is for other memory-optimization.
     * @return the archetype instance that was first constructed that is also matching the provided archetype.
     */
    public EntityArchetype identity() { return archetypeIdentities.getIdentityOf(this); }

    /** @return this archetype's component types (maps classes to count, count will always be >= 1). */
    public ImmutableHashMap<Class<? extends IComponent>, Integer> getComponentTypes() { return componentTypes; }

    /**
     * Get this archetype's bitmask.
     * Package-private, used by entity queries.
     * The bitmask must not be edited.
     * @return this archetype's bitmask.
     * @see EntityQuery TODO: entityquery
     */
    InfiniteBitmask getBitmask() { return bitmask; }

    @Override
    public int hashCode() {
        if (hashcodeDirty) { cachedHashcode = componentTypes.hashCode(); hashcodeDirty = false; }
        return cachedHashcode;
    }

    /** EntityArchetypes are considered equal if their component types are equal (with count). */
    @Override
    public boolean equals(Object o) {
        if (!EntityArchetype.class.equals(o.getClass())) return false;

        EntityArchetype other = (EntityArchetype)o;

        return componentTypes.equals(other.componentTypes);
    }

    @Override
    public String toString() {
        return componentTypes.toString();
    }

}
