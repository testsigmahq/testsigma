package com.testsigma.controller.api.v1;

import com.testsigma.dto.api.APITestSuiteResultDTO;
import com.testsigma.mapper.TestSuiteResultMapper;
import com.testsigma.model.TestSuiteResult;
import com.testsigma.service.TestSuiteResultService;
import com.testsigma.specification.TestSuiteResultSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController(value = "apiTestSuiteResultController")
@RequestMapping(path = "/api/v1/test_suite_results")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestSuiteResultController {

  private final TestSuiteResultService testSuiteResultService;
  private final TestSuiteResultMapper testSuiteResultMapper;

  @GetMapping
  public Page<APITestSuiteResultDTO> index(TestSuiteResultSpecificationsBuilder builder, @PageableDefault(size = 50) Pageable pageable) {
    log.info("Request /test_suite_results/");
    Specification<TestSuiteResult> spec = builder.build();
    Page<TestSuiteResult> testSuiteResults = testSuiteResultService.findAll(spec, pageable);
    List<APITestSuiteResultDTO> testSuiteResultDTOS =
      testSuiteResultMapper.mapApiDTO(testSuiteResults.getContent());
    return new PageImpl<>(testSuiteResultDTOS, pageable, testSuiteResults.getTotalElements());
  }

}
