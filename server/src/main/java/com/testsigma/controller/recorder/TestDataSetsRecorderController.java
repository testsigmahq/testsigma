package com.testsigma.controller.recorder;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestDataProfileMapper;
import com.testsigma.mapper.recorder.TestDataMapper;
import com.testsigma.model.TestData;
import com.testsigma.model.TestDataSet;
import com.testsigma.model.recorder.TestDataSetDTO;
import com.testsigma.service.TestDataProfileService;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.TestDataSetSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/os_recorder/test_data_sets")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestDataSetsRecorderController {
    private final TestDataMapper testDataMapper;
    private final TestDataProfileService testDataProfileService;
    private final TestDataProfileMapper testDataProfileMapper;

    @RequestMapping(method = RequestMethod.GET)
    public Page<TestDataSetDTO> index(TestDataSetSpecificationBuilder builder, @PageableDefault(value = 2000) Pageable pageable) throws ResourceNotFoundException {
        Long testDataId = null;
        for (SearchCriteria param : builder.params) {
            if (param.getKey().equals("testDataProfileId")) {
                testDataId = Long.parseLong(param.getValue().toString());
            }
        }
        TestData testData = testDataProfileService.find(testDataId);
        List<TestDataSet> testDataSets = testData.getData();
        List<TestDataSetDTO> testDataSetDTOS = testDataMapper.mapTestDataSetDTOs(testDataProfileMapper.mapToDtos(testDataSets));
        return new PageImpl<>(testDataSetDTOS, pageable, testDataSets.size());
    }
}
