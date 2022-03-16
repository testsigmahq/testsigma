/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.Element;
import com.testsigma.model.LocatorType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ElementRepository extends BaseRepository<Element, Long> {
  Element findFirstElementByNameAndWorkspaceVersionId(String name, Long workspaceVersionId);

  List<Element> findByNameInAndWorkspaceVersionId(List<String> names, Long workspaceVersionId);

  @Query("SELECT element FROM Element element " +
          "WHERE (element.workspaceVersionId =:versionId) " +
          "AND element.locatorValue =:locValue AND element.locatorType =:locType AND " +
          "element.screenNameId =:snId AND element.isDuplicated=:isDuplicated")
  List<Element> findAllMatchedElements(@Param("versionId") Long applicationVersionId, @Param("locValue") String locatorValue,
                                            @Param("locType") LocatorType locatorType, @Param("snId") Long screenNameId,
                                            @Param("isDuplicated") Boolean isDuplicated);


  @Query("SELECT element FROM Element element " +
          "WHERE (element.workspaceVersionId =:versionId) " +
          "AND element.locatorValue =:locValue AND element.locatorType =:locType AND " +
          "element.screenNameId =:snId")
  List<Element> findAllMatchedElements(@Param("versionId") Long applicationVersionId, @Param("locValue") String locatorValue,
                                            @Param("locType") LocatorType locatorType, @Param("snId") Long screenNameId);
}

