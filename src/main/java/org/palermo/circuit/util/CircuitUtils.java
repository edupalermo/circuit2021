package org.palermo.circuit.util;

import org.palermo.circuit.util.inner.GetParentPorts;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Comparator;

public class CircuitUtils {

    public static long[] getParentPorts(FileTreeSet<Long> relevantPorts, int inputSize, long portId) {
        return GetParentPorts.getParentPorts(relevantPorts, inputSize, portId);
    }

    public static FileTreeSet<Long> createLongFileTreeSet(File treeFile, File dataFile) {
        Comparator<Long> comparator = (o1, o2) -> Long.compare(o1, o2);

        Converter<Long> converter = new Converter<Long>() {

            @Override
            public byte[] serialize(Long input) {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.putLong(input);
                return buffer.array();
            }

            @Override
            public Long deserialize(byte[] input) {
                ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
                buffer.put(input);
                buffer.flip();//need flip
                return buffer.getLong();
            }

            @Override
            public int getSize() {
                return Long.BYTES;
            }
        };

        return new FileTreeSet<>(
                treeFile,
                dataFile,
                comparator,
                converter);
    }

    public static boolean resolve(FileTreeSet<Long> relevantPorts, boolean[] input, long portId) {
        if (portId < input.length) {
            return input[(int) portId];
        } else {
            long[] parents = CircuitUtils.getParentPorts(relevantPorts, input.length, portId);
            return !(resolve(relevantPorts, input, parents[0]) && resolve(relevantPorts, input, parents[1]));
        }
    }
}
