package com.testsigma.controller;


import com.testsigma.dto.MobileInspectionDTO;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.MobileInspectionMapper;
import com.testsigma.model.MobileInspection;
import com.testsigma.model.MobileInspectionStatus;
import com.testsigma.service.MobileInspectionService;
import com.testsigma.specification.MobileInspectionSpecificationBuilder;
import com.testsigma.web.request.MobileInspectionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/mobile_inspections")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class MobileInspectionsController {
  private final MobileInspectionService mobileInspectionService;
  private final MobileInspectionMapper mobileInspectionMapper;

  @GetMapping
  public Page<MobileInspectionDTO> index(MobileInspectionSpecificationBuilder builder, Pageable pageable) {
    log.info("Index request /mobile_inspections" + builder);
    Specification<MobileInspection> spec = builder.build();
    Page<MobileInspection> inspections = mobileInspectionService.findAll(spec, pageable);
    List<MobileInspectionDTO> mobileInspectionDTOS = mobileInspectionMapper.mapDTO(inspections.getContent());
    return new PageImpl<>(mobileInspectionDTOS, pageable, inspections.getTotalElements());
  }

  @GetMapping(value = "/{id}")
  public MobileInspectionDTO show(@PathVariable(value = "id") Long id) throws TestsigmaDatabaseException {
    log.info("Get request /mobile_inspections/" + id);
    MobileInspection mobileInspection = this.mobileInspectionService.find(id);
    return mobileInspectionMapper.mapDTO(mobileInspection);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public MobileInspectionDTO create(@RequestBody MobileInspectionRequest request) throws TestsigmaException, IOException, SQLException {
    log.info("Post request /mobile_inspections/" + request);
    MobileInspection mobileInspection = this.mobileInspectionMapper.map(request);
    mobileInspection.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    mobileInspection.setLastActiveAt(new Timestamp(System.currentTimeMillis()));
    mobileInspection = this.mobileInspectionService.create(mobileInspection);

    return this.mobileInspectionMapper.mapDTO(mobileInspection);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public MobileInspectionDTO update(@PathVariable("id") Long id, @RequestBody MobileInspectionRequest request) throws TestsigmaException {
    log.info("Update request /mobile_inspections/" + id + " with request" + request);
    if (request.getStatus() == MobileInspectionStatus.FINISHED) {
      return mobileInspectionService.closeSession(id);
    }
    MobileInspection mobileInspection = this.mobileInspectionService.find(id);
    request.setLastActiveAt(new Timestamp(System.currentTimeMillis()));
    this.mobileInspectionMapper.merge(request, mobileInspection);
    mobileInspection = this.mobileInspectionService.update(mobileInspection);
    return mobileInspectionMapper.mapDTO(mobileInspection);
  }
}
