package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Operator {
    CONTAINS,
    STARTS_WITH,
    ENDS_WITH,
    EQUALS,
    IN;
}