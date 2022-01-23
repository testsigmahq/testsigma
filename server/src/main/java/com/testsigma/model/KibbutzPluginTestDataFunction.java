package com.testsigma.model;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


@Entity
@Table(name = "kibbutz_plugin_test_data_functions")
@Data
public class  KibbutzPluginTestDataFunction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="fully_qualified_name")
  private String fullyQualifiedName;

  @Column
  @Lob
  private String description;

//  @Column
//  private String grammar;

  @Column(name="display_name")
  private String displayName;

  @Column(name="addon_id")
  private Long addonId;

  @Column
  private Boolean deprecated;

  @ManyToOne
  @JoinColumn(name = "addon_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @Fetch(value = FetchMode.SELECT)
  private Addon plugin;

  @Column(name = "created_by_id")
  @CreatedBy
  private Long createdById;

  @Column(name = "updated_by_id")
  @LastModifiedBy
  private Long updatedById;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "pluginTestDataFunction")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<KibbutzPluginTestDataFunctionParameter> parameters;

  @Transient
  private String externalUniqueId;
}
