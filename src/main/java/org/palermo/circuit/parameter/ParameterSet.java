package org.palermo.circuit.parameter;

import org.palermo.circuit.simplifier.Simplifier;
import org.palermo.circuit.util.CircuitUtils;

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

    private boolean[][] inputSample;
    private boolean[][] outputSample;



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

        this.inputSample = new boolean[argumentList.get(0).size()][];
        this.outputSample = new boolean[argumentList.get(0).size()][];

        for (int i = 0 ; i < argumentList.get(0).size(); i++) {
            this.inputSample[i] = getPrivateInputSample(i);
            this.outputSample[i] = getPrivateOutputSample(i);
        }

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
        return this.inputSample[i];
   }


    private boolean[] getPrivateInputSample(int i) {
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

    private boolean[] getPrivateOutputSample(int i) {
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

    public boolean[] getOutputSample(int i) {
        return this.outputSample[i];
    }

    public int getSampleCount() {
        return argumentList.get(0).size();
    }

    public List<Object> evaluate(long[] outputPorts, Object ... parameters) {
        boolean[] input = translateToByteArray(parameters);

        boolean[] output = generateOutput(outputPorts, input);

        return translateToOutputParameters(output);
    }

    private List<Object> translateToOutputParameters(boolean[] output) {
        List<Object> outputParameters = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < getOutputParameterCount(); i++) {
            int size = getOutputSimplifier(i).getSize();
            outputParameters.add(getOutputSimplifier(i).resolve(Arrays.copyOfRange(output, position, position + size)));
            position += size;
        }
        return outputParameters;
    }

    private boolean[] translateToByteArray(Object[] parameters) {
        boolean[] input = new boolean[this.inputBitSize];
        input[0] = false;
        input[1] = true;
        int position = 2;
        for (int i = 0; i < parameters.length; i++) {
            boolean[] partial = getInputSimplifier(i).simplify(parameters[i]);
            System.arraycopy(partial, 0, input, position, partial.length);
            position += partial.length;
        }
        return input;
    }

    private boolean[] generateOutput(long[] outputPorts, boolean[] input) {
        boolean output[] = new boolean[this.outputBitSize];

        for (int i = 0; i < outputPorts.length; i++) {
            output[i] = CircuitUtils.resolve(input, outputPorts[i]);
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
