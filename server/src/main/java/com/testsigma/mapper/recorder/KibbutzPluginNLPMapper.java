package com.testsigma.mapper.recorder;

import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.AddonNaturalTextActionParameterDTO;
import com.testsigma.dto.AddonPluginTestDataFunctionDTO;
import com.testsigma.model.recorder.KibbutzPluginNLPDTO;
import com.testsigma.model.recorder.KibbutzPluginNLPParameterDTO;
import com.testsigma.model.recorder.KibbutzPluginTestDataFunctionDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface KibbutzPluginNLPMapper {
/*
    @Mapping(source = "workspaceType", target = "applicationType")
    @Mapping(source = "naturalText", target = "grammar")
    KibbutzPluginNLPDTO mapDTO(AddonNaturalTextActionDTO addonNaturalTextActionDTO);

    KibbutzPluginNLPParameterDTO mapPluginNLPParameterDTO(AddonNaturalTextActionParameterDTO addonNaturalTextActionParameterDTO);

    @Mapping(source = "addonId", target = "pluginId")
    KibbutzPluginTestDataFunctionDTO mapPluginTestDataFunctionDTO(AddonPluginTestDataFunctionDTO addonPluginTestDataFunctionDTO);*/
}
