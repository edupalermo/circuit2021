package org.palermo.circuit.simplifier;

import org.palermo.circuit.parameter.EnumParameter;
import org.palermo.circuit.parameter.Parameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnumSimplifier implements Simplifier<String> {

    private final List<String> uniqueItems;

    private EnumSimplifier(List<String> uniqueItems) {
        this.uniqueItems = uniqueItems;
    }

    public static EnumSimplifier of(List<Parameter> list) {
        if (list == null) {
            throw new RuntimeException("Cannot create a simplifier with an empty list");
        }
        if (list.size() == 0) {
            throw new RuntimeException("Cannot create a simplifier with an empty list");
        }

        List<String> uniqueItems = new ArrayList<>();

        for (Parameter item : list) {
            int pos = Collections.binarySearch(uniqueItems, ((EnumParameter) item).getRawData());
            if (pos < 0) {
                uniqueItems.add(~pos, ((EnumParameter) item).getRawData());
                // System.out.println((-pos - 1) + " - " + (~pos));
            }
        }

        return new EnumSimplifier(uniqueItems);
    }


    @Override
    public int getSize() {
        return (int) Math.ceil(Math.log(uniqueItems.size()) / Math.log(2));
    }

    @Override
    public boolean[] simplify(String input) {
        int position = Collections.binarySearch(uniqueItems, input);

        if (position < 0) {
            throw new RuntimeException(String.format("The input value [%s] dos not exist in the enum values", input));
        }

        return toBooleanArray(position);
    }

    @Override
    public String resolve(boolean[] input) {

        int position = toInt(input);

        if (position >= uniqueItems.size()) {
            return "UNKNOWN";
        }

        return uniqueItems.get(position);
    }

    private boolean[] toBooleanArray(int position) {
        boolean[] answer = new boolean[this.getSize()];

        for (int i = 0; i < this.getSize(); i++) {
            answer[i] = (position >> (this.getSize() - i - 1)  & 0x01) == 0x01;
        }

        return answer;
    }

    private int toInt(boolean[] booleanArray) {
        if (booleanArray == null) {
            throw new RuntimeException("Cannot convert null to booleanArray");
        }
        if (booleanArray.length == 0) {
            return 0;
        }

        int answer = 0x00;

        for (int i = booleanArray.length - 1; i >= 0; i--) {
            if (booleanArray[i]) {
                answer |= 0x01 << (booleanArray.length - i - 1);
            }
        }

        return answer;
    }
}
