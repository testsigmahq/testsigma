package com.testsigma.mapper.recorder;

import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.NaturalTextActionsDTO;
import com.testsigma.model.recorder.KibbutzPluginNLPDTO;
import com.testsigma.model.recorder.NLPTemplateDTO;

import java.util.List;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NLPTemplateMapper {

    @Mapping(target = "keyword", source = "displayName")
    @Mapping(target = "grammar", source = "naturalText")
    @Mapping(target = "deprecated", ignore = true)
    @Mapping(target = "applicationType", source = "workspaceType")
    @Mapping(target = "data.testData", ignore = true)
    NLPTemplateDTO mapDTO(NaturalTextActionsDTO naturalTextActionDataDTO);

    List<NLPTemplateDTO> mapDTOs(List<NaturalTextActionsDTO> naturalTextActionsDTOs);

}
