package org.palermo.circuit;

import org.apache.commons.lang3.ArrayUtils;
import org.palermo.circuit.clock.Clock;
import org.palermo.circuit.parameter.CharParameter;
import org.palermo.circuit.parameter.Direction;
import org.palermo.circuit.parameter.EnumParameter;
import org.palermo.circuit.parameter.ParameterSet;
import org.palermo.circuit.util.CircuitUtils;
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

        Clock clock = Clock.start();

        FileTreeSet<Long> relevantPorts = CircuitUtils.createLongFileTreeSet(
                new File("C:\\temp\\relevant.tree"),
                new File("C:\\temp\\relevant.data"));
        FileTreeSet<Long> sortedOutput = createSortedOutputFileTreeSet(
                new File("C:\\temp\\sortedOutput.tree"),
                new File("C:\\temp\\sortedOutput.data"),
                parameterSet,
                relevantPorts);

        long[] output = new long[parameterSet.getOutputBitSize()];
        for (int i = 0; i < output.length; i++) {
            output[i] = 0;
        }
        relevantPorts.add(0L);
        sortedOutput.add(0L);

        System.out.println(String.format("Relevant Ports %d Output %s Points %s", relevantPorts.size(), toString(output), getProgress(parameterSet, relevantPorts, output)));

        for (int i = 1; i < 1000000; i++) {
            if (bringNewOutputVariation(sortedOutput, i)) {
                relevantPorts.add((long) i);
                sortedOutput.add((long) i);
                output = searchForOutputImprovement(parameterSet, relevantPorts, i, output);
                System.out.println(String.format("Relevant Ports %d Output %s Points %s", relevantPorts.size(), toString(output), getProgress(parameterSet, relevantPorts, output)));
            } else {
                //System.out.println(String.format("Port %d does not bring a new output", i));
            }

            if (finished(parameterSet, relevantPorts, output)) {
                break;
            }
        }
        System.out.println(clock.getDelta());
    }

    private static String toString(long[] input) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < input.length; i++) {
            sb.append(input[i]);

            if (i == input.length - 1) {
                sb.append("]");
            } else {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    private static boolean bringNewOutputVariation(FileTreeSet<Long> sortedOutput, long portId) {
        return !sortedOutput.contains(portId);
    }

    private static long[] searchForOutputImprovement(ParameterSet parameterSet, FileTreeSet<Long> relevantPorts, int portId, long[] originalOutput) {

        long[] newOutput = ArrayUtils.clone(originalOutput);
        long[] originalOutputPoints = new long[originalOutput.length];
        long[] newPortPoints = new long[originalOutput.length];

        int inputSize = parameterSet.getInputBitSize();

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (CircuitUtils.resolve(relevantPorts, input, originalOutput[j]) == output[j]) {
                    originalOutputPoints[j]++;
                }

                if (CircuitUtils.resolve(relevantPorts, input, portId) == output[j]) {
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
        int inputSize = parameterSet.getInputBitSize();
        int total = 0;

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (CircuitUtils.resolve(relevantPorts, input, originalOutput[j]) == output[j]) {
                    originalOutputPoints[j]++;
                }
            }
            total += output.length;
        }

        return String.format("%d out of %d", Arrays.stream(originalOutputPoints).sum(), total);
    }

    private static boolean finished(ParameterSet parameterSet, FileTreeSet<Long> relevantPorts, long[] originalOutput) {
        long[] originalOutputPoints = new long[originalOutput.length];
        int inputSize = parameterSet.getInputBitSize();
        int total = 0;

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (CircuitUtils.resolve(relevantPorts, input, originalOutput[j]) == output[j]) {
                    originalOutputPoints[j]++;
                }
            }
            total += output.length;
        }

        return total == Arrays.stream(originalOutputPoints).sum();
    }

    private static FileTreeSet<Long> createSortedOutputFileTreeSet(ParameterSet parameterSet, FileTreeSet<Long> relevantPorts) {
        try {
            return createSortedOutputFileTreeSet(
                    File.createTempFile("tree_", ".tmp"),
                    File.createTempFile("data_", ".tmp"),
                    parameterSet,
                    relevantPorts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileTreeSet<Long> createSortedOutputFileTreeSet(File treeFile, File dataFile, ParameterSet parameterSet, FileTreeSet<Long> relevantPorts) {

        Comparator<Long> comparator = (o1, o2) -> {
            int result = 0;
            int inputSize = parameterSet.getInputBitSize();

            for (int i = 0; i < parameterSet.getSampleCount(); i++) {
                boolean[] input = parameterSet.getInputSample(i);

                boolean b1 = CircuitUtils.resolve(relevantPorts, input, o1);
                boolean b2 = CircuitUtils.resolve(relevantPorts, input, o2);

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

        return new FileTreeSet<>(treeFile, dataFile, comparator, converter);
    }



    private static FileTreeSet<Long> createFileTreeSet() {
        try {
            return CircuitUtils.createLongFileTreeSet(File.createTempFile("tree_", ".tmp"), File.createTempFile("data_", ".tmp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
