package com.testsigma.mapper;


import com.testsigma.dto.SuggestionDTO;
import com.testsigma.automator.suggestion.entity.SuggestionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SuggestionMapper {
  List<SuggestionEntity> map(List<SuggestionDTO> suggestionDTOS);
}
