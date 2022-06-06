package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.IComponent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A group of component types.
 * Duplicates cannot exist.
 */
public class ComponentTypeGroup {       // TODO: implement Collection? if so: change tests and ComponentTypeRegistry

    private HashSet<Class<? extends IComponent>> group;

    public ComponentTypeGroup() {
        group = new HashSet<>();
    }

    public ComponentTypeGroup(Class<? extends IComponent>... classes) {
        group = new HashSet<>(Arrays.asList(classes));
    }

    /** @return an iterator for all entries in this group. */
    public Iterator<Class<? extends IComponent>> iterator() { return group.iterator(); }

    /**
     * Add a component type to this group.
     * If the component type already existed in this group, then nothing happens and the method returns false.
     * Otherwise the component type is added and this method returns true.
     * @param clazz - The component type to add.
     * @return whether or not the component type was added to this group (true = added, false = already exists).
     */
    public boolean addComponentType(Class<? extends IComponent> clazz) {
        return group.add(clazz);
    }

    /**
     * Remove a component type from this group.
     * If the component type does not exist in this group, then nothing happens.
     * @param clazz - The component type to be removed.
     * @return whether or not the component type existed and was removed (removed = true, did not exist = false).
     */
    public boolean removeComponentType(Class<? extends IComponent> clazz) {
        return group.remove(clazz);
    }

    /**
     * Check if this group contains a certain component type.
     * @param clazz - The component type to check for.
     * @return whether or not the given component type exists on this group.
     */
    public boolean hasComponentType(Class<? extends IComponent> clazz) {
        return group.contains(clazz);
    }

    /**
     * Get the number of component types in this group.
     * @return the number of component types currently stored in this group.
     */
    public int size() {
        return group.size();
    }

}