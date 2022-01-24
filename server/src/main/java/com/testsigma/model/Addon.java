package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "addons",
  uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@Data
@EntityListeners(AuditingEntityListener.class)
public class Addon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String name;

  @Column
  @Lob
  private String description;

  @Column
  private String version;

  @Column(name = "external_unique_id")
  private String externalUniqueId;

  @Column(name = "external_installed_version_unique_id")
  private String externalInstalledVersionUniqueId;

  @Column(name = "modified_hash")
  private String modifiedHash;

  @Column
  @Enumerated(EnumType.STRING)
  private AddonStatus status;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "plugin")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<AddonNaturalTextAction> actions;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "plugin")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<KibbutzPluginTestDataFunction> testDataFunctions;

}
