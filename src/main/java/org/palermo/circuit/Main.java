package org.palermo.circuit;

import org.apache.commons.lang3.ArrayUtils;
import org.palermo.circuit.engine.NoName;
import org.palermo.circuit.parameter.*;
import org.palermo.circuit.util.Converter;
import org.palermo.circuit.util.FileTreeSet;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;

public class Main {

    public static void main(String arg[]) {
        ParameterSet parameterSet = ParameterSet.builder()
                .configure(CharParameter.class, EnumParameter.class)
                .configure(Direction.INPUT, Direction.OUTPUT)
                .add(CharParameter.of('a'), EnumParameter.of("VOWEL"))
                .add(CharParameter.of('b'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('c'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('d'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('e'), EnumParameter.of("VOWEL"))
                .add(CharParameter.of('f'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('-'), EnumParameter.of("SYMBOL"))
                .add(CharParameter.of('0'), EnumParameter.of("NUMBER"))
                .build();

        FileTreeSet<Long> relevantPorts = createFileTreeSet();
        FileTreeSet<Long> sortedOutput = createSortedOutputFileTreeSet(parameterSet, relevantPorts);

        long[] output = new long[parameterSet.getOutputSize()];
        for (int i = 0; i < output.length; i++) {
            output[i] = 0;
        }
        relevantPorts.add(0L);
        sortedOutput.add(0L);

        System.out.println(String.format("Relevant Ports %d Output %s Points %s", relevantPorts.size(), toString(output), getProgress(parameterSet, relevantPorts, output)));

        for (int i = 1; i < 1000000; i++) {
            if (bringNewOutputVariation(sortedOutput, parameterSet.getInputSize(), i)) {
                relevantPorts.add((long) i);
                sortedOutput.add((long) i);
                output = searchForOutputImprovement(parameterSet, relevantPorts, i, output);
                System.out.println(String.format("Relevant Ports %d Output %s Points %s", relevantPorts.size(), toString(output), getProgress(parameterSet, relevantPorts, output)));
            }
            else {
                //System.out.println(String.format("Port %d does not bring a new output", i));
            }

            if (finished(parameterSet, relevantPorts, output)) {
                break;
            }
        }
    }

    private static String toString(long[] input) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < input.length; i++) {
            sb.append(input[i]);

            if (i == input.length - 1) {
                sb.append("]");
            }
            else {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private static boolean bringNewOutputVariation(FileTreeSet<Long> sortedOutput, int inputSize, long portId) {
        return !sortedOutput.contains(portId);
    }

    private static long[] searchForOutputImprovement(ParameterSet parameterSet, FileTreeSet<Long> relevantPorts, int portId, long[] originalOutput) {

        long[] newOutput = ArrayUtils.clone(originalOutput);
        long[] originalOutputPoints = new long[originalOutput.length];
        long[] newPortPoints = new long[originalOutput.length];

        int inputSize = parameterSet.getInputSize();

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (resolve(relevantPorts, input, inputSize, originalOutput[j]) == output[j]) {
                    originalOutputPoints[j]++;
                }

                if (resolve(relevantPorts, input, inputSize, portId) == output[j]) {
                    newPortPoints[j]++;
                }
            }
        }

        for (int i = 0; i < newOutput.length; i++) {
            if (newPortPoints[i] > originalOutputPoints[i]) {
                newOutput[i] = portId;
            }
        }

        return newOutput;
    }

    private static String getProgress(ParameterSet parameterSet, FileTreeSet<Long> relevantPorts, long[] originalOutput) {
        long[] originalOutputPoints = new long[originalOutput.length];
        int inputSize = parameterSet.getInputSize();
        int total = 0;

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (resolve(relevantPorts, input, inputSize, originalOutput[j]) == output[j]) {
                    originalOutputPoints[j]++;
                }
            }
            total+= output.length;
        }

        return String.format("%d out of %d", Arrays.stream(originalOutputPoints).sum(), total);
    }

    private static boolean finished(ParameterSet parameterSet, FileTreeSet<Long> relevantPorts, long[] originalOutput) {
        long[] originalOutputPoints = new long[originalOutput.length];
        int inputSize = parameterSet.getInputSize();
        int total = 0;

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (resolve(relevantPorts, input, inputSize, originalOutput[j]) == output[j]) {
                    originalOutputPoints[j]++;
                }
            }
            total+= output.length;
        }

        return total == Arrays.stream(originalOutputPoints).sum();
    }

    public static boolean resolve(FileTreeSet<Long> relevantPorts, boolean[] input, int inputSize, long portId) {
        if (portId < inputSize) {
            return input[(int) portId];
        }
        else {
            long[] parents = NoName.getParentPorts(relevantPorts, inputSize, portId);
            return !(resolve(relevantPorts, input, inputSize, parents[0]) && resolve(relevantPorts, input, inputSize, parents[1]));
        }
    }

    private static FileTreeSet<Long> createSortedOutputFileTreeSet(ParameterSet parameterSet, FileTreeSet<Long> relevantPorts) {

        Comparator<Long> comparator = new Comparator<Long>() {

            @Override
            public int compare(Long o1, Long o2) {
                int result = 0;
                int inputSize = parameterSet.getInputSize();

                for (int i = 0; i < parameterSet.getSampleCount(); i++) {
                    boolean[] input = parameterSet.getInputSample(i);

                    boolean b1 = resolve(relevantPorts, input, inputSize, o1);
                    boolean b2 = resolve(relevantPorts, input, inputSize, o2);

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
            }
        };

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

        try {
            return new FileTreeSet<Long>(
                    File.createTempFile("tree_", ".tmp"),
                    File.createTempFile("data_", ".tmp"),
                    comparator,
                    converter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileTreeSet<Long> createFileTreeSet() {

        Comparator<Long> comparator = new Comparator<Long>() {

            @Override
            public int compare(Long o1, Long o2) {
                return Long.compare(o1, o2);
            }
        };

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

        try {
            return new FileTreeSet<Long>(
                    File.createTempFile("tree_", ".tmp"),
                    File.createTempFile("data_", ".tmp"),
                    comparator,
                    converter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
