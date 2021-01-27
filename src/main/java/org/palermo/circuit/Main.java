package org.palermo.circuit;

import org.palermo.circuit.engine.NoName;
import org.palermo.circuit.parameter.*;
import org.palermo.circuit.util.FileTree;

public class Main {

    public static void main(String arg[]) {
        ParameterSet parameterSet = ParameterSet.create()
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
                .simplify();

        long[] output = null;
        FileTree relevantPorts = null;

        for (int i = 0; i < 100; i++) {
            if (NoName.isConnectedToSignificantPorts(relevantPorts, parameterSet, i) && bringNewOutputVariation(i)) {
                addToSignificantPorts(i);

                output = searchForOutputImprovement(i, output);
            }
        }
    }

    private static boolean bringNewOutputVariation(int i) {
        return true;
    }

    private static void addToSignificantPorts(int i) {
    }

    private static long[] searchForOutputImprovement(int i, long[] output) {
        return null;
    }
}
