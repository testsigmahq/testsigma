package com.testsigma.mapper;

import com.testsigma.automator.entity.ForLoopConditionsEntity;
import com.testsigma.dto.ForLoopConditionDTO;
import com.testsigma.dto.export.ForLoopConditionXMLDTO;
import com.testsigma.model.ForLoopCondition;
import com.testsigma.model.TestCaseFilter;
import com.testsigma.web.request.ForLoopConditionRequest;
import com.testsigma.web.request.TestCaseFilterRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ForLoopConditionsMapper {

    void merge(@MappingTarget ForLoopCondition forLoopCondition, ForLoopConditionRequest request);

    List<ForLoopConditionDTO> map(List<ForLoopCondition> models);

    List<ForLoopConditionXMLDTO> mapDtos(List<ForLoopCondition> models);

    List<ForLoopCondition> mapEntities(List<ForLoopConditionXMLDTO> dtos);

    ForLoopConditionDTO map(ForLoopCondition model);

    ForLoopCondition copy(ForLoopCondition model);

    ForLoopCondition map(ForLoopConditionRequest request);
}