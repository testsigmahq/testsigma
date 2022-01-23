/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
@ToString
@DiscriminatorValue("ADHOC_TEST_PLAN")
@Data
public class DryTestPlan extends AbstractTestPlan {

  @Transient
  private Long testCaseId;
}
