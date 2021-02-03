package org.palermo.circuit.util;

import org.palermo.circuit.util.inner.GetParentPorts;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Comparator;

public class CircuitUtils {

    public static long[] getParentPorts(int inputSize, long portId) {
        return GetParentPorts.getParentPortIds(inputSize, portId);
    }

    public static long getPortIdByParentPortIds(int inputSize, long leftPortId, long rightPortId) {
        return GetParentPorts.getPortIdByParentPortIds(inputSize, leftPortId, rightPortId);
    }

    public static FileTreeSet<Long> createLongFileTreeSet() {
        try {
            return createLongFileTreeSet(
                    File.createTempFile("tree_", ".tmp"),
                    File.createTempFile("data_", ".tmp")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public static boolean resolve(boolean[] input, long portId) {
        if (portId < input.length) {
            return input[(int) portId];
        } else {
            long[] parents = CircuitUtils.getParentPorts(input.length, portId);
            return !(resolve(input, parents[0]) && resolve(input, parents[1]));
        }
    }
}
