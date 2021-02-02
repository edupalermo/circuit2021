package org.palermo.circuit.parameter;

import org.palermo.circuit.simplifier.Simplifier;
import org.palermo.circuit.util.CircuitUtils;
import org.palermo.circuit.util.FileTreeSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterSet {

    private final List<List<Parameter>> argumentList;
    private final List<Direction> directions;
    private final List<Class<? extends Parameter>> classes;
    private final List<Simplifier> simplifiers;

    private final int inputBitSize;
    private final int outputBitSize;

    protected ParameterSet(List<Simplifier> simplifiers,
                           List<Direction> directions,
                           List<Class<? extends Parameter>> classes,
                           List<List<Parameter>> argumentList) {
        this.simplifiers = simplifiers;
        this.directions = directions;
        this.classes = classes;
        this.argumentList = argumentList;

        this.inputBitSize = computeInputSize();
        this.outputBitSize = computeOutputSize();
    }

    public int getInputBitSize() {
        return this.inputBitSize;
    }

    public int getOutputBitSize() {
        return this.outputBitSize;
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
        boolean[] input = new boolean[getInputBitSize()];
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
        boolean[] output = new boolean[getOutputBitSize()];
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

    public List<Object> evaluate(long[] outputPorts, FileTreeSet<Long> relevantPorts, Object ... parameters) {

        boolean[] input = new boolean[this.inputBitSize];
        int position = 0;
        for (int i = 0; i < parameters.length; i++) {
            boolean[] partial = getInputSimplifier(i).simplify(parameters[i]);
            System.arraycopy(partial, 0, input, position, partial.length);
            position += partial.length;
        }

        boolean[] output = generateOutput(outputPorts, relevantPorts, input);

        List<Object> outputParameters = new ArrayList<>();
        position = 0;
        for (int i = 0; i < getOutputParameterCount(); i++) {
            int size = getOutputSimplifier(i).getSize();
            outputParameters.add(getOutputSimplifier(i).resolve(Arrays.copyOfRange(output, position, position + size)));
            position += size;

            System.out.println("e0" + getOutputSimplifier(i).resolve(new boolean[] {false, false}));
            System.out.println("e1" + getOutputSimplifier(i).resolve(new boolean[] {false, true}));
            System.out.println("e2" + getOutputSimplifier(i).resolve(new boolean[] {true, false}));
            System.out.println("e3" + getOutputSimplifier(i).resolve(new boolean[] {true, true}));
        }

        return outputParameters;
    }

    private boolean[] generateOutput(long[] outputPorts, FileTreeSet<Long> relevantPorts, boolean[] input) {
        boolean output[] = new boolean[this.outputBitSize];

        for (int i = 0; i < outputPorts.length; i++) {
            output[i] = CircuitUtils.resolve(relevantPorts, input, outputPorts[i]);
        }

        return output;
    }

    private Simplifier getInputSimplifier(int index) {
        int count = 0;
        for (int i = 0; i < simplifiers.size(); i++) {
            if (directions.get(i) == Direction.INPUT) {
                if (count == index) {
                    return simplifiers.get(i);
                }
                count++;
            }
        }
        throw new RuntimeException("Fail to retrieve input simplifier " + index);
    }

    private Simplifier getOutputSimplifier(int index) {
        int count = 0;
        for (int i = 0; i < simplifiers.size(); i++) {
            if (directions.get(i) == Direction.OUTPUT) {
                if (count == index) {
                    return simplifiers.get(i);
                }
                count++;
            }
        }
        throw new RuntimeException("Fail to retrieve input simplifier " + index);
    }

    private int getOutputParameterCount() {
        int count = 0;
        for (int i = 0; i < simplifiers.size(); i++) {
            if (directions.get(i) == Direction.OUTPUT) {
                count++;
            }
        }
        return count;
    }


    public static ParameterSetBuilder builder() {
        return new ParameterSetBuilder();
    }
}
