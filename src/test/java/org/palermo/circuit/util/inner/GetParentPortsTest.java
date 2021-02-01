package org.palermo.circuit.util.inner;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class GetParentPortsTest {

    private static Stream<Arguments> scenarios() {
        return Stream.of(
                Arguments.of(0L, 0L, 0L),
                Arguments.of(1l, 0L, 1L),
                Arguments.of(2L, 1L, 1L),
                Arguments.of(3L, 0L, 2L),
                Arguments.of(4L, 1L, 2L),
                Arguments.of(5L, 2L, 2L),
                Arguments.of(6L, 0L, 3L),
                Arguments.of(7L, 1L, 3L),
                Arguments.of(8L, 2L, 3L),
                Arguments.of(9L, 3L, 3L),
                Arguments.of(10L, 0L, 4L),
                Arguments.of(11L, 1L, 4L),
                Arguments.of(12L, 2L, 4L),
                Arguments.of(13L, 3L, 4L),
                Arguments.of(14L, 4L, 4L),
                Arguments.of(15L, 0L, 5L),
                Arguments.of(16L, 1L, 5L),
                Arguments.of(17L, 2L, 5L),
                Arguments.of(18L, 3L, 5L),
                Arguments.of(19L, 4L, 5L),
                Arguments.of(20L, 5L, 5L),
                Arguments.of(21L, 0L, 6L),
                Arguments.of(22L, 1L, 6L));
    }

    @ParameterizedTest
    @MethodSource("scenarios")
    void test(long portId, long left, long right) {
        long[] answer = GetParentPorts.getParentPorts(portId);
        assertThat(answer[0]).isEqualTo(left);
        assertThat(answer[1]).isEqualTo(right);
    }
}
