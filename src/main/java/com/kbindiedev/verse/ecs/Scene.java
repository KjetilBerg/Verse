package com.kbindiedev.verse.ecs;

// TODO OUTDATED: Scenes represent data that load into Spaces

//TODO: check javadocs
/**
 * The root of an ECS instance. This keeps track of all loaded entities, and is responsible for
 *      creating, deleting, moving and searching through these entities. Note that the entities themselves
 *      responsible for tracking their children, yet Scene is still responsible for adding and/or removing
 *      those children.
 *
 * Note: there intentionally does not exist any .duplicate method for entities, as it is believed to cause bugs.
 *      If you want to instantiate objects, then you should use pre-made prefabs.
 *      If you really wish to duplicate an entity that exists in a scene, then you should
 *          serialize the entity to a prefab and then instantiate that instead.
 */
public class Scene {

    private EntityManager entityManager;

    public Scene() {
        entityManager = new EntityManager();
    }

    // TODO: javadoc (also alternative with parent)

    // TODO: move most of these methods to the EntityManager (?)




    //TODO document. called by entity on add and remove component
    //TODO: should entity have archetype? (or should it be managed by some other system). in that case, .instantiate() should must set archetypes (?)

}
