package org.palermo.circuit.simplifier;

import org.junit.jupiter.api.Test;
import org.palermo.circuit.converter.CharConverter;
import org.palermo.circuit.parameter.CharParameter;
import org.palermo.circuit.parameter.Parameter;
import org.palermo.circuit.util.ByteArrayUtils;

import java.util.Arrays;
import java.util.List;

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
}
