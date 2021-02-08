package org.palermo.circuit.collection;

public interface OrderedSet<T> {

    boolean add(T value);

    long size();

    boolean contains(T value);

    T select(long index);

    int height();
}
