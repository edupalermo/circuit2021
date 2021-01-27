package org.palermo.circuit.definition;

public interface Problem {

    long getNumberOfScenarios();

    long getInputSize();

    long getOutputSize();

    boolean getInputValue(long scenario, long input);

}
