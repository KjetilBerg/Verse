package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.system.ImmutableHashMap;

/**
 * Provide a base to fetch a list of EntityArchetype -> Set<Entity> mappings.
 * It is also easy to cache these values by "versioning".
 *
 * @see EntityQuery
 * @see EntityManager   //TODO: manager
 */
public interface IEntityTargetsProvider {

    /**
     * Get the "targets" of this implementor.
     * The targets are defined as a mapping from EntityArchetype to a EntityGroup of entities belonging to those archetypes.
     * TargetProviders are decided to have a versioning system for their "targets map" (the map that should be returned from this method).
     * Any internal changes to any EntityGroup in the map must be reflected in maps that have been returned from this method.
     *      Returned maps that are "out of date" do not need to be reflected / meet this requirement.
     * @return the "targets" of this implementor.
     * @see #getEntityTargetsMappingVersion()
     * @see #getEntityTargetsContentVersion()
     */
    ImmutableHashMap<EntityArchetype, EntityGroup> getEntityTargets();

    /**
     * Get the "target map"'s current mapping version number.
     * The "target map" is the map you would get if you called {@link #getEntityTargets()}.
     *
     * This version represents the state of the "target map"'s KeySet and ValueSet.
     * The version number increments whenever the "target map" changes KeySet (changes a key) or
     *      replaces a value's pointer (EntityGroup).
     *
     * It is illegal for the version number to go backwards.
     *      If the new map state happens to equal a previous state, then the version number must still increase.
     *
     * This versioning is not affected by the contents of the map changing (EntityGroup).
     *      For that, use {@link #getEntityTargetsContentVersion()}.
     * @return the current version number of the target mappings.
     * @see #getEntityTargets()
     */
    long getEntityTargetsMappingVersion();

    /**
     * Get the "target map"'s current content version number.
     * The "target map" is the map you would get if you called {@link #getEntityTargets()}.
     *
     * The version represents the state of the contents of the "target map"'s ValueSet (EntityGroup).
     * This version number increments whenever the contents of any EntityGroup in the "target map" changes.
     *
     * It is illegal for the version number to go backwards.
     *      If the new content state happens to equal a previous state, then the version number must still increase.
     *
     * Whenever a mapping changes, then its content is also deemed to be "changed".
     *      This means that the version number for "content" is always >= than that of {@link #getEntityTargetsMappingVersion()}.
     *
     * If you only desire to get the version of the mappings, use {@link #getEntityTargetsMappingVersion()}.
     * @return the current version number of the "content" in all mappings.
     * @see #getEntityTargets()
     */
    long getEntityTargetsContentVersion();

}