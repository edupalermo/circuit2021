package org.palermo.circuit.parameter;

import org.palermo.circuit.simplifier.CharSimplifier;
import org.palermo.circuit.simplifier.EnumSimplifier;
import org.palermo.circuit.simplifier.Simplifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParameterSetBuilder {

    private final List<List<Parameter>> argumentList = new ArrayList<>();
    private List<Direction> directions;
    private List<Class<? extends Parameter>> classes;


    public ParameterSetBuilder configure(Direction ... directions) {
        if (classes != null) {
            if (classes.size() != directions.length) {
                throw new RuntimeException(String.format("The directions length [%d] must have the same length of existing classes [%d] definition",
                        directions.length, classes.size()));
            }
        }
        this.directions = Arrays.asList(directions);
        return this;
    }

    public ParameterSetBuilder configure(Class<? extends Parameter> ... parameterClass) {
        if (directions != null) {
            if (directions.size() != parameterClass.length) {
                throw new RuntimeException(String.format("The classes length [%d] must have the same length of existing directions [%d] definition",
                        parameterClass.length, directions.size()));
            }
        }
        this.classes = Arrays.asList(parameterClass);
        return this;
    }

    public ParameterSetBuilder add(Parameter ... parameters) {
        if (this.directions == null) {
            throw new RuntimeException("The directions were not configured");
        }

        if (this.classes == null) {
            throw new RuntimeException("The class types were not configured");
        }

        if (this.classes.size() != parameters.length) {
            throw new RuntimeException(String.format("Parameters [%d] with inconsistent size, the class definition demands %d parameters",
                    parameters.length, this.classes.size()));
        }

        if (argumentList.size() == 0) {
            for (int i = 0; i < parameters.length; i++) {
                argumentList.add(new ArrayList<>());
            }
        }
        for (int i = 0; i < parameters.length; i++) {
            if (!parameters[i].getClass().equals(classes.get(i))) {
                throw new RuntimeException(String.format("Parameter on index %d with type %s is supposed to be of type %s",
                        i, parameters[i].getClass().getName(), classes.get(i).getName()));
            }
            argumentList.get(i).add(parameters[i]);
        }
        return this;
    }

    public ParameterSet build() {
        List<Simplifier> simplifiers = new ArrayList<Simplifier>();

        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).equals(CharParameter.class)) {
                simplifiers.add(CharSimplifier.of(argumentList.get(i)));
            }
            else if (classes.get(i).equals(EnumParameter.class)) {
                simplifiers.add(EnumSimplifier.of(argumentList.get(i)));
            }
            else {
                throw new RuntimeException("Not implemented for " + classes.get(i).getName());
            }
        }

        return new ParameterSet(simplifiers, directions, classes, argumentList);
    }
}
