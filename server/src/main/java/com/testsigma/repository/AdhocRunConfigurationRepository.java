package com.testsigma.repository;

import com.testsigma.model.AdhocRunConfiguration;
import com.testsigma.model.WorkspaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface AdhocRunConfigurationRepository extends JpaRepository<AdhocRunConfiguration, Long> {
  List<AdhocRunConfiguration> findAllByWorkspaceType(WorkspaceType workspaceType);
}
