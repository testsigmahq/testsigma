package com.testsigma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopFailuresDTO {
    Long count;
    String message;
}
