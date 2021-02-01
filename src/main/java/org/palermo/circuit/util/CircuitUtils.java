package org.palermo.circuit.util;

import org.palermo.circuit.util.inner.GetParentPorts;

public class CircuitUtils {

    public static long[] getParentPorts(FileTreeSet<Long> relevantPorts, int inputSize, long portId) {
        return GetParentPorts.getParentPorts(relevantPorts, inputSize, portId);
    }
}
