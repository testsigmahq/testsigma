package com.testsigma.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "addon_natural_text_actions",
  uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@Data
@EntityListeners(AuditingEntityListener.class)
public class AddonNaturalTextAction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fully_qualified_name")
  private String fullyQualifiedName;

  @Column
  @Lob
  private String description;

  @Column(name= "natural_text")
  private String naturalText;

  @Column(name = "addon_id")
  private Long addonId;

  @Column(name = "workspace_type")
  @Enumerated(EnumType.STRING)
  private WorkspaceType workspaceType;

  @Column
  private Boolean deprecated;

  @ManyToOne
  @JoinColumn(name = "addon_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @Fetch(value = FetchMode.SELECT)
  private Addon plugin;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "condition_type")
  @Enumerated(EnumType.STRING)
  private StepActionType stepActionType;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "pluginAction")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<AddonNaturalTextActionParameter> parameters;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "plugin")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<AddonPluginTestDataFunction> testDataFunctions;

}
