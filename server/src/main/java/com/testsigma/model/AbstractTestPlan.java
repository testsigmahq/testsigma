package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "test_plans")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "entity_type", discriminatorType = DiscriminatorType.STRING)
@Data
@Log4j2
@TypeDefs({
  @TypeDef(name = "json", typeClass = JsonStringType.class)
})
public class AbstractTestPlan implements Serializable {
  @Transient
  Set<Long> orphanTestDeviceIds;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "workspace_version_id")
  private Long workspaceVersionId;
  @Column(name = "last_run_id")
  private Long lastRunId;
  @Column(name = "name", length = 250, unique = true)
  private String name;
  @Column(name = "description", length = 250, unique = true)
  private String description;
  @Column(name = "entity_type", insertable = false, updatable = false)
  private String entityType;
  @Column(name = "test_lab_type")
  @Enumerated(EnumType.STRING)
  private TestPlanLabType testPlanLabType;
  @Column(name = "test_plan_type")
  @Enumerated(EnumType.STRING)
  private TestPlanType testPlanType;
  @Column(name = "element_time_out", length = 11)
  private Integer elementTimeOut;
  @Column(name = "page_time_out", length = 11)
  private Integer pageTimeOut;
  @Column(name = "environment_id")
  private Long environmentId;
  @Column(name = "screenshot")
  @Enumerated(EnumType.STRING)
  private Screenshot screenshot;
  @Column(name = "recovery_action")
  @Enumerated(EnumType.STRING)
  private RecoverAction recoveryAction = RecoverAction.Run_Next_Testcase;
  @Column(name = "on_aborted_action")
  @Enumerated(EnumType.STRING)
  private OnAbortedAction onAbortedAction = OnAbortedAction.Reuse_Session;
  @Column(name = "re_run_on_failure")
  @Enumerated(EnumType.STRING)
  private ReRunType reRunType = ReRunType.NONE;
  @Column(name = "on_suite_pre_requisite_failed")
  @Enumerated(EnumType.STRING)
  private PreRequisiteAction onSuitePreRequisiteFail = PreRequisiteAction.Abort;
  @Column(name = "on_testcase_pre_requisite_failed")
  @Enumerated(EnumType.STRING)
  private PreRequisiteAction onTestcasePreRequisiteFail = PreRequisiteAction.Abort;
  @Column(name = "on_step_pre_requisite_failed")
  @Enumerated(EnumType.STRING)
  private RecoverAction onStepPreRequisiteFail = RecoverAction.Run_Next_Testcase;
  
  @Column(name = "retry_session_timeout", length = 11)
  private Integer retrySessionCreationTimeout;
  @Column(name = "retry_session_creation")
  private boolean retrySessionCreation;
  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;
  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;
  @Column(name = "match_browser_version")
  private Boolean matchBrowserVersion = Boolean.FALSE;

  @Column(name = "imported_id")
  private Long importedId;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "workspace_version_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private WorkspaceVersion workspaceVersion;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "environment_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Environment environment;
  @OneToMany(mappedBy = "testPlan", fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  private List<TestDevice> testDevices;
  @OneToMany(mappedBy = "testPlan", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @JsonIgnore
  private List<TestPlanResult> testPlanResults;
  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "last_run_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestPlanResult lastRun;
}
