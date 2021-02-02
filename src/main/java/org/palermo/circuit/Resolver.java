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

        long[] output = new long[] {387, 2888};
        FileTreeSet<Long> relevantPorts = CircuitUtils.createLongFileTreeSet(
                new File("C:\\temp\\relevant.tree"),
                new File("C:\\temp\\relevant.data"));

        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('a')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('b')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('c')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('d')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('e')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('f')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('g')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('h')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('i')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('j')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('k')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('-')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('=')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('0')).get(0));
        System.out.println(parameterSet.evaluate(output, relevantPorts, Character.valueOf('1')).get(0));
    }
}
