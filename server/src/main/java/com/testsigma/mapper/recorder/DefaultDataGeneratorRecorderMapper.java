package com.testsigma.mapper.recorder;

import com.testsigma.dto.DefaultDataGeneratorsDTO;
import com.testsigma.model.recorder.DefaultDataGeneartorRecorderFunctionDTO;
import org.mapstruct.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface DefaultDataGeneratorRecorderMapper {

    @Mapping(target = "arguments", expression = "java(mapArguments(dto))")
    DefaultDataGeneartorRecorderFunctionDTO mapDTO(DefaultDataGeneratorsDTO dto);

    List<DefaultDataGeneartorRecorderFunctionDTO> mapDTOs(List<DefaultDataGeneratorsDTO> dtos);

    default Map<String, String> mapArguments(DefaultDataGeneratorsDTO dto) {
        Map<String, String> arguments = new HashMap<>();
        for(String entry : dto.getArguments().keySet()) {
            arguments.put(entry, dto.getArguments().get(entry).toString());
        }
        return arguments;
    }
}
