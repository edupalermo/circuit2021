package org.palermo.circuit.output;

import org.palermo.circuit.parameter.ParameterSet;
import org.palermo.circuit.util.CircuitUtils;

public class OutputContainer {

    private final ParameterSet parameterSet;
    private long[] outputPortIds;
    private long[] outputPortIdPoints;

    private OutputContainer(ParameterSet parameterSet) {
        this.parameterSet = parameterSet;
    }

    public boolean evaluate(long portId) {
        if (this.outputPortIds == null) {
            initialEvaluation(portId);
            return true;
        }

        long[] newPortPoints = new long[this.parameterSet.getOutputBitSize()];

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (CircuitUtils.resolve(input, portId) == output[j]) {
                    newPortPoints[j]++;
                }
            }
        }

        boolean hasImprovement = false;
        for (int i = 0; i < newPortPoints.length; i++) {
            if (newPortPoints[i] > outputPortIdPoints[i]) {
                outputPortIdPoints[i] = newPortPoints[i];
                outputPortIds[i] = portId;
                hasImprovement = true;
            }
        }
        return hasImprovement;
    }

    private void initialEvaluation(long portId) {
        this.outputPortIds = new long[this.parameterSet.getOutputBitSize()];
        this.outputPortIdPoints = new long[this.parameterSet.getOutputBitSize()];
        for (int i = 0; i < this.outputPortIds.length; i++) {
            this.outputPortIds[i] = portId;
            this.outputPortIdPoints[i] = 0;
        }

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (CircuitUtils.resolve(input, outputPortIds[j]) == output[j]) {
                    outputPortIdPoints[j]++;
                }
            }
        }
    }

    public String getOutputAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < outputPortIds.length; i++) {
            sb.append(outputPortIds[i]);

            if (i == outputPortIds.length - 1) {
                sb.append("]");
            } else {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public long getTotalPoints() {
        long total = 0;
        for (int i = 0; i < outputPortIdPoints.length; i++) {
            total += outputPortIdPoints[i];
        }
        return total;
    }

    private long maxPoints() {
        return  parameterSet.getOutputSample(0).length * parameterSet.getSampleCount();
    }

    public boolean finished() {
        return this.maxPoints() == this.getTotalPoints();
    }

    public String getProgress() {
        return String.format("%d out of %d", this.getTotalPoints(), this.maxPoints());
    }


    public static OutputContainer of(ParameterSet parameterSet) {
        return new OutputContainer(parameterSet);
    }
}
