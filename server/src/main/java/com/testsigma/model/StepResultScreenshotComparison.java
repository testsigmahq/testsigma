/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Calendar;

@Data
@Entity
@Table(name = "test_step_result_screenshot_comparisons")
@EqualsAndHashCode
public class StepResultScreenshotComparison {

  @Column(name = "created_date")
  @CreationTimestamp
  protected Timestamp createdDate = new Timestamp(Calendar.getInstance().getTimeInMillis());
  @Column(name = "updated_date")
  @UpdateTimestamp
  protected Timestamp updatedDate = null;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "test_step_id")
  @NotNull
  private Long testStepId;
  @Column(name = "test_step_result_id")
  @NotNull
  private Long testStepResultId;
  @Column(name = "test_case_result_id")
  @NotNull
  private Long testCaseResultId;
  @Column(name = "test_step_screenshot_id")
  private Long testStepBaseScreenshotId;
  @Column(name = "similarity_score")
  private Double similarityScore;
  @Column(name = "diff_coordinates")
  private String diffCoordinates;
  @Column(name = "image_shape")
  private String imageShape;
  @Column(name = "error_message")
  private String errorMessage;
  @Transient
  private String screenShotURL;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_step_screenshot_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestStepScreenshot testStepScreenshot;

  @OneToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_step_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestStepResult testStepResult;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_case_result_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private TestCaseResult testCaseResult;
}
