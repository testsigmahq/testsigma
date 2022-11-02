package com.testsigma.mapper.recorder;

import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.NaturalTextActionsDTO;
import com.testsigma.model.recorder.KibbutzPluginNLPDTO;
import com.testsigma.model.recorder.NLPTemplateDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NLPTemplateMapper {

    @Mapping(target = "keyword", source = "displayName")
    @Mapping(target = "grammar", expression = "java(mapNaturalText(naturalTextActionDataDTO.getNaturalText()))")
    @Mapping(target = "deprecated", ignore = true)
    @Mapping(target = "applicationType", source = "workspaceType")
    @Mapping(target = "data.testData", ignore = true)
    @Mapping(target = "allowedValues", expression = "java(mapAllowedValues(naturalTextActionDataDTO.getAllowedValues()))")
    NLPTemplateDTO mapDTO(NaturalTextActionsDTO naturalTextActionDataDTO);

    List<NLPTemplateDTO> mapDTOs(List<NaturalTextActionsDTO> naturalTextActionsDTOs);

    default String mapNaturalText(String naturalText) {
        return naturalText.replaceAll("#\\{element}", "#{ui-identifier}");
    }

    default Map<String, List> mapAllowedValues(List allowedValues) {
        return new HashMap<>() {{
           put("test-data", allowedValues);
        }};
    }

}
