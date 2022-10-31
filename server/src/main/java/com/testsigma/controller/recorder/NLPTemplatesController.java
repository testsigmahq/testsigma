package com.testsigma.controller.recorder;

import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.dto.NaturalTextActionsDTO;
import com.testsigma.mapper.NaturalTextActionMapper;
import com.testsigma.mapper.recorder.NLPTemplateMapper;
import com.testsigma.model.NaturalTextActions;
import com.testsigma.model.WorkspaceType;
import com.testsigma.model.recorder.NLPTemplateDTO;
import com.testsigma.service.NaturalTextActionsService;
import com.testsigma.service.ObjectMapperService;
import com.testsigma.specification.NaturalTextActionSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping(path = "/os_recorder/nlp_templates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class NLPTemplatesController {
    private final NaturalTextActionsService naturalTextActionsService;
    private final NaturalTextActionMapper naturalTextActionMapper;
    private final NLPTemplateMapper nlpTemplateMapper;
    private final ObjectMapperService objectMapperService;

    @GetMapping
    public Page<NLPTemplateDTO> index(NaturalTextActionSpecificationsBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.info("Request /os_recorder/nlp_templates");
        WorkspaceType workspaceType = null;
        String applicationType = null;
        for (SearchCriteria param : builder.params) {
            if (param.getKey().equals("applicationType")) {
                applicationType = param.getValue().toString();
            }
        }

        switch(applicationType) {
            case "MobileWeb":
                workspaceType = WorkspaceType.MobileWeb;
                break;
            case "WebApplication":
                workspaceType = WorkspaceType.WebApplication;
                break;
            case "AndroidNative":
                workspaceType = WorkspaceType.AndroidNative;
                break;
            case "IOSWeb":
                workspaceType = WorkspaceType.IOSWeb;
                break;
            case "IOSNative":
                workspaceType = WorkspaceType.IOSNative;
                break;
            case "Rest":
                workspaceType = WorkspaceType.Rest;
                break;
        }
        Page<NaturalTextActions> nlActions = naturalTextActionsService.findAllByWorkspaceType(workspaceType, pageable);
        List<NaturalTextActionsDTO> dtos = naturalTextActionMapper.mapDTO(nlActions.getContent());
        List<NLPTemplateDTO> results = new ArrayList<>();
        for(NaturalTextActionsDTO dto : dtos) {
            NLPTemplateDTO result = nlpTemplateMapper.mapDTO(dto);
            result.getData().setTestData(mapNLPTemplateTestData(dto.getData().getTestData()));
            results.add(result);
        }
        return new PageImpl<>(results, pageable, nlActions.getTotalElements());
    }

    private LinkedHashMap<String, String> mapNLPTemplateTestData(String data) {
        try {
            return (LinkedHashMap<String, String>) objectMapperService.parseJsonModel(data, Map.class);
        } catch (Exception e) {
            return new LinkedHashMap<>() {{
                put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA, data);
            }};
        }
    }
}