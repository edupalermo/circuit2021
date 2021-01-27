package org.palermo.circuit.simplifier;

import org.junit.jupiter.api.Test;
import org.palermo.circuit.parameter.EnumParameter;
import org.palermo.circuit.parameter.Parameter;
import org.palermo.circuit.util.ByteArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EnumSimplifierTest {

    @Test
    void testConversion() {

        List<Parameter> list = Arrays.asList(
                EnumParameter.of("A"),
                EnumParameter.of("B"),
                EnumParameter.of("C"),
                EnumParameter.of("D"),
                EnumParameter.of("E"),
                EnumParameter.of("F"),
                EnumParameter.of("G"),
                EnumParameter.of("H")
            );

        EnumSimplifier simplifier = EnumSimplifier.of(list);

        for (Parameter item : list) {
            boolean[] simplifiedArray = simplifier.simplify(((EnumParameter) item).getRawData());
            System.out.println(String.format("%s - %s - %s",
                    ((EnumParameter) item).getRawData(),
                    ByteArrayUtils.toString(simplifiedArray),
                    simplifier.resolve(simplifiedArray)
                    ));
        }
    }

    @Test
    void testBitsSize() {
        for (int i = 1; i <= 100; i++) {

            List<Parameter> list = new ArrayList<>();

            for (int j = 0; j < i; j++) {
                list.add(EnumParameter.of(Integer.toString(j)));
            }
            EnumSimplifier simplifier = EnumSimplifier.of(list);


            for (Parameter item : list) {
                assertThat(simplifier.resolve(simplifier.simplify(((EnumParameter) item).getRawData()))).isEqualTo(((EnumParameter) item).getRawData());
            }
        }
    }
}
