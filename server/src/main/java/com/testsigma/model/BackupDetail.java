package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "backup_details")
public class BackupDetail implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name = "back_up_" + String.join("_", new Timestamp(java.lang.System.currentTimeMillis()).toString().split(" ")) + ".zip";

  @Column(name = "is_test_case_enabled")
  private Boolean isTestCaseEnabled;

  @Column(name = "is_test_step_enabled")
  private Boolean isTestStepEnabled;

  @Column(name = "is_rest_step_enabled")
  private Boolean isRestStepEnabled;

  @Column(name = "is_upload_enabled")
  private Boolean isUploadsEnabled;

  @Column(name = "is_test_case_priority_enabled")
  private Boolean isTestCasePriorityEnabled;

  @Column(name = "is_test_case_type_enabled")
  private Boolean isTestCaseTypeEnabled;

  @Column(name = "is_element_enabled")
  private Boolean isElementEnabled;

  @Column(name = "is_element_screen_name_enabled")
  private Boolean isElementScreenNameEnabled;

  @Column(name = "is_test_data_enabled")
  private Boolean isTestDataEnabled;

  @Column(name = "is_attachment_enabled")
  private Boolean isAttachmentEnabled;

  @Column(name = "is_agent_enabled")
  private Boolean isAgentEnabled;

  @Column(name = "is_requirement_enabled")
  private Boolean isRequirementEnabled;

  @Column(name = "is_test_plan_enabled")
  private Boolean isTestPlanEnabled;

  @Column(name = "is_test_device_enabled")
  private Boolean isTestDeviceEnabled;

  @Column(name = "is_suites_enabled")
  private Boolean isSuitesEnabled;

  @Column(name = "is_label_enabled")
  private Boolean isLabelEnabled;

  @Column(name = "workspace_version_id")
  private Long workspaceVersionId;

  @Column(name = "filter_id")
  private Long filterId;

  @Column(name = "entity_id")
  private Long entityId;

  @Column
  @Enumerated(EnumType.ORDINAL)
  private BackupStatus status;

  @Column
  private String message;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updateDate;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "workspace_version_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @NotFound(action = NotFoundAction.IGNORE)
  private WorkspaceVersion workspaceVersion;

  @Transient
  private File srcFiles;
  @Transient
  private File destFiles;
}
