package org.palermo.circuit.engine;

import org.palermo.circuit.parameter.ParameterSet;
import org.palermo.circuit.util.FileTreeSet;

public class NoName {

    public static boolean isConnectedToSignificantPorts(FileTreeSet relevantPorts, ParameterSet parameterSet, long portId) {
        if (portId < parameterSet.getInputSize()) {
            return true;
        }

        long[] parentPorts = getParentPorts(relevantPorts, parameterSet.getInputSize(), portId);
        return isConnectedToSignificantPorts(relevantPorts, parameterSet, parentPorts[0]) &&
                isConnectedToSignificantPorts(relevantPorts, parameterSet, parentPorts[1]);
    }

    public static long[] getParentPorts(FileTreeSet<Long> relevantPorts, int inputSize, long portId) {
        if (inputSize <= 0 ) {
            throw new RuntimeException("Input size cannot be lower than 0");
        }
        if (portId < 0 ) {
            throw new RuntimeException("Port id cannot be negative");
        }
        if (portId < inputSize) {
            return new long[] {};
        }

        long[] baseParents = getParentPorts(portId - inputSize);
        return new long[] {relevantPorts.select(baseParents[0]), relevantPorts.select(baseParents[1])};
    }

    //TODO Can be improved
    private static long[] getParentPorts(long portId) {
        int window = 0;
        int total = 0;
        int partial = 0;

        while (total < portId) {
            if (partial >= window) {
                partial = 0;
                window++;
            }
            else {
                partial++;
            }
            total++;
        }
        return new long[] {partial, window};
    }
}
