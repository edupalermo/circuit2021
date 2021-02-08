package org.palermo.circuit.collection;

public interface Converter<T> {

    byte[] serialize(T input);

    T deserialize(byte[] input);

    int getSize();
}
