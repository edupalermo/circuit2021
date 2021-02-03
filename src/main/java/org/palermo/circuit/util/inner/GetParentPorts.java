package org.palermo.circuit.util.inner;

public class GetParentPorts {
    public static long[] getParentPortIds(int inputSize, long portId) {
        if (inputSize <= 0 ) {
            throw new RuntimeException("Input size cannot be lower than 0");
        }
        if (portId < 0 ) {
            throw new RuntimeException("Port id cannot be negative");
        }
        if (portId < inputSize) {
            return new long[] {};
        }

        long[] baseParents = getParentPortIds(portId - inputSize);
        return new long[] {baseParents[0], baseParents[1]};
    }

    protected static long[] getParentPortIds(long portId) {
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

    public static long getPortIdByParentPortIds(int inputSize, long left, long right) {
        return getPortIdByParentPortIds(left, right) + inputSize;
    }

    protected static long getPortIdByParentPortIds(long left, long right) {
        long portId = 0;
        for (int i = 0; i <= right; i++) {
            portId += i;
        }
        return portId + left;
    }
}
