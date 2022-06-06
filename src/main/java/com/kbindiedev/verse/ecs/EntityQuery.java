package com.kbindiedev.verse.ecs;


import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.math.logic.InfiniteBitmask;
import com.kbindiedev.verse.system.ImmutableHashMap;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: better caching
/**
 * Allows querying of entities in an ECS Scene.
 *
 * May be called any number of times and will always be up-to-date when called.
 *
 * @see Scene
 * @see Entity
 */
public class EntityQuery implements IEntityTargetsProvider {

    // standard in Collectors.toMap(...)
    private static final BinaryOperator<EntityGroup> TARGETS_MAP_MERGER = (u,v) -> { throw new IllegalStateException(String.format("Duplicate key: %s", u)); };

    private InfiniteBitmask includeAllBitmask;
    private InfiniteBitmask includeAnyBitmask;
    private InfiniteBitmask includeNoneBitmask;

    private IEntityTargetsProvider core;

    private long internalMappingVersion;
    private long internalContentVersion;
    private ImmutableHashMap<EntityArchetype, EntityGroup> cache; //TODO: docs. cache by version
    private EntityGroup contentCache;

    // TODO: build from desc

    /**
     * Create an EntityQuery based off of some IEntityTargetsProvider.
     * All entities to search through will be sourced from the results of the core provider.
     * @param core - The IEntityTargetsProvider to source my requests through.
     * @see IEntityTargetsProvider
     */
    public EntityQuery(IEntityTargetsProvider core, InfiniteBitmask all, InfiniteBitmask any, InfiniteBitmask none) { // TODO: javadoc and constructor. split into multiple query types??

        this.core = core;
        internalMappingVersion = core.getEntityTargetsMappingVersion() - 1;
        internalContentVersion = core.getEntityTargetsContentVersion() - 1; // ensure next .execute() causes update

        cache = null;
        contentCache = null;

        includeAllBitmask = all;
        includeAnyBitmask = any;
        includeNoneBitmask = none;

    }

    // TODO: docs
    public EntityGroup execute() {
        ensureContentCacheUpToDate();
        return contentCache;
    }

    @Override
    public ImmutableHashMap<EntityArchetype, EntityGroup> getEntityTargets() {
        ensureTargetsCacheUpToDate();
        return cache;
    }

    @Override
    public long getEntityTargetsMappingVersion() {
        long parentMappingVersion = core.getEntityTargetsMappingVersion();
        if (parentMappingVersion > internalMappingVersion) return parentMappingVersion;
        return internalMappingVersion;
    }

    @Override
    public long getEntityTargetsContentVersion() {
        long parentContentVersion = core.getEntityTargetsContentVersion();
        if (parentContentVersion > internalContentVersion) return parentContentVersion;
        return internalContentVersion;
    }

    /** Update the "targets cache". If the cache is "up to date" then nothing happens. Otherwise, the version number is also incremented. */
    private void ensureTargetsCacheUpToDate() {
        long desiredVersion = getEntityTargetsMappingVersion();
        if (desiredVersion <= internalMappingVersion) return;

        Stream<Map.Entry<EntityArchetype, EntityGroup>> stream = core.getEntityTargets().entrySet().stream()
                .filter(e -> e.getKey().getBitmask().isBitSupersetOf(includeAllBitmask))
                .filter(e -> e.getKey().getBitmask().isBitDisjointSet(includeNoneBitmask));

        if (!includeAnyBitmask.equals(InfiniteBitmask.EMPTY)) stream = stream.filter(e -> e.getKey().getBitmask().isBitOverlappingSet(includeAnyBitmask));

        HashMap<EntityArchetype, EntityGroup> result =
                stream.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, TARGETS_MAP_MERGER, HashMap::new));

        cache = new ImmutableHashMap<>(result);

        internalMappingVersion = desiredVersion;
    }

    /** Update the "content cache". If the cache is "up to date" then nothing happens. Otherwise, the version number is also incremented. */
    private void ensureContentCacheUpToDate() {
        long desiredVersion = getEntityTargetsContentVersion();
        if (desiredVersion <= internalContentVersion) return;

        contentCache = new EntityGroup(getEntityTargets().values());

        internalContentVersion = desiredVersion;
    }

}
