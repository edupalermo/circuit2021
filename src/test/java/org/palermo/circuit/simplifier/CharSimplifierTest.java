package org.palermo.circuit.simplifier;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.palermo.circuit.converter.CharConverter;
import org.palermo.circuit.parameter.CharParameter;
import org.palermo.circuit.parameter.Parameter;
import org.palermo.circuit.util.ByteArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

class CharSimplifierTest {

    @Test
    void test() {

        List<Parameter> list = Arrays.asList(
                CharParameter.of('a'),
                CharParameter.of('b'),
                CharParameter.of('c'),
                CharParameter.of('z'),
                CharParameter.of('0'),
                CharParameter.of('ä')
            );

        CharSimplifier simplifier = CharSimplifier.of(list);

        for (Parameter c : list) {
            boolean[] simplifiedArray = simplifier.simplify(((CharParameter) c).getRawData());
            System.out.println(String.format("%s -  %s %s - %s", ((CharParameter) c).getRawData(),
                    ByteArrayUtils.toString(CharConverter.toBooleanArray(((CharParameter) c).getRawData())),
                    ByteArrayUtils.toString(simplifiedArray),
                    simplifier.resolve(simplifiedArray)
                    ));
        }
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of(new char[] {0x00, 0x01}, 1),
                Arguments.of(new char[] {0x00, 0x01, 0x02}, 2),
                Arguments.of(new char[] {0x00, 0x01, 0x02, 0x03}, 2),
                Arguments.of(new char[] {0x00, 0x01, 0x02, 0x03, 0x04}, 3));
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    void testSize(char[] array, int size) {
        List<Parameter> list = new ArrayList<>();
        for (char c : array) {
            list.add(CharParameter.of(c));
        }

        CharSimplifier charSimplifier = CharSimplifier.of(list);
        assertThat(charSimplifier.getSize()).isEqualTo(size);
    }
}
