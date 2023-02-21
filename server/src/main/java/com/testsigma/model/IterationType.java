
package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IterationType {
    INDEX,
    SET_NAME,
    PARAMETER_VALUE,
    VALUE_TYPE;
}