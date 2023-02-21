package com.testsigma.automator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IterationType {
    INDEX(1, "Index"),
    SET_NAME(2, "Set Name"),
    PARAMETER_VALUE(3, "Parameter Value"),
    VALUE_TYPE(4, "Value Type");


    private final Integer id;

    private final String name;

    public static IterationType getById(Integer id) {
        for (IterationType type : values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }
}
