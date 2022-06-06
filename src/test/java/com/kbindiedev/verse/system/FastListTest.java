package com.kbindiedev.verse.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FastListTest {

    private FastList<Integer> list;
    private List<Integer> source;
    private Random random;
    private int count;

    @BeforeEach
    public void setup() {
        list = new FastList<>();
        random = new Random();
        count = 20;
        source = new ArrayList<>();
        for (int i = 0; i < count; ++i) source.add(random.nextInt());
    }

    @Test
    public void testSize() {

        assertEquals(0, list.size(), "list size should be 0");

        list.add(source.get(0));
        assertEquals(1, list.size(), "list size should be 1");

        list.add(source.get(1));
        list.add(source.get(2));
        assertEquals(3, list.size(), "list size should be 3");

        list.remove(source.get(1));
        assertEquals(2, list.size(), "list size should be 2");

    }

    @Test
    public void testContains() {
        list.addAll(source);

        assertTrue(list.containsAll(source), "list should contain all from source");
        for (int i : source) assertTrue(list.contains(i), "list should contain every element from source");

    }

    @Test
    public void testIterator() {

        assertFalse(list.iterator().hasNext(), "empty list should have empty iterator");
        assertNull(list.iterator().next(), "empty list should have null-giving iterator");

        assertTrue(list.addAll(source), "elements should be added successfully");
        Iterator<Integer> it1 = list.iterator();
        Iterator<Integer> it2 = source.iterator();

        int num = 0;
        List<Integer> it1order = new ArrayList<>();
        while (it1.hasNext() && it2.hasNext()) {
            num++;
            int v = it1.next();
            assertEquals(it2.next(), v, "iterators should be equal");
            it1order.add(v);
        }

        assertFalse(it1.hasNext(), "it1 should be emptied");
        assertFalse(it2.hasNext(), "it2 should be emptied");
        assertEquals(count, num, "num should equal count");

        list.remove(source.get(0));
        int newSize = 0;
        Iterator<Integer> it3 = list.iterator();
        List<Integer> it3order = new ArrayList<>();
        while (it3.hasNext()) {
            newSize++;
            it3order.add(it3.next());
        }

        assertEquals(num-1, newSize, "iterator size should decrease by 1 when an element is removed");

        it1order.remove(source.get(0));
        assertEquals(it1order, it3order, "order of remaining elements in iterator should not change after removing first element");

    }

    @Test
    public void testRemove() {

        assertTrue(list.add(1), "1 should be added successfully");
        assertFalse(list.remove(2), "2 should not be removed successfully (should not exist)");
        assertTrue(list.remove(1), "1 should be removed successfully");

        assertTrue(list.addAll(source), "elements should be added successfully");

        int c1 = list.size();
        assertTrue(list.remove(source.remove(0)), "element should be removed");
        int c2 = list.size();
        assertTrue(list.remove(source.remove(0)), "element should be removed");
        int c3 = list.size();

        assertNotEquals(c2, c1, "c1 and c2 should not be equal");
        assertNotEquals(c3, c1, "c1 and c3 should not be equal");
        assertNotEquals(c3, c2, "c2 and c3 should not be equal");

        assertEquals(source.size(), list.size(), "list and source should have same size");
        assertTrue(source.containsAll(list), "all in list should also be in source");
        assertTrue(list.containsAll(source), "all in source should also be in list");

    }

    @Test
    public void testLastElement() {

        list.addAll(source);
        Iterator<Integer> it;
        int val;

        it = list.iterator();
        val = 0;
        while (it.hasNext()) val = it.next();
        assertEquals(source.get(count-1), val, "last element from list should equal last element from source");

        assertTrue(list.remove(source.get(0)), "element should be removed successfully");


        int lastElement = val;
        it = list.iterator();
        val = 0;
        while (it.hasNext()) val = it.next();
        assertEquals(lastElement, val, "last element should not change when removing the non-last element");

    }

    @Test
    public void testAsListFunction() {

        assertDoesNotThrow(() -> {
            int r1 = random.nextInt();
            int r2 = random.nextInt();
            int r3 = random.nextInt();
            int r4 = random.nextInt();

            list.add(r1);
            list.add(r2);
            list.add(r3);
            compareListToCollection(list.asList(), list);

            list.add(r2);
            compareListToCollection(list.asList(), list);

            list.remove(r1);
            compareListToCollection(list.asList(), list);

            list.clear();
            compareListToCollection(list.asList(), list);

            list.add(r4);
            compareListToCollection(list.asList(), list);
        });

    }

    @Test
    public void maintainsOrder() {

        List<Integer> list2 = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) {
            int r = random.nextInt();
            list.add(r);
            list2.add(r);
        }

        assertEquals(list2, list.asList(), "order should be maintained upon just adding elements");

        list.remove(list2.remove(2));
        list.remove(list2.remove(5));
        list.remove(list2.remove(8));

        assertEquals(list2, list.asList(), "order should be maintained after removing elements");

    }

    private <T> void compareListToCollection(List<T> list, Collection<T> c) {
        if (list.size() != c.size()) throw new IllegalStateException("not same size");
        if (!c.containsAll(list)) throw new IllegalStateException("collection does not contain all from list");
        if (!list.containsAll(c)) throw new IllegalStateException("list does not contain all from collection");
    }

}