package org.palermo.circuit.example;

import org.palermo.circuit.definition.Problem;

public class VowelProblem implements Problem {

    public VowelProblem() {

    }

    @Override
    public long getNumberOfScenarios() {
        return 0;
    }

    @Override
    public long getInputSize() {
        return 0;
    }

    @Override
    public long getOutputSize() {
        return 0;
    }

    @Override
    public boolean getInputValue(long scenario, long input) {
        return false;
    }
}
