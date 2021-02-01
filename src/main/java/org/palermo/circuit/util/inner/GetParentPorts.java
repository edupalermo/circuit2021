package org.palermo.circuit.util.inner;

import org.palermo.circuit.util.FileTreeSet;

public class GetParentPorts {
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
    protected static long[] getParentPorts(long portId) {
        long window = 0;
        long total = 0;
        long partial = 0;

        while (total < portId) {
            if (portId - total - 1 >= window) {
                partial = 0;
                window++;
                total += window;
            }
            else {
                partial = portId - total;
                break;
            }

        }
        return new long[] {partial, window};
    }
}
