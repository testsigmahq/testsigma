package com.testsigma.controller.api.agent;

import com.testsigma.dto.MobileInspectionDTO;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.mapper.MobileInspectionMapper;
import com.testsigma.model.MobileInspection;
import com.testsigma.service.MobileInspectionService;
import com.testsigma.web.request.MobileInspectionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController(value = "agentMobileInspectionsController")
@RequestMapping(value = {"/api/agents/{uuid}/mobile_inspections"})
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class MobileInspectionsController {

  private final MobileInspectionService mobileInspectionService;
  private final MobileInspectionMapper mobileInspectionMapper;

  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  public MobileInspectionDTO update(@PathVariable("uuid") String uniqueId,
                                    @PathVariable("id") Long mobileInspectionId,
                                    @RequestBody MobileInspectionRequest request) throws TestsigmaDatabaseException {
    log.info("put request api/agents/" + uniqueId + "/mobile_inspections/" + mobileInspectionId + ": " + request);
    MobileInspection mobileInspection = this.mobileInspectionService.find(mobileInspectionId);
    this.mobileInspectionMapper.merge(request, mobileInspection);
    mobileInspection = this.mobileInspectionService.update(mobileInspection);
    return mobileInspectionMapper.mapDTO(mobileInspection);
  }
}
