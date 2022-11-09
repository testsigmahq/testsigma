package com.testsigma.controller.recorder;

import com.testsigma.dto.DefaultDataGeneratorsDTO;
import com.testsigma.mapper.DefaultDataGeneratorMapper;
import com.testsigma.model.DefaultDataGenerator;
import com.testsigma.service.DefaultDataGeneratorService;
import com.testsigma.specification.DefaultDataGeneratorSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/os_recorder/custom_functions")
public class DefaultDataRecorderGeneratorController {

    private final DefaultDataGeneratorMapper testDataFunctionMapper;
    private final DefaultDataGeneratorService defaultDataGeneratorService;

    @GetMapping
    public Page<DefaultDataGeneratorsDTO> index(DefaultDataGeneratorSpecificationsBuilder builder, Pageable pageable) {
        Page<DefaultDataGenerator> testDataFunctions = defaultDataGeneratorService.findAll(pageable);
        List<DefaultDataGeneratorsDTO> dtos = testDataFunctionMapper.mapToDTO(testDataFunctions.getContent());
        return new PageImpl<>(dtos, pageable, testDataFunctions.getTotalElements());
    }

    @GetMapping(path = "/{id}")
    public DefaultDataGeneratorsDTO show(@PathVariable(value = "id") Long id) throws Exception {
        return testDataFunctionMapper.mapToDTO(defaultDataGeneratorService.find(id));
    }
}
