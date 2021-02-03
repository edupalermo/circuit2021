package org.palermo.circuit.simplifier;

import org.apache.commons.lang3.ArrayUtils;
import org.palermo.circuit.converter.CharConverter;
import org.palermo.circuit.parameter.CharParameter;
import org.palermo.circuit.parameter.Parameter;

import java.util.List;
import java.util.TreeSet;

public class CharSimplifier implements Simplifier<Character> {

    private final boolean[] mask;
    private final int[] relevantBits;

    private CharSimplifier(boolean[] mask, int[] relevantBits) {
        this.mask = mask;
        this.relevantBits = relevantBits;
    }

    public static CharSimplifier of(List<Parameter> list) {
        if (list == null) {
            throw new RuntimeException("Cannot create a simplifier with an empty list");
        }
        if (list.size() == 0) {
            throw new RuntimeException("Cannot create a simplifier with an empty list");
        }

        boolean mask[] = CharConverter.toBooleanArray(((CharParameter) list.get(0)).getRawData());

        int[] relevantBits = new int[0];

        if (list.size() > 1) {

            TreeSet<Integer> hasDifference = new TreeSet<>();
            for (int i = 1; i < list.size(); i++) { // goes through all parameters, except the first = mask
                for (int j = 0; j < Character.SIZE; j++) {
                    if (!hasDifference.contains(j)) {
                        if (CharConverter.toBooleanArray(((CharParameter) list.get(i)).getRawData())[j] != mask[j]) {
                            hasDifference.add(j);
                        }
                    }
                }
            }

            relevantBits = ArrayUtils.toPrimitive(hasDifference.toArray(new Integer[] {}));
        }
        return new CharSimplifier(mask, relevantBits);
    }

    @Override
    public int getSize() {
        return this.relevantBits.length;
    }

    @Override
    public boolean[] simplify(Character c) {
        boolean[] answer = new boolean[this.relevantBits.length];
        boolean[] input = CharConverter.toBooleanArray(c);

        for (int i = 0; i < this.relevantBits.length; i++) {
            answer[i] = input[relevantBits[i]];
        }

        return answer;
    }

    @Override
    public Character resolve(boolean[] input) {
        boolean[] answer = new boolean[Character.SIZE];

        int j = 0;
        for (int i = 0; i < Character.SIZE; i++) {
            if ((j < relevantBits.length) && (relevantBits[j] == i)) {
                answer[i] = input[j++];
            }
            else {
                answer[i] = mask[i];
            }
        }

        return CharConverter.toChar(answer);
    }
}
