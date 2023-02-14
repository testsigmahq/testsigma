package com.testsigma.mapper.recorder;

import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.dto.NaturalTextActionsDTO;
import com.testsigma.model.recorder.NLPTemplateDTO;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    NLPTemplateDTO mapDTO(NaturalTextActionsDTO naturalTextActionDataDTO);

    List<NLPTemplateDTO> mapDTOs(List<NaturalTextActionsDTO> naturalTextActionsDTOs);

    default String mapNaturalText(String naturalText) {
        return naturalText.replaceAll("#\\{element}", "#{ui-identifier}");
    }

    default Map<String, List> mapAllowedValues(List allowedValues) {
        return new HashMap<>() {{
           put("testData", allowedValues);
        }};
    }

    default List<NLPTemplateDTO> changeDataToCamelCase(List<NaturalTextActionsDTO> dtos) {
        List<NLPTemplateDTO> results = new ArrayList<>();
        for(NaturalTextActionsDTO dto : dtos) {
            NLPTemplateDTO result = mapDTO(dto);
            result.getData().setTestData(mapNLPTemplateTestData());
            String grammar = null;
            if(result.getGrammar().contains("#{ui-identifier}")) {
                grammar = result.getGrammar().replaceAll("#\\{ui-identifier}", "\\#{uiIdentifier}");
                result.setGrammar(grammar);
                result.getData().setUiIdentifier(NaturalTextActionConstants.TEST_STEP_KEY_UI_IDENTIFIER_RECORDER);
            }
            Pattern pattern = Pattern.compile("\\$\\{(.*?)}");
            Matcher matcher = pattern.matcher(result.getGrammar());
            while (matcher.find())
            {

                String allowedValues = matcher.group(1);
                if(allowedValues.equals("test-data1")) {
                    grammar = result.getGrammar().replaceAll("test-data1", "testData1");
                    result.getData().getTestData().put("testData1", "testData1");
                }
                else if(allowedValues.equals("test-data2")) {
                    grammar = result.getGrammar().replaceAll("test-data2", "testData2");
                    result.getData().getTestData().put("testData2", "testData2");
                }
                else if(allowedValues.equals("test-data")) {
                    grammar = result.getGrammar().replaceAll("test-data", "testData");
                    result.getData().getTestData().put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA_RECORDER, dto.getData().getTestData().get("test-data"));
                }
                result.setGrammar(grammar);
            }
            if(dto.getAllowedValues() != null && dto.getAllowedValues().containsKey("test-data"))
                result.setAllowedValues(mapAllowedValues(dto.getAllowedValues().get("test-data")));
            results.add(result);
        }
        return results;
    }

    default LinkedHashMap<String, String> mapNLPTemplateTestData() {
        return new LinkedHashMap<>() {{}};
    }

}
