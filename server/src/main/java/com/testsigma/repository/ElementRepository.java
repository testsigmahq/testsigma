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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

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

  Optional<Element> findAllByWorkspaceVersionIdAndImportedId(Long applicationVersionId, Long id);

  Optional<Element> findByNameAndWorkspaceVersionId(String name, Long workspaceVersionId);

  @Query(value= "SELECT * FROM elements T1 " +
          "INNER JOIN element_screen_names T2 on T1.screen_name_id=T2.id " +
          "where T1.workspace_version_id=:versionId " +
          "AND T1.element_name LIKE %:fieldName% " +
          "AND T2.name LIKE %:snName% " +
          "ORDER BY FIELD(screen_name_id,  :snId) DESC, screen_name_id", nativeQuery = true)
  Page<Element> findWithOrderByPreviousStepElementID(Pageable pageable,
                                                            @Param("versionId") Long applicationVersionId,
                                                            @Param("fieldName") String name, @Param("snName") String screenName,
                                                            @Param("snId") Long previousElementId);
}


