package com.testsigma.mapper.recorder;

import java.util.List;
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
    @Mapping(target = "applicationType", source = "workspaceType")
    @Mapping(target = "grammar", source = "naturalText")
    @Mapping(target = "parameters", expression = "java(mapPluginNLPParameterDTO(addonNaturalTextActionDTO.getParameters()))")
    KibbutzPluginNLPDTO mapDTO(AddonNaturalTextActionDTO addonNaturalTextActionDTO);

    List<KibbutzPluginNLPDTO> mapDTOs(List<AddonNaturalTextActionDTO> addonNaturalTextActionDTOs);


    KibbutzPluginNLPParameterDTO mapPluginNLPParameterDTO(AddonNaturalTextActionParameterDTO addonNaturalTextActionParameterDTO);

    List<KibbutzPluginNLPParameterDTO> mapPluginNLPParameterDTOs(List<AddonNaturalTextActionParameterDTO> addonNaturalTextActionParameterDTOs);*/

    @Mapping(source = "addonId", target = "pluginId")
    KibbutzPluginTestDataFunctionDTO mapPluginTestDataFunctionDTO(AddonPluginTestDataFunctionDTO addonPluginTestDataFunctionDTO);

    List<KibbutzPluginTestDataFunctionDTO> mapPluginTestDataFunctionDTOs(List<AddonPluginTestDataFunctionDTO> addonPluginTestDataFunctionDTOs);
}
