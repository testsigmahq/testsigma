/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.export.RequirementXMLDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.RequirementMapper;
import com.testsigma.model.Requirement;
import com.testsigma.repository.RequirementRepository;
import com.testsigma.specification.RequirementSpecificationsBuilder;
import com.testsigma.specification.SearchCriteria;
import com.testsigma.specification.SearchOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service(value = "requirementService")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RequirementService extends XMLExportService<Requirement> {

  private final RequirementRepository requirementRepository;
  private final RequirementMapper mapper;

  public Page<Requirement> findAll(Specification<Requirement> spec, Pageable pageable) {
    return this.requirementRepository.findAll(spec, pageable);
  }

  public Requirement find(Long id) throws ResourceNotFoundException {
    return this.requirementRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Requirement missing with::" + id));
  }

  public Requirement create(Requirement requirement) {
    return this.requirementRepository.save(requirement);
  }

  public Requirement update(Requirement requirement) {
    return this.requirementRepository.save(requirement);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    Requirement requirement = find(id);
    this.requirementRepository.delete(requirement);
  }

  public void export(BackupDTO backupDTO) throws IOException, ResourceNotFoundException {
    if (!backupDTO.getIsRequirementEnabled()) return;
    log.debug("backup process for requirement initiated");
    writeXML("requirement", backupDTO, PageRequest.of(0, 25));
    log.debug("backup process for requirement completed");
  }

  @Override
  protected List<RequirementXMLDTO> mapToXMLDTOList(List<Requirement> list) {
    return mapper.mapRequirements(list);
  }

  public Specification<Requirement> getExportXmlSpecification(BackupDTO backupDTO) {
    SearchCriteria criteria = new SearchCriteria("workspaceVersionId", SearchOperation.EQUALITY, backupDTO.getWorkspaceVersionId());
    List<SearchCriteria> params = new ArrayList<>();
    params.add(criteria);
    RequirementSpecificationsBuilder requirementSpecificationsBuilder = new RequirementSpecificationsBuilder();
    requirementSpecificationsBuilder.params = params;
    return requirementSpecificationsBuilder.build();
  }
}
