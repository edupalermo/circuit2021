package org.palermo.circuit;

import org.apache.commons.lang3.ArrayUtils;
import org.palermo.circuit.clock.Clock;
import org.palermo.circuit.output.OutputContainer;
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

    public static ParameterSet createParameterSet() {
        return ParameterSet.builder()
                .configure(CharParameter.class, EnumParameter.class)
                .configure(Direction.INPUT, Direction.OUTPUT)
                .add(CharParameter.of('a'), EnumParameter.of("VOWEL"))
                .add(CharParameter.of('b'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('c'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('d'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('e'), EnumParameter.of("VOWEL"))
                .add(CharParameter.of('f'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('g'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('h'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('i'), EnumParameter.of("VOWEL"))
                .add(CharParameter.of('j'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('k'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('l'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('m'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('n'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('o'), EnumParameter.of("VOWEL"))
                .add(CharParameter.of('p'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('A'), EnumParameter.of("VOWEL"))
                .add(CharParameter.of('B'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('C'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('D'), EnumParameter.of("CONSONANT"))
                .add(CharParameter.of('-'), EnumParameter.of("SYMBOL"))
                .add(CharParameter.of('='), EnumParameter.of("SYMBOL"))
                .add(CharParameter.of('"'), EnumParameter.of("SYMBOL"))
                .add(CharParameter.of('&'), EnumParameter.of("SYMBOL"))
                .add(CharParameter.of('*'), EnumParameter.of("SYMBOL"))
                .add(CharParameter.of('+'), EnumParameter.of("SYMBOL"))
                .add(CharParameter.of('0'), EnumParameter.of("NUMBER"))
                .add(CharParameter.of('1'), EnumParameter.of("NUMBER"))
                .add(CharParameter.of('2'), EnumParameter.of("NUMBER"))
                .add(CharParameter.of('4'), EnumParameter.of("NUMBER"))
                .add(CharParameter.of('9'), EnumParameter.of("NUMBER"))
                .build();
    }

    public static void main(String arg[]) {
        ParameterSet parameterSet = createParameterSet();

        Clock clock = Clock.start();

        FileTreeSet<Long> relevantPorts = CircuitUtils.createLongFileTreeSet();
        FileTreeSet<Long> sortedOutput = createSortedOutputFileTreeSet(
                parameterSet,
                relevantPorts);

        OutputContainer outputContainer = OutputContainer.of(parameterSet);

        for (long i = 0; i < Long.MAX_VALUE; i++) {
            long portId = translateToPortId(relevantPorts, parameterSet.getInputBitSize(), i);
            //System.out.println("Port translated " + partial.getDelta());
            //if (connectedWithRelevantPorts(relevantPorts, parameterSet.getInputBitSize(), portId)) {
                //System.out.println("Port relevant " + partial.getDelta());
                if (bringNewOutputVariation(sortedOutput, portId)) {
                    //System.out.println("Port bring new output " + partial.getDelta());
                    relevantPorts.add(portId);
                    sortedOutput.add(portId);
                    if (outputContainer.evaluate(portId)) {
                        System.out.println(String.format("Relevant Ports %d Output %s Progress %s - %s", relevantPorts.size(), outputContainer.getOutputAsString(), outputContainer.getProgress(), clock.getDelta()));
                    }
                    //System.out.println("Searched for improvement " + partial.getDelta());
                } else {
                    //System.out.println("Port bring does not bring new output " + partial.getDelta());
                }

                if (outputContainer.finished()) {
                    break;
                }
            //}
            //else {
            //    System.out.println("Port not relevant " + partial.getDelta());
            //}
            //System.out.println("Last Log " + partial.getDelta());
        }
        System.out.println(clock.getDelta());
    }

    private static long translateToPortId(FileTreeSet<Long> relevantPorts, int inputSize, long portId) {
        if (portId < inputSize) {
            return portId;
        }
        long parents[] = CircuitUtils.getParentPorts(inputSize, portId);
        long translated = CircuitUtils.getPortIdByParentPortIds(inputSize, relevantPorts.select(parents[0]), relevantPorts.select(parents[1]));
        //System.out.println(String.format("Original %d Translated %d", portId, translated));
        return translated;
    }

    private static boolean connectedWithRelevantPorts(FileTreeSet<Long> relevantPorts, int inputSize, long portId) {
        if (portId < inputSize) {
            return true;
        }
        long parents[] = CircuitUtils.getParentPorts(inputSize, portId);
        return relevantPorts.contains(parents[0]) && relevantPorts.contains(parents[1]);
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

    private static long[] searchForOutputImprovement(ParameterSet parameterSet, long portId, long[] originalOutput) {

        long[] newOutput = ArrayUtils.clone(originalOutput);
        long[] originalOutputPoints = new long[originalOutput.length];
        long[] newPortPoints = new long[originalOutput.length];

        for (int i = 0; i < parameterSet.getSampleCount(); i++) {
            boolean[] input = parameterSet.getInputSample(i);
            boolean[] output = parameterSet.getOutputSample(i);

            for (int j = 0; j < output.length; j++) {
                if (CircuitUtils.resolve(input, originalOutput[j]) == output[j]) {
                    originalOutputPoints[j]++;
                }

                if (CircuitUtils.resolve(input, portId) == output[j]) {
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
                if (CircuitUtils.resolve(input, originalOutput[j]) == output[j]) {
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
                if (CircuitUtils.resolve(input, originalOutput[j]) == output[j]) {
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
