package org.palermo.circuit.util;

public class ByteArrayUtils {
    public static String toString(boolean[] input) {
        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for (int i = 0; i < input.length; i++) {
            if (input[i]) {
                sb.append("1");
            }
            else {
                sb.append("0");
            }
            if (i == input.length - 1) {
                sb.append("]");
            }
        }

        return sb.toString();
    }
}
