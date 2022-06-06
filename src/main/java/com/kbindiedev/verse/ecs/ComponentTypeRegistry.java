package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.HECSScript;
import com.kbindiedev.verse.ecs.components.IComponent;
import com.kbindiedev.verse.math.logic.InfiniteBitmask;
import com.kbindiedev.verse.profiling.Assertions;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Defines a registry for component types that maps them into some index of an InfiniteBitmask.
 * This class is static, and so the registry applies globally to all consumers.
 *
 * As of this version of Verse, any element in the registry will not have its "pointing index"
 *      changed after registration.
 *
 * Types are divided into 2 groups: common and uncommon.
 * This is to group common types together more efficiently.
 *      Common types are stored starting at index 0, and increasing.
 *      Uncommon types are stored starting at index LONG.MAX_VALUE-1, and decreasing.
 *
 * @see com.kbindiedev.verse.math.logic.InfiniteBitmask
 */
public class ComponentTypeRegistry {

    // not "image" bitmap, but a map to bits.
    private static HashMap<Class<? extends IComponent>, Long> bitmap = new HashMap<>();
    private static long commonIndex = 0;
    private static long uncommonIndex = Long.MAX_VALUE - 1;

    /**
     * Get a bit-index for a given class.
     * If does not exist in the registry, then an index will be created for it.
     * If the class already exists in the registry, then its index is returned.
     * This index will never change.
     * @param clazz - The class to get an index for.
     * @return the bit-index for a given class, used to index into component-type structures about an InfiniteBitmask.
     */
    public static long getBitIndexForClass(Class<? extends IComponent> clazz) {
        if (!bitmap.containsKey(clazz)) createIndexForClass(clazz);
        return bitmap.get(clazz);
    }

    /**
     * Create an index for a class instance and store it in the registry.
     * Classes are divided into "common" and "uncommon" groups.
     *      The "common" indices are stored towards the LSB of the registry.
     *      The "uncommon" indices are stored towards the MSB of the registry.
     * As of now, all classes that extend HECSScript are considered "uncommon",
     *      and everything else is "common".
     * At most LONG.MAX_VALUE-1 indices may exist in the registry (about 9 quintillion).
     * @param clazz - The class to create an index for.
     */
    private static synchronized void createIndexForClass(Class<? extends IComponent> clazz) {

        if (commonIndex > uncommonIndex) {
            // TODO: assertions.fatal?
            Assertions.error("component-type-registry ran out of indices (this should not be possible!) " +
                    "(9 quintillion classes?). commonIndex: %d, uncommonIndex: %d", commonIndex, uncommonIndex);
            throw new IllegalStateException("exceeded number of indices (did you really store 9 quintillion classes?)");
        }

        boolean common = !HECSScript.class.isAssignableFrom(clazz);

        if (common) {
            bitmap.put(clazz, commonIndex++);
        } else {
            bitmap.put(clazz, uncommonIndex--);
        }

    }

    /**
     * Create an InfiniteBitmask from the given group according to the state of this registry.
     * The bitmask do not consider duplicate entries; if an entry exists, then its index is set to 1.
     * @param group - The group to create an InfiniteBitmask from. If null, then returned bitmask is empty.
     * @return a new InfiniteBitmask based off of the provided group.
     */
    public static InfiniteBitmask createBitmaskFromGroup(ComponentTypeGroup group) {
        InfiniteBitmask bitmask = new InfiniteBitmask();
        if (group == null) return bitmask;
        Iterator<Class<? extends IComponent>> iterator = group.iterator(); // TODO: change, replace iterator??
        while (iterator.hasNext()) bitmask.setBit(getBitIndexForClass(iterator.next()), true);
        return bitmask;
    }

}