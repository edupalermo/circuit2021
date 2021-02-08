package org.palermo.circuit;

import org.palermo.circuit.clock.Clock;
import org.palermo.circuit.collection.FileOrderedSet;
import org.palermo.circuit.collection.OrderedSet;
import org.palermo.circuit.output.OutputContainer;
import org.palermo.circuit.parameter.CharParameter;
import org.palermo.circuit.parameter.Direction;
import org.palermo.circuit.parameter.EnumParameter;
import org.palermo.circuit.parameter.ParameterSet;
import org.palermo.circuit.problem.VowelProblem;
import org.palermo.circuit.util.CircuitUtils;

import java.lang.management.ManagementFactory;

public class Main {


    public static void main(String arg[]) {

        System.out.println(
                ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax()
        );

        ParameterSet parameterSet = VowelProblem.createParameterSet();

        Clock clock = Clock.start();

        //OrderedSet<Long> relevantPorts = CircuitUtils.createLongFileOrderedSet();
        //OrderedSet<Long> sortedOutput = CircuitUtils.createOutputFileOrderedSet(parameterSet);

        OrderedSet<Long> relevantPorts = CircuitUtils.createLongMemoryOrderedSet();
        OrderedSet<Long> sortedOutput = CircuitUtils.createOutputMemoryOrderedSet(parameterSet);

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

                if (i % 10000 == 0) {
                    System.out.println(String.format("Relevant Ports %d Output %s Progress %s - %s", relevantPorts.size(), outputContainer.getOutputAsString(), outputContainer.getProgress(), clock.getDelta()));
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

    private static long translateToPortId(OrderedSet<Long> relevantPorts, int inputSize, long portId) {
        if (portId < inputSize) {
            return portId;
        }
        long parents[] = CircuitUtils.getParentPorts(inputSize, portId);
        long translated = CircuitUtils.getPortIdByParentPortIds(inputSize, relevantPorts.select(parents[0]), relevantPorts.select(parents[1]));
        //System.out.println(String.format("Original %d Translated %d", portId, translated));
        return translated;
    }

    private static boolean connectedWithRelevantPorts(FileOrderedSet<Long> relevantPorts, int inputSize, long portId) {
        if (portId < inputSize) {
            return true;
        }
        long parents[] = CircuitUtils.getParentPorts(inputSize, portId);
        return relevantPorts.contains(parents[0]) && relevantPorts.contains(parents[1]);
    }

    private static boolean bringNewOutputVariation(OrderedSet<Long> sortedOutput, long portId) {
        return !sortedOutput.contains(portId);
    }
}
