package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.system.ImmutableHashMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A manager for a set of entities.
 * Said entities are stored into chunks by their archetype.
 *
 * Because this is java (and precise memory management is not possible), no fancy memory allocation is done
 *      to store entities. They are still categorized into archetypes which make queries faster, though.
 *
 * @see EntityArchetype
 * @see Scene
 * @see Entity
 */
public class EntityManager implements IEntityTargetsProvider {

    private HashMap<EntityArchetype, EntityGroup> entities;                         // TODO: rename, ArchetypeChunk or something
    private ImmutableHashMap<EntityArchetype, EntityGroup> immutableEntities;
    private long entitiesMappingVersion;
    private long entitiesContentVersion;

    protected EntityManager() {
        entities = new HashMap<>();
        immutableEntities = new ImmutableHashMap<>(entities);
        entitiesMappingVersion = 0;
        entitiesContentVersion = 0;
    }

    /**
     * Create a new entity with no components.
     * The newly created entity will be added to this manager.
     * @return the entity that was created and added to this manager.
     */
    public Entity instantiate() {
        Entity entity = new Entity(this); // TODO: messy (?) make clear that entity does not add itself, but does changearchetype etc (change that??)
        addEntity(entity);
        return entity;
    } // TODO: how entities attach themselves onto manager (other .instantiate() depend on this, so maybe delay attachment?)

    /**
     * Create a new entity with the given components (the components are constructed using their empty constructor).
     * @param components - A list of classes that the new entity should have as components.
     *                     These will be constructed using their empty constructor (which must be available
     *                     by contract of IComponent).
     * @return the entity that was created and added to this manager.
     */
    public Entity instantiate(Class<? extends IComponent>... components) {
        return instantiate(null, instantiateComponents(components));
    }

    /**
     * Create a new entity with the given components (the components are constructed using their empty constructor).
     * The newly created entity will also be added as a child to the provided parent.
     * @param parent - The new entity's parent.
     * @param components - A list of classes that the new entity should have as components.
     *                     These will be constructed using their empty constructor (which must be available
     *                     by contract of IComponent).
     * @return the entity that was created and added to this manager.
     */
    public Entity instantiate(Entity parent, Class<? extends IComponent>... components) {
        return instantiate(parent, instantiateComponents(components));
    }

    /**
     * Create a new entity with the given components (exact data).
     * @param components - A list of components that the new entity should use.
     * @return the entity that was created and added to this manager.
     */
    public Entity instantiate(IComponent... components) {
        return instantiate(null, components);
    }

    /**
     * Create a new entity with the given components (exact data).
     * The newly created entity will also be added as a child to the provided parent.
     * @param components - A list of components that the new entity should use.
     * @return the entity that was created and added to this manager.
     */
    public Entity instantiate(Entity parent, IComponent... components) {
        if (parent != null) throw new NotImplementedException(); // TODO: impl
        Entity entity = instantiate();
        entity.putComponents(Arrays.asList(components)); //TODO: inefficient
        return entity;
    }

    /**
     * Create a new entity from a provided prefab (with exact data).
     * @param prefab - The prefab to instantiate.
     * @return the entity that was created and added to this manager.
     */
    public Entity instantiate(Prefab prefab) {
        return instantiate(null, prefab);
    }

    /**
     * Create a new entity from a provided prefab (with exact data)
     * The newly created entity will also be added as a child to the provided parent.
     * @param parent - The new entity's parent.
     * @param prefab - The prefab to instantiate.
     * @return the entity that was created and added to this manager.
     */
    public Entity instantiate(Entity parent, Prefab prefab) {
        // TODO impl
        Assertions.warn("instantiate prefab is not implemented");
        throw new UnsupportedOperationException("instantiate prefab not implemented");
    }

    /**
     * Destroy an entity.
     * Any children that belong to the given entity will also be destroyed.
     * If the entity does not exist on this manager, then nothing happens and this method returns false.
     * @param entity - The entity to remove.
     * @return true if the entity was removed, or false if it did not exist on this manager.
     */
    public boolean destroy(Entity entity) {
        return removeEntity(entity); // TODO: remove removeEntity ??
    }

    /**
     * Instantiate all classes of components into actualized components.
     * The components are constructed using their respective empty constructors.
     * @throws RuntimeException - An exception with a nested {@link InstantiationException}
     *      or {@link IllegalAccessException} if the component could not be created.
     *      Note that before the exception is thrown, an assertion is thrown at the error-level.
     * @param classes - The classes to create components from.
     * @return an array of newly created components (based by provided classes).
     */
    private IComponent[] instantiateComponents(Class<? extends IComponent>... classes) {
        IComponent[] components = new IComponent[classes.length];
        try {
            for (int i = 0; i < classes.length; ++i) components[i] = classes[i].newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Assertions.error("unable to instantiate component: %s", e);
            throw new RuntimeException(e);
        }
        return components;
    }

    /**
     * Check if an entity exists on this EntityManager.
     * @param entity - The entity to check for.
     * @return whether or the entity exists on this manager.
     */
    public boolean hasEntity(Entity entity) {
        EntityArchetype archetype = entity.getArchetype();
        if (!entities.containsKey(archetype)) return false;
        return entities.get(archetype).hasEntity(entity);
    }


    /**
     * Add an entity to this EntityManager.
     * If the provided entity already exists, then nothing happens and this method returns false.
     * Otherwise the provided entity will be added and this method returns true.
     * If the entity's archetype is not currently being managed by this manager, then it will be.
     * @param entity - The entity to add.
     * @return whether or not the given entity was successfully added to this manager.
     */
    private boolean addEntity(Entity entity) {
        EntityArchetype archetype = entity.getArchetype();
        if (!entities.containsKey(archetype)) {
            entities.put(archetype, new EntityGroup());
            entitiesMappingVersion++;
        }
        entitiesContentVersion++;
        return entities.get(archetype).addEntity(entity);
    }

    /**
     * Remove an entity from this EntityManager.
     * If the provided entity is not found, then nothing happens and this method returns false.
     * Otherwise the provided entity is removed and this method returns true.
     * If the entity was successfully removed and it was the last entry in its archetype in this manager,
     *      then that archetype will no longer be managed (will become re-managed upon adding
     *      a new entity with that archetype).
     * @param entity - The entity to remove.
     * @return whether or not the given entity was successfully removed from this EntityManager.
     */
    private boolean removeEntity(Entity entity) {
        EntityArchetype archetype = entity.getArchetype();
        if (!entities.containsKey(archetype)) return false;

        EntityGroup set = entities.get(archetype);

        boolean existed = set.removeEntity(entity);

        if (existed) entitiesContentVersion++;

        if (set.size() == 0) {
            entities.remove(archetype); // TODO not removing = no need to update mapping version. note: irrelevant? content version updated regardless.
            entitiesMappingVersion++;
        }

        return existed;
    }

    // TODO: rework method
    /**
     * Change the archetype of an entity in this scene.
     * Note that the provided archetype is not checked to be correct for the new state of the given entity.
     * If the entity is not found in this archetype manager, then nothing happens and this method returns false.
     * Otherwise, the entity has its archetype changed and this method returns true.
     * If the archetype is changed, then future calls to {@link Entity#getArchetype()} will reflect this.
     * If the archetype is changed, then future calls to {@link #query()} will reflect this. //TODO: query()
     * This method is intentionally package-private.
     * @param entity - The entity to change the archetype for.
     * @param to - The archetype that the entity should end up with.
     * @return whether or not the archetype was successfully changed.
     */
    private boolean changeArchetype(Entity entity, EntityArchetype to) {
        EntityArchetype current = entity.getArchetype();
        if (current == to) Assertions.warn("entity wants to change its archetype to itself. entity: %s", entity.toString());

        // remove, with assertions
        if (!hasEntity(entity)) {
            Assertions.warn("entity does not exist in EntityManager. Archetype: %s, Entity: %s", current.toString(), entity.toString());
            return false;
        }

        removeEntity(entity);
        entity.setArchetype(to);
        addEntity(entity);

        return true;
    }

    /**
     * Internal method. Called by {@link Entity#putComponents(List)}.
     * This will adjust the entity's archetype in this manager.
     * @param entity - The entity that had components added.
     * @param added - The components that were added.
     */
    protected void onEntityAddComponent(Entity entity, List<IComponent> added) {
        List<Class<? extends IComponent>> addedClasses = added.stream().map(IComponent::getClass).collect(Collectors.toList());
        EntityArchetype newArchetype = EntityArchetypeBuilder.fromTemplate(entity.getArchetype()).addComponentTypes(addedClasses).toIdentity();
        changeArchetype(entity, newArchetype);
    }

    /**
     * Internal method. Called by {@link Entity#removeComponents(List)}.
     * This will adjust the entity's archetype in this manager.
     * @param entity - The entity that had components removed.
     * @param removed - The components that were removed.
     */
    protected void onEntityRemoveComonent(Entity entity, List<IComponent> removed) {
        List<Class<? extends IComponent>> removedClasses = removed.stream().map(IComponent::getClass).collect(Collectors.toList());
        EntityArchetype newArchetype = EntityArchetypeBuilder.fromTemplate(entity.getArchetype()).removeComponents(removedClasses).toIdentity();
        changeArchetype(entity, newArchetype);
    }

    @Override
    public ImmutableHashMap<EntityArchetype, EntityGroup> getEntityTargets() { return immutableEntities; }

    @Override
    public long getEntityTargetsMappingVersion() { return entitiesMappingVersion; }

    @Override
    public long getEntityTargetsContentVersion() { return entitiesContentVersion; }
}