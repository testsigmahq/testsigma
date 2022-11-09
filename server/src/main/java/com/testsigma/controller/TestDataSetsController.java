package com.testsigma.controller;

import com.testsigma.dto.TestDataSetDTO;
import com.testsigma.mapper.TestDataProfileMapper;
import com.testsigma.model.TestDataSet;
import com.testsigma.service.TestDataSetService;
import com.testsigma.specification.TestDataSetSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/test_data_sets", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestDataSetsController {
    private final TestDataSetService testDataSetService;
    private final TestDataProfileMapper testDataProfileMapper;

    @RequestMapping(method = RequestMethod.GET)
    public Page<TestDataSetDTO> index(TestDataSetSpecificationBuilder builder, @PageableDefault(value = 2000) Pageable pageable){
        Specification<TestDataSet> specification = builder.build();
        Page<TestDataSet> testDataSets = testDataSetService.findAll(specification, pageable);
        List<TestDataSetDTO> testDataSetDTOS = testDataProfileMapper.mapToDtos(testDataSets.getContent());
        return new PageImpl<>(testDataSetDTOS, pageable, testDataSets.getTotalElements());
    }

    @RequestMapping(path = "/bulk", method = RequestMethod.DELETE)
    public void bulkDelete(@RequestParam(value = "ids[]") Long[] ids) throws Exception {
        this.testDataSetService.bulkDestroy(ids);
    }
}