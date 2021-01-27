package org.palermo.circuit.converter;

public class CharConverter {

    public static boolean[] toBooleanArray(char c) {
        boolean[] answer = new boolean[Character.SIZE];

        for (int i = 0; i < Character.SIZE; i++) {
            answer[i] = (c >> (Character.SIZE - i - 1)  & 0x01) == 0x01;
        }

        return answer;
    }

    public static char toChar(boolean[] booleanArray) {
        if (booleanArray == null) {
            throw new RuntimeException("Cannot convert null to booleanArray");
        }
        if (booleanArray.length != Character.SIZE) {
            throw new RuntimeException(String.format("A boolean array of size %d cannot be converted to char", booleanArray.length));
        }

        char answer = 0x00;

        for (int i = 0; i < Character.SIZE; i++) {
            if (booleanArray[i]) {
                answer |= 0x01 << (Character.SIZE - i - 1);
            }
        }

        return answer;
    }
}
