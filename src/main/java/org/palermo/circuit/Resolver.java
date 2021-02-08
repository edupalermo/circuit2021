package org.palermo.circuit;

import org.palermo.circuit.parameter.ParameterSet;
import org.palermo.circuit.problem.VowelProblem;

public class Resolver {


    public static void main(String args[]) {

        ParameterSet parameterSet = VowelProblem.createParameterSet();

        System.out.println("Input Size: " + parameterSet.getInputBitSize());

        long[] output = new long[] {287902, 287774};

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
        evaluate(output, parameterSet, 'q');
        evaluate(output, parameterSet, 'r');
        evaluate(output, parameterSet, 's');
        evaluate(output, parameterSet, 't');
        evaluate(output, parameterSet, 'u');
        evaluate(output, parameterSet, 'v');
        evaluate(output, parameterSet, 'w');
        evaluate(output, parameterSet, 'x');
        evaluate(output, parameterSet, 'y');
        evaluate(output, parameterSet, 'z');
        evaluate(output, parameterSet, 'A');
        evaluate(output, parameterSet, 'B');
        evaluate(output, parameterSet, 'C');
        evaluate(output, parameterSet, 'D');
        evaluate(output, parameterSet, 'E');
        evaluate(output, parameterSet, 'F');
        evaluate(output, parameterSet, 'G');
        evaluate(output, parameterSet, 'H');
        evaluate(output, parameterSet, 'I');
        evaluate(output, parameterSet, 'J');
        evaluate(output, parameterSet, '-');
        evaluate(output, parameterSet, '=');
        evaluate(output, parameterSet, '"');
        evaluate(output, parameterSet, '*');
        evaluate(output, parameterSet, '+');
        evaluate(output, parameterSet, '$');
        evaluate(output, parameterSet, '&');
        evaluate(output, parameterSet, '0');
        evaluate(output, parameterSet, '1');
        evaluate(output, parameterSet, '2');
        evaluate(output, parameterSet, '3');
        evaluate(output, parameterSet, '4');
        evaluate(output, parameterSet, '5');
        evaluate(output, parameterSet, '6');
        evaluate(output, parameterSet, '7');
        evaluate(output, parameterSet, '8');
        evaluate(output, parameterSet, '9');
    }

    private static void evaluate(long[] output, ParameterSet parameterSet, char input) {
        System.out.println(String.format("Input: %s Output %s", input, parameterSet.evaluate(output, Character.valueOf(input)).get(0)));
    }
}
