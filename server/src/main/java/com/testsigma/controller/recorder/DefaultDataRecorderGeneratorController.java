package com.testsigma.controller.recorder;

import com.testsigma.dto.DefaultDataGeneratorsDTO;
import com.testsigma.mapper.DefaultDataGeneratorMapper;
import com.testsigma.mapper.recorder.DefaultDataGeneratorRecorderMapper;
import com.testsigma.model.DefaultDataGenerator;
import com.testsigma.model.recorder.DefaultDataGeneartorRecorderFunctionDTO;
import com.testsigma.service.DefaultDataGeneratorService;
import com.testsigma.specification.DefaultDataGeneratorSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/os_recorder/default_data_generators")
public class DefaultDataRecorderGeneratorController {

    private final DefaultDataGeneratorMapper testDataFunctionMapper;
    private final DefaultDataGeneratorService defaultDataGeneratorService;
    private final DefaultDataGeneratorRecorderMapper defaultDataGeneratorRecorderMapper;

    @GetMapping
    public Page<DefaultDataGeneartorRecorderFunctionDTO> index(DefaultDataGeneratorSpecificationsBuilder builder, Pageable pageable) {
        Specification<DefaultDataGenerator> specification = builder.build();
        Page<DefaultDataGenerator> testDataFunctions = defaultDataGeneratorService.findAll(specification, pageable);
        List<DefaultDataGeneratorsDTO> dtos = testDataFunctionMapper.mapToDTO(testDataFunctions.getContent());
        List<DefaultDataGeneartorRecorderFunctionDTO> results = defaultDataGeneratorRecorderMapper.mapDTOs(dtos);
        return new PageImpl<>(results, pageable, testDataFunctions.getTotalElements());
    }

    @GetMapping(path = "/{id}")
    public DefaultDataGeneartorRecorderFunctionDTO show(@PathVariable(value = "id") Long id) throws Exception {
        DefaultDataGeneratorsDTO defaultDataGeneratorsDTO = testDataFunctionMapper.mapToDTO(defaultDataGeneratorService.find(id));
        return defaultDataGeneratorRecorderMapper.mapDTO(defaultDataGeneratorsDTO);
    }
}
