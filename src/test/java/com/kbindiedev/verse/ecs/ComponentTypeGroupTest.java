package com.kbindiedev.verse.ecs;

import com.kbindiedev.verse.ecs.components.IComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentTypeGroupTest {

    private ComponentTypeGroup group;

    @BeforeEach
    public void setup() {
        group = new ComponentTypeGroup();
    }

    @Test
    public void iteratorTest() {
        HashSet<Class<? extends IComponent>> set = new HashSet<>();

        group.addComponentType(Component1.class);
        set.add(Component1.class);
        assertTrue(iteratorMatchesSet(group.iterator(), set), "iterator should have 1 element in it");

        group.addComponentType(Component2.class);
        set.add(Component2.class);
        assertTrue(iteratorMatchesSet(group.iterator(), set), "iterator should have 2 elements in it");

        group.removeComponentType(Component3.class);
        assertTrue(iteratorMatchesSet(group.iterator(), set), "iterator should have 2 elements in it");

        group.removeComponentType(Component1.class);
        set.remove(Component1.class);
        assertTrue(iteratorMatchesSet(group.iterator(), set), "iterator should have 1 element in it");

        group.removeComponentType(Component2.class);
        group.addComponentType(Component1.class);
        set.remove(Component2.class);
        assertFalse(iteratorMatchesSet(group.iterator(), set), "iterator should not match current set");
    }

    @Test
    public void addComponentTypes() {
        assertTrue(group.addComponentType(Component1.class), "Component1 should be added successfully");
        assertTrue(group.addComponentType(Component2.class), "Component2 should be added successfully");
        assertTrue(group.addComponentType(Component3.class), "Component3 should be added successfully");
        assertFalse(group.addComponentType(Component1.class), "Component1 should not be added successfully the 2nd time");
        assertFalse(group.addComponentType(Component3.class), "Component3 should not be added successfully the 2nd time");
    }

    @Test
    public void removeEntities() {
        assertTrue(group.addComponentType(Component1.class), "Component1 should be added successfully");
        assertTrue(group.addComponentType(Component2.class), "Component2 should be added successfully");

        assertTrue(group.removeComponentType(Component1.class), "Component1 should be removed successfully");
        assertTrue(group.removeComponentType(Component2.class), "Component2 should be removed successfully");
        assertFalse(group.removeComponentType(Component3.class), "Component3 should not be removed successfully");

        assertFalse(group.removeComponentType(Component2.class), "Component2 should not be removed successfully the 2nd time");
    }

    @Test
    public void hasComponentTypes() {
        assertTrue(group.addComponentType(Component1.class), "Component1 should be added successfully");
        assertTrue(group.addComponentType(Component2.class), "Component2 should be added successfully");

        assertTrue(group.hasComponentType(Component1.class), "Component1 should exist in the set");
        assertTrue(group.hasComponentType(Component2.class), "Component2 should exist in the set");
        assertFalse(group.hasComponentType(Component3.class), "Component3 should not exist in the set");

        assertTrue(group.addComponentType(Component3.class), "Component3 should be added successfully");
        assertTrue(group.hasComponentType(Component3.class), "Component3 should exist in the set after being added");
    }

    @Test
    public void sizeComponentTypes() {
        assertTrue(group.addComponentType(Component1.class), "Component1 should be added successfully");
        assertTrue(group.addComponentType(Component2.class), "Component2 should be added successfully");
        assertEquals(2, group.size(), "group size should be 2");

        assertTrue(group.addComponentType(Component3.class), "Component3 should be added successfully");
        assertEquals(3, group.size(), "group size should be 3");

        assertTrue(group.removeComponentType(Component1.class), "Component1 should be removed successfully");
        assertEquals(2, group.size(), "group size should be 2");
    }

    @Test
    public void constructorTest() {
        group = new ComponentTypeGroup(Component1.class, Component3.class);

        assertTrue(group.hasComponentType(Component1.class), "Component1 should exist in the set");
        assertFalse(group.hasComponentType(Component2.class), "Component2 should not exist in the set");
        assertTrue(group.hasComponentType(Component3.class), "Component3 should exist in the set");

        assertTrue(group.addComponentType(Component2.class), "Component2 should be added successfully");
        assertTrue(group.hasComponentType(Component2.class), "Component2 should exist in the set after being added");
    }

    private static class Component1 implements IComponent {}
    private static class Component2 implements IComponent {}
    private static class Component3 implements IComponent {}

    private <T> boolean iteratorMatchesSet(Iterator<T> iterator, Set<T> set) {
        while (iterator.hasNext()) {
            if (!set.remove(iterator.next())) return false;
        }
        return set.size() == 0;
    }

}
