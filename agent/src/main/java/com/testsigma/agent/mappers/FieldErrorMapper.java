/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.mappers;

import com.testsigma.agent.dto.FieldErrorDTO;
import com.testsigma.agent.dto.ObjectErrorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface FieldErrorMapper {
  List<FieldErrorDTO> map(List<FieldError> fieldError);

  List<ObjectErrorDTO> mapObjectErrors(List<ObjectError> objectErrors);
}
