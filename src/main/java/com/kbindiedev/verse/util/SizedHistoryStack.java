package com.kbindiedev.verse.util;

import java.util.ArrayList;
import java.util.Collection;

public class SizedHistoryStack<T> {  //TODO: rename

    private int index;
    private int size;
    private int numElements;
    private ArrayList<T> data;

    public SizedHistoryStack(int size) {
        this.size = size;
        index = 0;
        numElements = 0;
        data = new ArrayList<>(size);
    }

    private void adjustIndex(int by) {
        index = (index + by + size) % size;
    }

    public void push(T piece) {
        data.add(index, piece);
        adjustIndex(1);

        numElements++;
        if (numElements > size) numElements = size; //since we overwrote previous data
    }

    public T pop() throws IndexOutOfBoundsException {
        adjustIndex(-1);
        numElements--;  //will never dip below 0 because of IndexOutOfBoundsException
        return data.remove(index);
    }

    public T peek() throws IndexOutOfBoundsException { return peek(0); }
    public T peek(int offset) throws IndexOutOfBoundsException  {
        offset = offset % size;
        int pos = (index - 1 + offset + size) % size;
        return data.get(pos);
    }

    public Collection<T> history() { return history(size); }
    public Collection<T> history(int amount) {
        if (amount > numElements) amount = numElements;

        ArrayList<T> list = new ArrayList<>(amount);
        for (int i = 0; i < amount; ++i) { list.add(i, peek(-i)); }
        return list;
    }



}
