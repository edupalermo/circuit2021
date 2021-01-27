package org.palermo.circuit.engine;

import org.palermo.circuit.parameter.ParameterSet;
import org.palermo.circuit.util.FileTree;

public class NoName {

    public static boolean isConnectedToSignificantPorts(FileTree relevantPorts, ParameterSet parameterSet, long portId) {
        return false;
    }

    public static long[] getParentPorts(int inputSize, long portId) {
        if (inputSize <= 0 ) {
            throw new RuntimeException("Input size cannot be lower than 0");
        }
        if (portId < 0 ) {
            throw new RuntimeException("Port id cannot be negative");
        }
        if (portId < inputSize) {
            return new long[] {};
        }

        return getParentPorts(portId - inputSize);
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
