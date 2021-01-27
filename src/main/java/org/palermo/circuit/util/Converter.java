package org.palermo.circuit.util;

public interface Converter<T> {

    byte[] serialize(T input);

     T deserialize(byte[] input);

    int getSize();
}
