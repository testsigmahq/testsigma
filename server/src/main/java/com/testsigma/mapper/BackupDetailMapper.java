/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.BackupDTO;
import com.testsigma.dto.BackupDetailDTO;
import com.testsigma.model.BackupDetail;
import com.testsigma.web.request.BackupRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface BackupDetailMapper {
  BackupDTO map(BackupRequest file);

  BackupDetail map(BackupDTO file);

  BackupDTO mapTo(BackupDetail backupDetail);

  BackupDetailDTO map(BackupDetail backupDetail);

  List<BackupDetailDTO> map(List<BackupDetail> backupDetails);
}
