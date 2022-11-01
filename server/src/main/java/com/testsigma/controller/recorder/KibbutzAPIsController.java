package com.testsigma.controller.recorder;

import com.testsigma.dto.AddonNaturalTextActionDTO;
import com.testsigma.dto.AddonPluginTestDataFunctionDTO;
import com.testsigma.mapper.AddonMapper;
import com.testsigma.mapper.recorder.KibbutzPluginNLPMapper;
import com.testsigma.model.AddonNaturalTextAction;
import com.testsigma.model.AddonPluginTestDataFunction;
import com.testsigma.model.recorder.KibbutzPluginNLPDTO;
import com.testsigma.model.recorder.KibbutzPluginTestDataFunctionDTO;
import com.testsigma.service.AddonNaturalTextActionService;
import com.testsigma.service.AddonPluginTestDataFunctionService;
import com.testsigma.specification.AddonNaturalTextActionSpecificationsBuilder;
import com.testsigma.specification.AddonPluginTestDataFunctionSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = {"/os_recorder/kibbutz"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KibbutzAPIsController {
    private final KibbutzPluginNLPMapper kibbutzPluginNLPMapper;
    private final AddonMapper mapper;
    private final AddonPluginTestDataFunctionService testDataFunctionService;
    private final AddonNaturalTextActionService service;

    @GetMapping(path = "/test_data_functions")
    public Page<KibbutzPluginTestDataFunctionDTO> testDataFunctions(AddonPluginTestDataFunctionSpecificationBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.debug("GET /addon/test_data_functions");
        Specification<AddonPluginTestDataFunction> spec = builder.build();
        Page<AddonPluginTestDataFunction> actions = testDataFunctionService.findAll(spec, pageable);
        List<KibbutzPluginTestDataFunctionDTO> dtos = kibbutzPluginNLPMapper.mapPluginTestDataFunctionDTOs(mapper.mapTDFToDTO(actions.getContent()));
        return new PageImpl<>(dtos, pageable, actions.getTotalElements());
    }

    @GetMapping(path = "/nlps")
    public Page<KibbutzPluginNLPDTO> actions(AddonNaturalTextActionSpecificationsBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
        log.debug("GET /addon/actions");
        Specification<AddonNaturalTextAction> spec = builder.build();
        Page<AddonNaturalTextAction> actions = service.findAll(spec, pageable);
        List<AddonNaturalTextActionDTO> dtos = mapper.mapToDTO(actions.getContent());
        //List<KibbutzPluginNLPDTO> results = kibbutzPluginNLPMapper.mapKibbutzPluginNLPDTOs(dtos);
        List<KibbutzPluginNLPDTO> results = new ArrayList<>();
        return new PageImpl<>(results, pageable, actions.getTotalElements());
    }
}
