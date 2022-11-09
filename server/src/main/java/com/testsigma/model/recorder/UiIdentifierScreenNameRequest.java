package com.testsigma.model.recorder;

import lombok.Data;

import java.io.Serializable;

@Data
public class UiIdentifierScreenNameRequest implements Serializable {
    private Long id;
    private Long applicationVersionId;
    private String name;
}