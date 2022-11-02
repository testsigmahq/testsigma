package com.testsigma.mapper.recorder;

import com.testsigma.dto.ElementDTO;
import com.testsigma.dto.ElementScreenNameDTO;
import com.testsigma.model.Element;
import com.testsigma.model.ElementMetaData;
import com.testsigma.model.ElementMetaDataRequest;
import com.testsigma.model.recorder.UiIdentifierDTO;
import com.testsigma.model.recorder.UiIdentifierRequest;
import com.testsigma.model.recorder.UiIdentifierScreenNameDTO;
import com.testsigma.model.recorder.UiIdentifierScreenNameRequest;
import com.testsigma.web.request.ElementRequest;
import com.testsigma.web.request.ElementScreenNameRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UiIdentifierMapper {

    @Mapping(target = "screenShotData", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "shadowParentId", ignore = true)
    @Mapping(target = "shadowParentElements", ignore = true)
    @Mapping(target = "shadowOrder", ignore = true)
    @Mapping(target = "screenShotURL", ignore = true)
    @Mapping(target = "screenShot", ignore = true)
    @Mapping(target = "isShadowDom", ignore = true)
    @Mapping(target = "isAdvanced", ignore = true)
    @Mapping(target = "fieldName", ignore = true)
    @Mapping(target = "elementSourceUrl", ignore = true)
    @Mapping(target = "autoHealingEnabled", ignore = true)
    @Mapping(source = "elementDTO.locatorValue", target = "definition")
    @Mapping(source = "elementDTO.workspaceVersionId", target = "applicationVersionId")
    @Mapping(target = "screenNameObj", expression = "java(mapScreenNameDTO(elementDTO.getScreenNameObj()))")
    UiIdentifierDTO mapDTO(ElementDTO elementDTO);

    List<UiIdentifierDTO> mapDTOs(List<ElementDTO> elementDTOs);

    @Mapping(source = "elementScreenNameDTO.workspaceVersionId", target = "applicationVersionId")
    UiIdentifierScreenNameDTO mapScreenNameDTO(ElementScreenNameDTO elementScreenNameDTO);

    List<UiIdentifierScreenNameDTO> mapScreenNameDTOs(List<ElementScreenNameDTO> elementScreenNameDTOs);

    @Mapping(source = "uiIdentifierRequest.applicationVersionId", target = "workspaceVersionId")
    @Mapping(source = "uiIdentifierRequest.definition", target = "locatorValue")
    ElementRequest mapRequest(UiIdentifierRequest uiIdentifierRequest);

    @Mapping(source = "applicationVersionId", target = "workspaceVersionId")
    ElementScreenNameRequest mapScreenNameRequest(UiIdentifierScreenNameRequest request);

    @Mapping(target = "currentElement", expression = "java(elementMetaDataRequest.getStringCurrentElement())")
    ElementMetaData map(ElementMetaDataRequest elementMetaDataRequest);

    ElementDTO map(Element element);

    //@Mapping(target = "screenNameObj", ignore = true)
    void merge(ElementRequest elementRequest, @MappingTarget Element element);

    //@Mapping(target = "screenNameObj", ignore = true)
    Element map(ElementRequest elementRequest);
}
