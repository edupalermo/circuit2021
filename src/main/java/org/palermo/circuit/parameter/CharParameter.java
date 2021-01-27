package org.palermo.circuit.parameter;

public class CharParameter implements Parameter<Character> {

    private final char c;

    private CharParameter(char c) {
        this.c = c;
    }

    public static CharParameter of(char c) {
        return new CharParameter(c);
    }

    @Override
    public Character getRawData() {
        return Character.valueOf(this.c);
    }
}
