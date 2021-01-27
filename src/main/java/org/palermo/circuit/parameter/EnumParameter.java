package org.palermo.circuit.parameter;

public class EnumParameter implements Parameter<String> {

    private final String value;

    private EnumParameter(String value) {
        this.value = value;
    }

    public static EnumParameter of(String value) {
        return new EnumParameter(value);
    }

    @Override
    public String getRawData() {
        return this.value;
    }
}
