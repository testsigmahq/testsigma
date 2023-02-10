package com.testsigma.mapper.recorder;

import java.util.List;
import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.AddonNaturalTextActionParameterDTO;
import com.testsigma.dto.AddonPluginTestDataFunctionDTO;
import com.testsigma.model.WorkspaceType;
import com.testsigma.model.recorder.KibbutzPluginNLPDTO;
import com.testsigma.model.recorder.KibbutzPluginNLPParameterDTO;
import com.testsigma.model.recorder.KibbutzPluginTestDataFunctionDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface KibbutzPluginNLPMapper {

    @Mapping(target = "applicationType", source = "workspaceType")
    @Mapping(target = "parameters", expression = "java(mapNLPParameter(dto.getParameters()))")
    @Mapping(target = "grammar", source = "naturalText")
    KibbutzPluginNLPDTO mapKibbutzPluginNLPDTOs(AddonNaturalTextActionDTO dto);

    List<KibbutzPluginNLPDTO> mapKibbutzPluginNLPDTOs(List<AddonNaturalTextActionDTO> dtos);

    KibbutzPluginNLPParameterDTO mapNLPParameter(AddonNaturalTextActionParameterDTO parameter);

    List<KibbutzPluginNLPParameterDTO> mapNLPParameter(List<AddonNaturalTextActionParameterDTO> parameters);

    @Mapping(source = "addonId", target = "pluginId")
    KibbutzPluginTestDataFunctionDTO mapPluginTestDataFunctionDTO(AddonPluginTestDataFunctionDTO addonPluginTestDataFunctionDTO);

    List<KibbutzPluginTestDataFunctionDTO> mapPluginTestDataFunctionDTOs(List<AddonPluginTestDataFunctionDTO> addonPluginTestDataFunctionDTOs);
}
