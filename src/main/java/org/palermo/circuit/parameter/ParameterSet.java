package org.palermo.circuit.parameter;

import org.palermo.circuit.simplifier.CharSimplifier;
import org.palermo.circuit.simplifier.EnumSimplifier;
import org.palermo.circuit.simplifier.Simplifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterSet {

    private final List<List<Parameter>> argumentList;
    private final List<Direction> directions;
    private final List<Class<? extends Parameter>> classes;
    private final List<Simplifier> simplifiers;

    private final int inputSize;
    private final int outputSize;

    protected ParameterSet(List<Simplifier> simplifiers,
                           List<Direction> directions,
                           List<Class<? extends Parameter>> classes,
                           List<List<Parameter>> argumentList) {
        this.simplifiers = simplifiers;
        this.directions = directions;
        this.classes = classes;
        this.argumentList = argumentList;

        this.inputSize = computeInputSize();
        this.outputSize = computeOutputSize();
    }

    public int getInputSize() {
        return this.inputSize;
    }

    public int getOutputSize() {
        return this.outputSize;
    }

    public int computeInputSize() {
        int total = 2; // Adding absolute 0 and absolute 1
        for (int i = 0; i < simplifiers.size(); i++) {
            if (directions.get(i) == Direction.INPUT) {
                total += simplifiers.get(i).getSize();
            }
        }
        return total;
    }

    public int computeOutputSize() {
        int total = 0;
        for (int i = 0; i < simplifiers.size(); i++) {
            if (directions.get(i) == Direction.OUTPUT) {
                total += simplifiers.get(i).getSize();
            }
        }
        return total;
    }

    public boolean[] getInputSample(int i) {
        boolean[] input = new boolean[getInputSize()];
        int j = 0;
        input[j++] = false;
        input[j++] = true;
        for (int s = 0; s < simplifiers.size(); s++) {
            if (directions.get(s) == Direction.INPUT) {
                for (boolean b : simplifiers.get(s).simplify(this.argumentList.get(s).get(i).getRawData())) {
                    input[j++] = b;
                }
            }
        }
        return input;
    }

    public boolean[] getOutputSample(int i) {
        boolean[] output = new boolean[getOutputSize()];
        int j = 0;
        for (int s = 0; s < simplifiers.size(); s++) {
            if (directions.get(s) == Direction.OUTPUT) {
                for (boolean b : simplifiers.get(s).simplify(this.argumentList.get(s).get(i).getRawData())) {
                    output[j++] = b;
                }
            }
        }
        return output;
    }

    public int getSampleCount() {
        return argumentList.get(0).size();
    }

    public List<Parameter> evaluate(Parameter ... parameters) {

        return null; //TODO

    }

    public static ParameterSetBuilder builder() {
        return new ParameterSetBuilder();
    }
}
