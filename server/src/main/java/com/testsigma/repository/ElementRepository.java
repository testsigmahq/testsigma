/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.Element;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ElementRepository extends BaseRepository<Element, Long> {
  Element findFirstElementByNameAndWorkspaceVersionId(String name, Long workspaceVersionId);

  List<Element> findByNameInAndWorkspaceVersionId(List<String> names, Long workspaceVersionId);

}

