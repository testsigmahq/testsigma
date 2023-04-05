package com.testsigma.automator.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Operator {
    CONTAINS(1, "contains"),
    STARTS_WITH(2, "start with"),
    ENDS_WITH(3, "ends with"),
    EQUALS(4, "equals"),
    IN(5, "in"),
    IS_EMPTY(6, "is empty"),
    IS_NOT_EMPTY(7, "is not empty"),
    BETWEEN(8, "between"),
    GREATER_THAN(9, "greater than"),
    LESS_THAN(10, "less than"),
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
}
