package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum Operator {
    CONTAINS,
    STARTS_WITH,
    ENDS_WITH,
    EQUALS,
    IN,
    IS_EMPTY,
    IS_NOT_EMPTY,
    BETWEEN,
    GREATER_THAN,
    LESS_THAN,
    NONE;

    public static List<Operator> getForLoopOperatorsExceptEmpty() {
        List<Operator> excludedOps = List.of(IS_EMPTY, IS_NOT_EMPTY, BETWEEN, GREATER_THAN, LESS_THAN);
        List<Operator> operators = new ArrayList<>();
        for (Operator type : values()) {
            if (!excludedOps.contains(type)) {
                operators.add(type);
            }
        }
        return operators;
    }

    public static List<Operator> getForLoopOperatorsEmpty() {
        return List.of(IS_EMPTY, IS_NOT_EMPTY);
    }
}