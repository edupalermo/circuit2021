package org.palermo.circuit;

import org.palermo.circuit.parameter.CharParameter;
import org.palermo.circuit.parameter.Direction;
import org.palermo.circuit.parameter.EnumParameter;
import org.palermo.circuit.parameter.ParameterSet;

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


        parameterSet.evaluate(CharParameter.of('a'));


    }
}
