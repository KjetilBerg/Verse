package com.kbindiedev.verse.system;

import java.lang.reflect.Array;
import java.util.*;

// TODO: cachedList may be unnecessary

/**
 * A collection that has O(1) insertion, O(1) removal and O(1) lookup.
 * Added elements are always added to the end of the collection, and the order is always maintained.
 * Internally, the collection is stored like a LinkedList.
 * The structure caches a list internally that can be quickly retrieved.
 *      O(1) if no elements have been removed since the last time it was called, otherwise O(n).
 * @param <T> - The type of data to store.
 */
public class FastList<T> implements Collection<T> {

    private Node<T> head;
    private Node<T> tail;
    private HashMap<Integer, Node<T>> idLookupTable;
    private HashMap<T, LinkedList<Integer>> elementToIdsTable;
    private int id;

    private List<T> cachedList;
    private boolean cachedListValid;

    public FastList() {
        idLookupTable = new HashMap<>();
        elementToIdsTable = new HashMap<>();
        id = 0;

        cachedList = new ArrayList<>();
        cachedListValid = true;
    }

    @Override
    public int size() { return idLookupTable.size(); }

    @Override
    public boolean isEmpty() { return size() == 0; }

    /** @return an iterator of all objects in the list, in the order they were added. */
    @Override
    public Iterator<T> iterator() { return new NodeIterator<>(head); }

    /**
     * Add an element to the end of this list.
     * If the element already exists, then it is added again.
     * @param t - The element to add.
     * @return always returns true, returns boolean by contract of {@link Collection#add(Object)}
     * @kb.time O(1).
     */
    @Override
    public boolean add(T t) {
        if (!elementToIdsTable.containsKey(t)) elementToIdsTable.put(t, new LinkedList<>());

        int elementId = id++;
        elementToIdsTable.get(t).add(elementId);

        Node<T> node = new Node<>(null, null, t);
        if (head == null) head = node;
        if (tail != null) {
            node.prev = tail;
            tail.next = node;
        }
        tail = node;

        idLookupTable.put(elementId, node);

        cachedList.add(t);

        return true;
    }

    /**
     * Remove an element from this list.
     * If multiple items are found to match the given parameter, then the first found matching element is removed.
     * @param o - The element to remove.
     * @return true if the element was removed, false otherwise.
     * @kb.time O(1).
     */
    @Override
    public boolean remove(Object o) {
        if (!elementToIdsTable.containsKey(o)) return false;

        LinkedList<Integer> ids = elementToIdsTable.get(o);
        if (ids == null || ids.size() == 0) return false;
        int elementId = ids.poll();
        if (ids.size() == 0) elementToIdsTable.remove(o);

        Node<T> node = idLookupTable.remove(elementId);

        if (node.next == null) tail = node.prev;
        if (node.prev == null) head = node.next;

        if (node.next != null) node.next.prev = node.prev;
        if (node.prev != null) node.prev.next = node.next;

        cachedListValid = false;

        return true;
    }

    /**
     * Check if an element exists in this list.
     * @param o - The element to check for.
     * @return whether or not the specified element exists in this list.
     */
    @Override
    public boolean contains(Object o) { return elementToIdsTable.containsKey(o); }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c)
            if (!contains(o)) return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T t : c) {
            boolean added = add(t);
            if (added) changed = true;
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            boolean removed = remove(o);
            if (removed) changed = true;
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            boolean removed = remove(o);
            if (removed) changed = true;
        }
        return changed;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        idLookupTable.clear();
        elementToIdsTable.clear();
        id = 0;

        cachedList.clear();
        cachedListValid = true;
    }

    /**
     * Retrieve an element from this list (returns exact element).
     * If multiple items are found to match the given parameter, then the first found matching element is retrieved.
     * @param t - The element to look for.
     * @return the first found matching element in this list.
     * @kb.time O(1).
     */
    public T actualValue(T t) {
        if (!elementToIdsTable.containsKey(t)) return null;

        LinkedList<Integer> ids = elementToIdsTable.get(t);
        if (ids == null || ids.size() == 0) return null;
        int elementId = ids.peek();

        Node<T> node = idLookupTable.get(elementId);

        return node.value;
    }

    // TODO: rename to getCachedList (or remove entirely ?)
    /**
     * Get this collection as a list.
     * The order of the list is the same as the order of this collection (ordered by registration order).
     * @return this collection as a list.
     * @kb.time O(1) if {@link #remove(Object)} has not been called since this method was last called, otherwise O(n).
     */
    public List<T> asList() {
        ensureCachedListValid();
        return cachedList;
    }

    @Override
    public Object[] toArray() {
        // note: do not use .asList().toArray(). causes infinite loop
        Object[] ret = new Object[size()];
        Iterator<T> it = iterator();
        for (int i = 0; i < ret.length; ++i) ret[i] = it.next();
        return ret;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        // note: do not use .asList().toArray(). causes infinite loop
        T1[] ret;

        if (a.length < size()) ret = (T1[])Array.newInstance(a.getClass(), size());
        else ret = a;

        Iterator<T> it = iterator();
        for (int i = 0; i < ret.length; ++i) ret[i] = (T1)it.next();

        if (ret.length > size()) ret[size()] = null;
        return ret;
    }

    @Override
    public String toString() {
        Iterator<T> it = iterator();
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        while (it.hasNext()) sb.append(it.next()).append(", ");
        if (sb.length() > 1) sb.setLength(sb.length() - 2);
        return sb.append("]").toString();
    }

    /** if the current list is invalid, rebuild it and set it to valid. order is maintained. */
    private void ensureCachedListValid() {
        if (cachedListValid) return;
        cachedList.clear();
        cachedList.addAll(this);
    }

    private static class Node<T> {
        private Node<T> prev;
        private Node<T> next;
        private T value;
        public Node(Node<T> prev, Node<T> next, T value) { this.prev = prev; this.next = next; this.value = value; }
    }

    private static class NodeIterator<T> implements Iterator<T> {

        private Node<T> node;

        public NodeIterator(Node<T> node) { this.node = node; }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public T next() {
            if (!hasNext()) return null;
            T val = node.value;
            node = node.next;
            return val;
        }
    }
}
