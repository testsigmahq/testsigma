package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public enum Operator {
    CONTAINS(1, "contains"),
    STARTS_WITH(2, "startsWith"),
    ENDS_WITH(3, "endsWith"),
    EQUALS(4, "equals"),
    IN(5, "in"),
    IS_EMPTY(6, "isEmpty"),
    IS_NOT_EMPTY(7, "isNotEmpty"),
    BETWEEN(8, "between"),
    GREATER_THAN(9, "greaterThan"),
    LESS_THAN(10, "lessThan"),
    NONE(11, "contains/startwith/Endswith/Equals/IN");


    private final Integer id;

    private final String name;

    public static Operator getById(Integer id) {
        for (Operator type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }

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