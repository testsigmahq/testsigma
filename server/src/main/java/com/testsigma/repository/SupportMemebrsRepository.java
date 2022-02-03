/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.repository;

import com.testsigma.model.SupportMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SupportMemebrsRepository extends JpaRepository<SupportMembers, Long> {
  List<SupportMembers> findAll();
}
