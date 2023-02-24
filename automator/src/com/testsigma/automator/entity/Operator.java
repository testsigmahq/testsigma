package com.testsigma.automator.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Operator {
    CONTAINS(1, "contains"),
    STARTS_WITH(2, "Start With"),
    ENDS_WITH(3, "Ends with"),
    EQUALS(4, "Equals"),
    IN(5, "IN");

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
