package com.testsigma.controller.recorder;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestDataProfileMapper;
import com.testsigma.mapper.recorder.TestDataMapper;
import com.testsigma.model.TestData;
import com.testsigma.model.recorder.TestDataDTO;
import com.testsigma.service.TestDataProfileService;
import com.testsigma.specification.TestDataProfileSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/os_recorder/test_data", consumes = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestDataProfilesRecorderController {
    private final TestDataProfileService service;
    private final TestDataMapper testDataMapper;
    private final TestDataProfileMapper mapper;

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public TestDataDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        TestData testData = service.find(id);
        return testDataMapper.mapDTO(mapper.mapToDTO(testData));
    }

    @RequestMapping(method = RequestMethod.GET)
    public Page<TestDataDTO> index(TestDataProfileSpecificationsBuilder builder, Pageable pageable) {
        Specification<TestData> spec = builder.build();
        Page<TestData> testData = this.service.findAll(spec, pageable);
        List<TestDataDTO> testDataProfileDTOS = testDataMapper.mapDTOs(mapper.mapToDTO(testData.getContent()));
        return new PageImpl<>(testDataProfileDTOS, pageable, testData.getTotalElements());
    }
}
