package org.palermo.circuit.util;

import org.palermo.circuit.collection.Converter;
import org.palermo.circuit.collection.FileOrderedSet;
import org.palermo.circuit.collection.MemoryOrderedSet;
import org.palermo.circuit.collection.OrderedSet;
import org.palermo.circuit.parameter.ParameterSet;
import org.palermo.circuit.util.inner.GetParentPorts;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

public class CircuitUtils {

    public static long[] getParentPorts(int inputSize, long portId) {
        return GetParentPorts.getParentPortIds(inputSize, portId);
    }

    public static long getPortIdByParentPortIds(int inputSize, long leftPortId, long rightPortId) {
        return GetParentPorts.getPortIdByParentPortIds(inputSize, leftPortId, rightPortId);
    }

    public static OrderedSet<Long> createLongFileOrderedSet() {
        try {
            return createLongFileOrderedSet(
                    File.createTempFile("tree_", ".tmp"),
                    File.createTempFile("data_", ".tmp")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static OrderedSet<Long> createLongFileOrderedSet(File treeFile, File dataFile) {
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

        return new FileOrderedSet<>(
                treeFile,
                dataFile,
                comparator,
                converter);
    }

    public static OrderedSet<Long> createLongMemoryOrderedSet() {
        return new MemoryOrderedSet<>((o1, o2) -> Long.compare(o1, o2));
    }

    public static OrderedSet<Long> createOutputMemoryOrderedSet(ParameterSet parameterSet) {

        Comparator<Long> comparator = (o1, o2) -> {
            int result = 0;

            for (int i = 0; i < parameterSet.getSampleCount(); i++) {
                boolean[] input = parameterSet.getInputSample(i);

                boolean b1 = CircuitUtils.resolve(input, o1);
                boolean b2 = CircuitUtils.resolve(input, o2);

                if (!b1 && b2) {
                    result = -1;
                    break;
                }
                if (b1 && !b2) {
                    result = 1;
                    break;
                }
            }

            return result;
        };

        return new MemoryOrderedSet<>(comparator);
    }

    private static final TreeMap<InputAndPortId, Boolean> cache = new TreeMap<>();

    public static boolean resolve(boolean[] input, long portId) {
        if (portId < input.length) {
            return input[(int) portId];
        } else {
            Boolean cacheResult = cache.get(InputAndPortId.of(input, portId));
            if (cacheResult != null) {
                return cacheResult.booleanValue();
            }
            long[] parents = CircuitUtils.getParentPorts(input.length, portId);
            boolean resolved = !(resolve(input, parents[0]) && resolve(input, parents[1]));
            cache.put(InputAndPortId.of(input, portId), Boolean.valueOf(resolved));
            return resolved;
        }
    }

    public static class InputAndPortId implements Comparable<InputAndPortId> {

        private final boolean[] input;
        private final long portId;

        public InputAndPortId(boolean[] input, long portId) {
            this.input = input;
            this.portId = portId;
        }

        public static InputAndPortId of(boolean[] input, long portId) {
            return new InputAndPortId(input, portId);
        }

        private int compare(boolean[] i1, boolean[] i2) {
            if (i1.length < i2.length) {
                return -1;
            }
            else if (i1.length == i2.length) {
                int partial = 0;
                for (int i = 0; i < i1.length; i++) {
                    partial = Boolean.compare(i1[i], i2[i]);
                    if (partial != 0) {
                        break;
                    }
                }
                return partial;
            }
            else {
                return 1;
            }
        }

        @Override
        public int compareTo(InputAndPortId o) {
            int partial = Long.compare(this.portId, o.portId); // This tends to be more different
            if (partial == 0) {
                partial = compare(this.input, o.input);
            }
            return partial;

        }
    }


    public static OrderedSet<Long> createOutputFileOrderedSet(ParameterSet parameterSet) {
        try {
            return createOutputFileOrderedSet(
                    File.createTempFile("tree_", ".tmp"),
                    File.createTempFile("data_", ".tmp"),
                    parameterSet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static OrderedSet<Long> createOutputFileOrderedSet(File treeFile, File dataFile, ParameterSet parameterSet) {

        Comparator<Long> comparator = (o1, o2) -> {
            int result = 0;

            for (int i = 0; i < parameterSet.getSampleCount(); i++) {
                boolean[] input = parameterSet.getInputSample(i);

                boolean b1 = CircuitUtils.resolve(input, o1);
                boolean b2 = CircuitUtils.resolve(input, o2);

                if (!b1 && b2) {
                    result = -1;
                    break;
                }
                if (b1 && !b2) {
                    result = 1;
                    break;
                }
            }

            return result;
        };

        Converter<Long> converter = new Converter<>() {

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

        return new FileOrderedSet<>(treeFile, dataFile, comparator, converter);
    }
}
