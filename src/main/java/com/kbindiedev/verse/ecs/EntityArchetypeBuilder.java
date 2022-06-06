package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.system.ImmutableHashMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to create EntityArchetypes.
 * @see EntityArchetype
 */
public class EntityArchetypeBuilder {

    private HashMap<Class<? extends IComponent>, Integer> componentTypes;    // types to count

    public EntityArchetypeBuilder() {
        componentTypes = new HashMap<>();
    }

    /**
     * Create a new EntityArchetypeBuilder by assuming a template (use an archetype as a template).
     *      All classes that exist on the given archetype (including their counts) will be copied into this archetype builder.
     * @param archetype - The archetype to base myself off of.
     * @return an EntityArchetypeBuilder that starts with the classes and counts from the given archetype.
     */
    public static EntityArchetypeBuilder fromTemplate(EntityArchetype archetype) {
        EntityArchetypeBuilder builder = new EntityArchetypeBuilder();

        for (Map.Entry<Class<? extends IComponent>, Integer> entry : archetype.getComponentTypes().entrySet()) {
            for (int count = 0; count < entry.getValue(); ++count) builder.addComponentType(entry.getKey());
        }

        return builder;
    }

    /**
     * Add a component type to this builder.
     * @param clazz - The class to add as a type.
     * @return self, for chaining calls.
     */
    public EntityArchetypeBuilder addComponentType(Class<? extends IComponent> clazz) {
        int count = componentTypes.getOrDefault(clazz, 0);
        componentTypes.put(clazz, count + 1);
        return this;
    }

    /**
     * Add a list of component types to this builder.
     * @param classes - The collection of classes to add as types.
     * @return self, for chaining calls.
     */
    public EntityArchetypeBuilder addComponentTypes(Collection<Class<? extends IComponent>> classes) {
        for (Class<? extends  IComponent> clazz : classes) addComponentType(clazz);
        return this;
    }

    /**
     * Remove a component type from this builder.
     * If the component type does not exist on the builder, then it is ignored.
     * @param clazz - The class to remove as a type.
     * @return self, for chaining calls.
     */
    public EntityArchetypeBuilder removeComponent(Class<? extends IComponent> clazz) {
        int count = componentTypes.getOrDefault(clazz, 1) - 1;
        if (count == 0) componentTypes.remove(clazz); else componentTypes.put(clazz, count);
        return this;
    }

    /**
     * Remove a list of component types from this builder.
     * If any component type does not exist on the builder, then it is skipped.
     * @param classes - The collection of classes to remove as types.
     * @return self, for chaining calls.
     */
    public EntityArchetypeBuilder removeComponents(Collection<Class<? extends IComponent>> classes) {
        for (Class<? extends IComponent> clazz : classes) removeComponent(clazz);
        return this;
    }

    /**
     * Get an EntityArchetype that is the identity of the archetype that would be created by
     *      the current state of this builder.
     * The "identity" of an archetype is defined as the first constructed archetype that matches this
     *      new archetype by equality (.equals()). If no "identity" exists, then it will be created.
     * You may change the state of the builder and execute this method again several times
     *      without affecting the returned archetype.
     * @return the identifying archetype by the current state of this builder.
     * @see EntityArchetype#identity()
     */
    public EntityArchetype toIdentity() {
        HashMap<Class<? extends IComponent>, Integer> duplicateComponents = new HashMap<>(componentTypes);

        EntityArchetype built = new EntityArchetype(new ImmutableHashMap<>(duplicateComponents));

        return built.identity();
    }

}
