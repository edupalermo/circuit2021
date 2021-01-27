package org.palermo.circuit.converter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CharConverterTest {

    @ParameterizedTest
    @MethodSource("scenarios")
    void testCharConverter(Character c) {
        assertThat(CharConverter.toChar(CharConverter.toBooleanArray(c.charValue()))).isEqualTo(c.charValue());
    }

    private static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of(Character.valueOf('a')),
                Arguments.of(Character.valueOf('b')),
                Arguments.of(Character.valueOf('c')),
                Arguments.of(Character.valueOf('d')),
                Arguments.of(Character.valueOf('ä')),
                Arguments.of(Character.valueOf('€')),
                Arguments.of(Character.valueOf('0')));
    }
}
