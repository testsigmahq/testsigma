package com.testsigma.service;

import com.testsigma.dto.MobileInspectionDTO;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.MobileInspectionMapper;
import com.testsigma.model.WorkspaceType;
import com.testsigma.model.MobileInspection;
import com.testsigma.model.MobileInspectionStatus;
import com.testsigma.model.Platform;
import com.testsigma.repository.MobileInspectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MobileInspectionService {
  private final MobileInspectionRepository mobileInspectionRepository;
  private final MobileInspectionMapper mobileInspectionMapper;
  private final PlatformsService platformsService;
  private final TestDeviceResultService testDeviceResultService;

  public MobileInspection find(Long id) throws TestsigmaDatabaseException {
    return mobileInspectionRepository.findById(id).orElseThrow(() -> new TestsigmaDatabaseException("Mobile Inspection not found with" + id));
  }

  public MobileInspection create(MobileInspection mobileInspection) {
    return this.mobileInspectionRepository.save(mobileInspection);
  }

  public MobileInspection update(MobileInspection mobileInspection) {
    return this.mobileInspectionRepository.save(mobileInspection);
  }

  public Page<MobileInspection> findAll(Specification<MobileInspection> spec, Pageable pageable) {
    return this.mobileInspectionRepository.findAll(spec, pageable);
  }

  public List<MobileInspection> findAllByLastActiveAtBeforeAndStatusIn(Timestamp lastActiveAt, Collection<MobileInspectionStatus> statusTypes) {
    return this.mobileInspectionRepository.findAllByLastActiveAtBeforeAndStatusIn(lastActiveAt, statusTypes);
  }

  public MobileInspectionDTO closeSession(Long id) throws TestsigmaException {
    log.info("Closing Mobile inspector session with id - " + id);
    MobileInspection mobileInspection = find(id);
    mobileInspection.setLastActiveAt(new Timestamp(System.currentTimeMillis()));
    mobileInspection.setFinishedAt(new Timestamp(System.currentTimeMillis()));
    mobileInspection.setStatus(MobileInspectionStatus.FINISHED);
    mobileInspection = update(mobileInspection);
    WorkspaceType workspaceType = WorkspaceType.AndroidNative;
    if (mobileInspection.getPlatform().equals(Platform.iOS))
      workspaceType = WorkspaceType.IOSNative;
    if (mobileInspection.getSessionId() != null) {
      platformsService.closePlatformSession(mobileInspection.getLabType());
    }
    try {
      testDeviceResultService.sendPendingTestPlans();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return mobileInspectionMapper.mapDTO(mobileInspection);
  }
}
