package org.palermo.circuit;

import org.palermo.circuit.parameter.*;
import org.palermo.circuit.util.CircuitUtils;
import org.palermo.circuit.util.FileTreeSet;

import java.io.File;
import java.util.List;

public class Resolver {


    public static void main(String args[]) {

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

        long[] output = new long[] {115449, 1565789285};

        evaluate(output, parameterSet, 'a');
        evaluate(output, parameterSet, 'b');
        evaluate(output, parameterSet, 'c');
        evaluate(output, parameterSet, 'd');
        evaluate(output, parameterSet, 'e');
        evaluate(output, parameterSet, 'f');
        evaluate(output, parameterSet, 'g');
        evaluate(output, parameterSet, 'h');
        evaluate(output, parameterSet, 'i');
        evaluate(output, parameterSet, 'j');
        evaluate(output, parameterSet, 'k');
        evaluate(output, parameterSet, 'l');
        evaluate(output, parameterSet, 'm');
        evaluate(output, parameterSet, 'n');
        evaluate(output, parameterSet, 'o');
        evaluate(output, parameterSet, 'p');
        evaluate(output, parameterSet, 'A');
        evaluate(output, parameterSet, 'B');
        evaluate(output, parameterSet, 'C');
        evaluate(output, parameterSet, 'D');
        evaluate(output, parameterSet, 'E');
        evaluate(output, parameterSet, 'F');
        evaluate(output, parameterSet, '-');
        evaluate(output, parameterSet, '=');
        evaluate(output, parameterSet, '$');
        evaluate(output, parameterSet, '0');
        evaluate(output, parameterSet, '1');
        evaluate(output, parameterSet, '2');
        evaluate(output, parameterSet, '3');
        evaluate(output, parameterSet, '4');
        evaluate(output, parameterSet, '5');
        evaluate(output, parameterSet, '6');
    }

    private static void evaluate(long[] output, ParameterSet parameterSet, char input) {
        System.out.println(String.format("Input: %s Output %s", input, parameterSet.evaluate(output, Character.valueOf(input)).get(0)));
    }
}
