package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "requirements")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Requirement {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 250, unique = true)
  private String requirementName;

  @Column(name = "description", length = 250)
  private String requirementDescription;

  @Transient
  private Map<String, String> files;

  @Column(name = "workspace_version_id")
  private Long workspaceVersionId;


  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;


  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "workspace_version_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private WorkspaceVersion version;

  @OneToMany(mappedBy = "requirement", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<TestCase> testcases;

  @Transient
  private String requirementPriorityName;
}
