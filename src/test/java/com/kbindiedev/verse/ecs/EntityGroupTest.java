package com.kbindiedev.verse.ecs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// TODO: (new requirement) test maintains order of entities

public class EntityGroupTest {

    private EntityGroup group;

    private Entity e1;
    private Entity e2;
    private Entity e3;

    @BeforeEach
    public void setup() {
        group = new EntityGroup();

        e1 = new Entity(null);
        e2 = new Entity(null);
        e3 = new Entity(null);
    }

    @Test
    public void iteratorTest() {
        HashSet<Entity> set = new HashSet<>();

        group.addEntity(e1);
        set.add(e1);
        assertTrue(iteratorMatchesSet(group.iterator(), set), "iterator should have 1 element in it");

        group.addEntity(e2);
        set.add(e2);
        assertTrue(iteratorMatchesSet(group.iterator(), set), "iterator should have 2 elements in it");

        group.removeEntity(e3);
        assertTrue(iteratorMatchesSet(group.iterator(), set), "iterator should have 2 elements in it");

        group.removeEntity(e1);
        set.remove(e1);
        assertTrue(iteratorMatchesSet(group.iterator(), set), "iterator should have 1 element in it");

        group.removeEntity(e2);
        group.addEntity(e1);
        set.remove(e2);
        assertFalse(iteratorMatchesSet(group.iterator(), set), "iterator should not match current set");
    }

    @Test
    public void addEntities() {
        assertTrue(group.addEntity(e1), "e1 should be added successfully");
        assertTrue(group.addEntity(e2), "e2 should be added successfully");
        assertTrue(group.addEntity(e3), "e3 should be added successfully");
        assertFalse(group.addEntity(e1), "e1 should not be added successfully the 2nd time");
        assertFalse(group.addEntity(e3), "e3 should not be added successfully the 2nd time");
    }

    @Test
    public void removeEntities() {
        assertTrue(group.addEntity(e1), "e1 should be added successfully");
        assertTrue(group.addEntity(e2), "e2 should be added successfully");

        assertTrue(group.removeEntity(e1), "e1 should be removed successfully");
        assertTrue(group.removeEntity(e2), "e2 should be removed successfully");
        assertFalse(group.removeEntity(e3), "e3 should not be removed successfully");

        assertFalse(group.removeEntity(e2), "e2 should not be removed successfully the 2nd time");
    }

    @Test
    public void existsEntities() {
        assertTrue(group.addEntity(e1), "e1 should be added successfully");
        assertTrue(group.addEntity(e2), "e2 should be added successfully");

        assertTrue(group.hasEntity(e1), "e1 should exist in the group");
        assertTrue(group.hasEntity(e2), "e2 should exist in the group");
        assertFalse(group.hasEntity(e3), "e3 should not exist in the group");

        assertTrue(group.removeEntity(e2), "e2 should be removed successfully");

        assertTrue(group.hasEntity(e1), "e1 should exist in the group");
        assertFalse(group.hasEntity(e2), "e2 should not exist in the group after being removed");
    }

    @Test
    public void sizeEntities() {
        assertTrue(group.addEntity(e1), "e1 should be added successfully");
        assertTrue(group.addEntity(e2), "e2 should be added successfully");
        assertEquals(2, group.size(), "group size should be 2");

        assertTrue(group.addEntity(e3), "e3 should be added successfully");
        assertEquals(3, group.size(), "group size should be 3");

        assertTrue(group.removeEntity(e2), "e2 should be removed successfully");
        assertEquals(2, group.size(), "group size should be 2");
    }

    private <T> boolean iteratorMatchesSet(Iterator<T> iterator, Set<T> set) {
        while (iterator.hasNext()) {
            if (!set.remove(iterator.next())) return false;
        }
        return set.size() == 0;
    }

}