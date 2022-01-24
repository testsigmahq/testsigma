/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.UserPreferenceDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.UserPreferenceMapper;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.model.UserPreference;
import com.testsigma.service.WorkspaceService;
import com.testsigma.service.WorkspaceVersionService;
import com.testsigma.service.CurrentUserService;
import com.testsigma.service.UserPreferenceService;
import com.testsigma.web.request.UserPreferenceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/user_preferences")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class UserPreferencesController {
  private final UserPreferenceService userPreferenceService;
  private final UserPreferenceMapper userPreferenceMapper;
  private final WorkspaceVersionService workspaceVersionService;
  private final WorkspaceService workspaceService;


  @GetMapping
  public UserPreferenceDTO show() throws ResourceNotFoundException {
    if (CurrentUserService.getCurrentUser().getEmail() == null) {
      return new UserPreferenceDTO();
    }
    UserPreference userPreference = userPreferenceService.findByEmail(CurrentUserService.getCurrentUser().getEmail());
    WorkspaceVersion appVersion = workspaceVersionService.find(userPreference.getVersionId());
    userPreference.setWorkspaceId(appVersion.getWorkspaceId());
    return userPreferenceMapper.map(userPreference);
  }

  @PutMapping
  public UserPreferenceDTO update(@RequestBody UserPreferenceRequest userPreferenceRequest)
    throws ResourceNotFoundException, SQLException {
    if (CurrentUserService.getCurrentUser().getEmail() == null) {
      return new UserPreferenceDTO();
    }
    UserPreference userPreference = userPreferenceService.findByEmail(CurrentUserService.getCurrentUser().getEmail());
    userPreference.setVersionId(userPreferenceRequest.getVersionId());
    userPreference.setTestCaseFilterId(userPreferenceRequest.getTestCaseFilterId());
    userPreferenceMapper.merge(userPreferenceRequest, userPreference);
    userPreference = userPreferenceService.save(userPreference);
    return userPreferenceMapper.map(userPreference);
  }
}
