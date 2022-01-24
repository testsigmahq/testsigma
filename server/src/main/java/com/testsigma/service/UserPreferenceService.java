/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.model.Workspace;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.model.AuthUser;
import com.testsigma.model.UserPreference;
import com.testsigma.repository.UserPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserPreferenceService {
  private final UserPreferenceRepository userPreferenceRepository;
  private final WorkspaceService workspaceService;
  private final WorkspaceVersionService workspaceVersionService;

  public UserPreference findByEmail(String email) {
    return this.userPreferenceRepository.findByEmail(email).orElse(null);
  }

  public UserPreference save(UserPreference userPreference) {
    return this.userPreferenceRepository.save(userPreference);
  }

  public void insertDefaultUserPreferences(AuthUser authUser) {
    UserPreference userPreference = this.findByEmail(authUser.getEmail());
    if (userPreference == null) {
      userPreference = new UserPreference();
      Workspace workspace = workspaceService.findFirstWebDemoApplication();
      WorkspaceVersion applicationVersion = workspaceVersionService.findFirstByWorkspaceId(workspace.getId());
      userPreference.setVersionId(applicationVersion.getId());
      userPreference.setEmail(authUser.getEmail());
      userPreferenceRepository.save(userPreference);
    }
  }
}
