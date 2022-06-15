package com.kbindiedev.verse.system;

import com.kbindiedev.verse.math.helpers.Tuple;

import java.lang.reflect.Array;
import java.util.*;

// TODO: tree balancing (in that case, remember childrenToMyLeft)
// TODO: testing

/**
 * Similar to FastList, but sorted. Elements are sorted upon insertion. O(log n) insertion, O(1) removal, O(1) lookup.
 * Indexed lookup is possible with two alternatives: O(log n) {@link #getByIndex(int)} or O(1)/O(n) {@link #getCachedList().get(int)}..
 * Internally, this class is structured like a binary tree. Duplicate values are allowed.
 *
 * The structure caches a list internally that can be quickly retrieved {@link #getCachedList()}.
 *      O(1) if no elements have been added or removed since the last time it was called, otherwise O(n).
 *
 * @param <T> - The type of data to store.
 *
 * @see FastList
 */
public class SortedFastList<T> implements Collection<T> {

    private TreeNode<T> root;
    private HashMap<T, TreeNode<T>> lookupTable;
    private Comparator<T> comparator;

    private List<T> cachedList;
    boolean cachedListValid;

    public SortedFastList(Comparator<T> comparator) {
        root = null;
        lookupTable = new HashMap<>();
        this.comparator = comparator;

        cachedList = new ArrayList<>();
        cachedListValid = true;
    }

    /**
     * @param item - The item.
     * @param wrap - If the item would be inserted at the beginning or end of the structure, then:
     *                  if false, both entries point to index 0 or index size() - 1 correspondingly.
     *                  if true, then one entry wraps around to the other side of the structure.
     *                      (if beginning, then first tuple will be last element, and vice versa).
     * @return a tuple of the two entries that would immediately preceed and succeed the given item if it was inserted.
     *          if the list is empty, then (null, null) is returned. if the size is 1, then (entry1, entry1) is returned.
     *          if an exact match is found, then (item, item) is returned.
     */
    public Tuple<T, T> getSurroundingEntries(T item, boolean wrap) {
        if (root == null) return new Tuple<>(null, null);

        TreeNode<T> current = root;
        while (true) {
            int comparison = comparator.compare(item, current.value);
            if (comparison == 0) return new Tuple<>(current.value, current.value);

            if (comparison < 0) {
                if (current.left == null) {
                    // i am high, prev is low
                    TreeNode<T> prev = current.getPrevious();
                    if (prev == null && wrap) prev = getNodeByIndex(size() - 1);
                    if (prev == null) prev = current;
                    return new Tuple<>(prev.value, current.value);
                }
                current = current.left;
            }
            if (comparison > 0) {
                if (current.right == null) {
                    // i am low, next is high
                    TreeNode<T> next = current.getNext();
                    if (next == null && wrap) next = getNodeByIndex(0);
                    if (next == null) next = current;
                    return new Tuple<>(next.value, current.value);
                }
                current = current.right;
            }
        }
    }

    /**
     * @return a value by its index in the list, or null if index falls outside range.
     * @kb.time O(log n).
     */
    public T getByIndex(int index) {
        TreeNode<T> node = getNodeByIndex(index);
        if (node == null) return null;
        return node.value;
    }

    private TreeNode<T> getNodeByIndex(int index) {
        if (index >= size()) return null;
        if (index < 0) return null;

        TreeNode<T> current = root;
        while (current.getIndexInTree() != index) {
            if (index < current.getIndexInTree()) current = current.left; else current = current.right;
        }

        return current;
    }

    @Override
    public int size() {
        return lookupTable.size();
    }

    @Override
    public boolean isEmpty() {
        return lookupTable.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return lookupTable.containsKey(o);
    }

    @Override
    public Iterator<T> iterator() {
        return new TreeNodeIterator<>(root);
    }

    @Override
    public Object[] toArray() {
        Object[] ret = new Object[size()];
        Iterator<T> it = iterator();
        for (int i = 0; i < ret.length; ++i) ret[i] = it.next();
        return ret;
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        T1[] ret;

        if (a.length < size()) ret = (T1[]) Array.newInstance(a.getClass(), size());
        else ret = a;

        Iterator<T> it = iterator();
        for (int i = 0; i < ret.length; ++i) ret[i] = (T1)it.next();

        if (ret.length > size()) ret[size()] = null;
        return ret;
    }

    /** @return always true, by contract of {@link Collection#add(Object)}. */
    @Override
    public boolean add(T t) {
        TreeNode<T> node = new TreeNode<>(comparator, t);
        if (root == null) root = node; else root.addNode(node);
        lookupTable.putIfAbsent(t, node);
        cachedListValid = false;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        TreeNode<T> target = lookupTable.get(o);
        if (target == null) return false;

        if (target.left != null && target.left.equals(o)) lookupTable.put((T)o, target.left); // replace value in case duplicate exists.
        if (target.parent == null) root = target.removeAsRoot(); else target.remove();
        cachedListValid = false;
        return true;
    }

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
        root = null;
        lookupTable.clear();

        cachedList.clear();
        cachedListValid = true;
    }

    public List<T> getCachedList() {
        ensureCachedListValid();
        return cachedList;
    }

    private void ensureCachedListValid() {
        if (cachedListValid) return;
        cachedList.clear();
        cachedList.addAll(this);
    }

    private static class TreeNode<T> {
        private Comparator<T> comparator;
        private TreeNode<T> parent;
        private TreeNode<T> left;
        private TreeNode<T> right;
        private T value;
        private TreeNode<T> rightTreeLeftmostNode;
        private TreeNode<T> leftTreeRightmostNode;
        private int childrenToMyleft;

        public TreeNode(Comparator<T> comparator, T value) {
            parent = null; left = null; right = null;
            this.comparator = comparator;
            this.value = value;
            rightTreeLeftmostNode = null;
            leftTreeRightmostNode = null;
            childrenToMyleft = 0;
        }

        /** Add the given node recursively. */
        public void addNode(TreeNode<T> node) {
            int comp = comparator.compare(node.value, value);
            if (comp <= 0) {
                childrenToMyleft++;
                if (leftTreeRightmostNode == null || comparator.compare(node.value, leftTreeRightmostNode.value) > 0) {
                    leftTreeRightmostNode = node;
                }
                if (left != null) { left.addNode(node); return; }
                left = node; node.parent = this;
            } else {
                if (rightTreeLeftmostNode == null || comparator.compare(node.value, rightTreeLeftmostNode.value) <= 0) {
                    rightTreeLeftmostNode = node;
                }
                if (right != null) { right.addNode(node); return; }
                right = node; node.parent = this;
            }
        }

        /** Remove this node, but assume you are root and have no parent. */
        public TreeNode<T> removeAsRoot() {
            if (left == null) return right;
            if (right == null) return left;

            // both not null
            TreeNode<T> replacement = rightTreeLeftmostNode;
            replacement.remove();
            replacement.parent = null;
            replacement.right = right;
            replacement.left = left;
            right.parent = replacement;
            left.parent = replacement;
            replacement.childrenToMyleft = childrenToMyleft;
            return replacement;
        }

        /** Remove this node. */
        public void remove() {
            if (parent == null) throw new IllegalArgumentException("parent == null require special handling");

            TreeNode<T> replacement = removeAsRoot();

            replacement.leftTreeRightmostNode = leftTreeRightmostNode;
            replacement.rightTreeLeftmostNode = rightTreeLeftmostNode;
            if (replacement == rightTreeLeftmostNode) {
                replacement.rightTreeLeftmostNode = replacement.parent;
                while (replacement.rightTreeLeftmostNode.left != null) replacement.rightTreeLeftmostNode = replacement.rightTreeLeftmostNode.left;
            }

            replacement.parent = parent;

            boolean iAmLeft = (this == parent.left);
            if (iAmLeft) parent.left = replacement; else parent.right = replacement;
            if (iAmLeft) parent.oneChildLeftWasRemoved();
        }

        /** @return my next node, in order of ascending value. */
        public TreeNode<T> getNext() {
            if (rightTreeLeftmostNode != null) return rightTreeLeftmostNode;
            TreeNode<T> current = this;
            while (current.parent != null && current == current.parent.right) current = current.parent;
            return current.parent;
        }

        /** @return my previous node, in order of ascending value. */
        public TreeNode<T> getPrevious() {
            if (leftTreeRightmostNode != null) return leftTreeRightmostNode;
            TreeNode<T> current = this;
            while (current.parent != null && current == current.parent.left) current = current.parent;
            return current.parent;
        }

        public int getIndexInTree() { return childrenToMyleft; }

        private void oneChildLeftWasRemoved() {
            childrenToMyleft--;
            if (parent != null && this == parent.left) parent.oneChildLeftWasRemoved();
        }

    }

    private static class TreeNodeIterator<T> implements Iterator<T> {

        private TreeNode<T> current;

        public TreeNodeIterator(TreeNode<T> root) {
            current = root;
            if (current != null) {
                while (current.left != null) current = current.left;
            }
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T ret = current.value;
            current = current.getNext();
            return ret;
        }
    }

}
