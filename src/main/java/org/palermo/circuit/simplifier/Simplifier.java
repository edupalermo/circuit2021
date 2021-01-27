package org.palermo.circuit.simplifier;

public interface Simplifier<T> {

    int getSize();

    boolean[] simplify(T input);

    T resolve(boolean[] input);
}
