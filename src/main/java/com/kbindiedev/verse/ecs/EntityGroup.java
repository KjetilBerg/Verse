package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.system.FastList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

// TODO: replace everything with "extends Group<T>" (that also "extends HashSet<T>").
/**
 * Describes a group of entities.
 * Duplicates may not exist.
 *
 * @see Entity
 * @see EntityArchetype //TODO: see ArchetypeChunk ?? TODO: change HashSet to FastList ?
 */
public class EntityGroup { // TODO: implement collection? same as ComponentTypeGroup

    private FastList<Entity> group;

    /** Create a new blank EntityGroup */
    public EntityGroup() { this(new ArrayList<>()); }

    /**
     * Create an EntityGroup that is based off other EntityGroup(s).
     * @param groups - Other groups to add to this group. Changes to these groups do not affect this group.
     */
    public EntityGroup(Collection<EntityGroup> groups) {
        group = new FastList<>();
        for (EntityGroup g : groups) group.addAll(g.group);
    }

    /** @return an iterator for all entries in this group. */
    public Iterator<Entity> iterator() { return group.iterator(); }

    /**
     * Add an entity to this group.
     * If the entity already exists in this group, then nothing happens.
     * @param entity - The entity to be added.
     * @return whether or not the entity did not previously exist in this group (added = true, existed = false).
     */
    public boolean addEntity(Entity entity) {
        return group.add(entity);
    }

    /**
     * Remove an entity from this group.
     * If the entity does not exist in this group, then nothing happens.
     * @param entity - The entity to be removed.
     * @return whether or not the entity existed and was removed (removed = true, did not exist = false).
     */
    public boolean removeEntity(Entity entity) {
        return group.remove(entity);
    }

    /**
     * Check if an entity exists in this group.
     * @param entity - The entity to check for.
     * @return whether or not the given entity exists in this group.
     */
    public boolean hasEntity(Entity entity) {
        return group.contains(entity);
    }

    /**
     * Get the number of entities in this group.
     * @return the number of entities currently stored in this group.
     */
    public int size() {
        return group.size();
    }

}