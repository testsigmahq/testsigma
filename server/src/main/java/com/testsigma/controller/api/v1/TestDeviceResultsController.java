package com.testsigma.controller.api.v1;

import com.testsigma.dto.api.APITestDeviceResultDTO;
import com.testsigma.mapper.TestDeviceResultMapper;
import com.testsigma.model.TestDeviceResult;
import com.testsigma.service.TestDeviceResultService;
import com.testsigma.specification.TestDeviceResultSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController(value = "apiTestDeviceResultsController")
@Log4j2
@RequestMapping(path = "/api/v1/test_device_results")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestDeviceResultsController {

  private final TestDeviceResultService testDeviceResultService;
  private final TestDeviceResultMapper testDeviceResultMapper;

  @RequestMapping(method = RequestMethod.GET)
  public Page<APITestDeviceResultDTO> index(TestDeviceResultSpecificationsBuilder builder, @PageableDefault(size = 50) Pageable pageable) {
    log.info("Request /test_device_results list request received");
    Specification<TestDeviceResult> spec = builder.build();
    Page<TestDeviceResult> environmentResults = testDeviceResultService.findAll(spec, pageable);
    List<APITestDeviceResultDTO> environmentResultDTOS =
      testDeviceResultMapper.mapApiDTO(environmentResults.getContent());
    return new PageImpl<>(environmentResultDTOS, pageable, environmentResults.getTotalElements());
  }
}
