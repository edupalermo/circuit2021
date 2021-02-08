package org.palermo.circuit.problem;

import org.palermo.circuit.parameter.CharParameter;
import org.palermo.circuit.parameter.Direction;
import org.palermo.circuit.parameter.EnumParameter;
import org.palermo.circuit.parameter.ParameterSet;

public class VowelProblem {

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
}
