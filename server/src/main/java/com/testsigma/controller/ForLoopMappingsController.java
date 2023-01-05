package com.testsigma.controller;

import com.testsigma.dto.ForLoopConditionDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.ForLoopConditionsMapper;
import com.testsigma.model.ForLoopCondition;
import com.testsigma.service.ForLoopConditionService;
import com.testsigma.specification.ForLoopSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/for_loop_conditions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ForLoopMappingsController {

    private final ForLoopConditionService service;
    private final ForLoopConditionsMapper mapper;

    @RequestMapping(method = RequestMethod.GET)
    public Page<ForLoopConditionDTO> index(ForLoopSpecificationsBuilder builder,
                                           Pageable pageable) throws ResourceNotFoundException {
        log.debug("GET /for_loop_conditions ");
        Specification<ForLoopCondition> spec = builder.build();
        Page<ForLoopCondition> overriddenMappings = this.service.findAll(spec, pageable);
        List<ForLoopConditionDTO> testDataDTOS =
                mapper.map(overriddenMappings.getContent());
        return new PageImpl<>(testDataDTOS, pageable, overriddenMappings.getTotalElements());
    }
}
