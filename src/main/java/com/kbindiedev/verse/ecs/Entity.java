package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.ecs.components.Transform;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.system.FastList;
import com.kbindiedev.verse.util.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An entity is a "something" that exists in an ECS scene.
 * Entities consist of components that define their behavior (may also contain no components / be empty).
 *
 * Creation and deletion of entities MUST happen through an {@link EntityManager}.
 * The most common way to do this is with the use of {@link Scene}.
 *
 * An Entity may only be managed by a single EntityManager, though may be migrated.
 *
 * Entities are only equal when they share the same identity (.equals() is the same as ==).
 *      This is because any entity is only equal to another entity if they are the same literal
 *      entity (in other words, by the default implementation of double equals: ==).
 *
 * Component-driven programming is divided into two categories:
 *      Pure ECS - All components related to entities are data, and all functionality are driven by systems
 *                 that exist outside the direct scope of any given entity ({@link System}) //TODO: verify System
 *      Hybrid ECS - Components related to entities may include functionality (code that is not related to
 *                   representing data). All such components must extend {@link com.kbindiedev.verse.ecs.components.HECSScript}.
 */
public class Entity {

    private static Transform identityTransform = new Transform();   // TODO PRIORITY: make immutable

    private EntityManager manager;
    private EntityArchetype archetype;
    private HashMap<Class<? extends IComponent>, FastList<IComponent>> components; // TODO: replace Class<...> with ComponentType (?). ComponentGroup or FastList<IComponent>?

    /**
     * Package-private constructor. The creation of entities must happen in {@link EntityManager}. //TODO package private
     */
    Entity(EntityManager manager) {
        this.manager = manager;
        archetype = EntityArchetype.BLANK;
        components = new HashMap<>();
    }

    /** Package private getter */ //TODO: package private or public? if package-private, then entity does not need to store archetype
    public EntityArchetype getArchetype() { return archetype; }
    /** Package private setter */
    void setArchetype(EntityArchetype archetype) { this.archetype = archetype; }

    /**
     * Get this entity's transform.
     * If none exist, then the IdentityTransform is returned (position=0, scale=1, rotation=0).
     * @return this entity's transform, or the IdentityTransform.
     */
    public Transform getTransform() {
        Transform transform = getComponent(Transform.class);
        if (transform != null) return transform;
        return identityTransform;
    }

    /**
     * Add a component to this entity.
     * If a component already exists that matches this component, then nothing will happen and this method will return false.
     * Otherwise, the component will be added, this entity's archetype will immediately be changed and this method will return true.
     * @param component - The component to add.
     * @return whether or not a component was successfully added to this entity.
     */
    public boolean putComponent(IComponent component) {
        ArrayList<IComponent> list = new ArrayList<>(1);
        list.add(component);
        return putComponents(list);
    }

    /**
     * Add a list of components to this entity.
     * If a component already exists that matches any given component in the list, then that component will be skipped.
     * If any components were added, then this entity's archetype will immediately be changed.
     * If any components were added, then this method will return true, otherwise this method will return false.
     * @param list - The list of components to add.
     * @return whether or not any component was successfully added.
     */
    public boolean putComponents(List<IComponent> list) { // TODO: make accept arrays
        List<IComponent> added = new ArrayList<>(list.size());

        for (IComponent component : list) {
            if (hasExactComponent(component)) continue;

            //TODO: if HECSScript, set entity

            if (!components.containsKey(component.getClass())) components.put(component.getClass(), new FastList<>());

            components.get(component.getClass()).add(component);

            added.add(component);
        }

        // TODO
        if (added.size() > 0) manager.onEntityAddComponent(this, added);

        return added.size() > 0;
    }

    /**
     * Remove a component from this entity.
     * If no matching component is found on this entity, then nothing will happen and this method will return false.
     * Otherwise, the first matching component found will be removed, this entity's archetype will immediately be changed
     *      and this method will return true.
     * @param component - The component to remove.
     * @return whether or not a component was successfully removed from this entity.
     */
    public boolean removeComponent(IComponent component) {
        ArrayList<IComponent> list = new ArrayList<>(1);
        list.add(component);
        return removeComponents(list);
    }

    /**
     * Remove a list of components from this entity.
     * If no matching component is found for any entry in the list on this entity, then that component will be skipped.
     * If any components were removed, then this entity's archetype will immediately be changed.
     * If any components were removed, then this method will return true, otherwise this method will return false.
     * @param list - The list of components to remove.
     * @return whether or not any component was successfully removed.
     */
    public boolean removeComponents(List<IComponent> list) {
        List<IComponent> removed = new ArrayList<>(list.size());

        for (IComponent component : list) {
            if (!hasExactComponent(component)) continue;

            boolean existed = components.get(component.getClass()).remove(component);
            if (!existed) Assertions.error("hasExactComponent said component existed, but did not when .remove was called. " +
                    "component class: %s, component: %s", component.getClass().getCanonicalName(), component);

            removed.add(component);
        }

        // TODO
        if (removed.size() > 0) manager.onEntityRemoveComonent(this, removed);

        return removed.size() > 0;
    }

    /**
     * Check whether or not a given component exists on this entity by equality / identity.
     *      Two components are only equal if they share the same identity.
     * If you want to check if a component exists by class then use {@code {@link #getComponent(Class)} != null}.
     * @param component - The component to check for.
     * @return whether or not the given component exists on this entity (by equality / identity).
     */
    public boolean hasExactComponent(IComponent component) {
        FastList<IComponent> relevant = components.get(component.getClass());
        if (relevant == null) return false;
        return relevant.contains(component);
    }

    // TODO: Class<? extends IComponent> messy, but ComponentTypeGroup too long. consider something better
    /**
     * Check whether or not all the given component types exist on this entity.
     * @param classes - The component types.
     * @return true if all component types exist on this entity, false otherwise.
     */
    public boolean hasComponents(Class<? extends IComponent>... classes) {
        for (Class<? extends IComponent> c : classes) {
            if (getComponent(c) == null) return false;
        }
        return true;
    }

    //TODO: package-private or remove?
    /**
     * Get this entity's current bound manager.
     * @return the manager this entity is currently being managed by.
     */
    EntityManager getEntityManager() {
        return manager;
    }

    /**
     * Get a component of the provided class type that exists on this entity, or null if none such components exist.
     * If multiple components of the provided class exists on this entity, then one of them will be picked
     *      by order of registration.
     * @param clazz - The class of the component to look for.
     * @param <T> - The component type.
     * @return The first component that exists on this entity by the provided class, or null if none exist.
     */
    public @Nullable <T extends IComponent> T getComponent(Class<T> clazz) {
        List<T> list = getComponents(clazz);
        if (list == null) return null;
        return list.get(0);
    }

    /**
     * Get a list of components of the provided class type that exists on this entity, or null if none such components exist.
     * The order of the components will be the same as their registration-order.
     * The returned list will never be empty (but may be null).
     * @param clazz - The class of the component to look for.
     * @param <T> - The component type.
     * @return The list of components that exist on this entity by the provided class, or null if none exist.
     */
    public @Nullable <T extends IComponent> List<T> getComponents(Class<T> clazz) {

        if (!components.containsKey(clazz)) return null;

        @SuppressWarnings("unchecked")
        List<T> comps = (List<T>)components.get(clazz).asList();

        return comps;
    }

    /**
     * Get a list of all components on this entity, or null if none exist.
     * The order of the components will be the same as their registration-order.
     * The returned list will never be empty (but may ne null).
     * @return a list of all components on this entity, ordered by registration-order, or null if no components exist on this entity.
     */
    public @Nullable List<IComponent> getAllComponents() {
        if (components.size() == 0) return null;
        List<IComponent> comps = new ArrayList<>();
        for (FastList<IComponent> list : components.values()) comps.addAll(list);
        return comps;
    }

    @Override
    public String toString() {
        return String.format("{ hashCode: %d, manager: %s, archetype: %s, components: %s }", hashCode(), manager.toString(), archetype.toString(), components.toString());
    }

}