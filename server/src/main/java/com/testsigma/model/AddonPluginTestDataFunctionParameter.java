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

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "addon_plugin_test_data_function_parameters")
@Data
public class AddonPluginTestDataFunctionParameter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="test_data_function_id")
  private Long testDataFunctionId;

  @Column
  private String reference;

  @Column
  private String name;

  @Column
  @Lob
  private String description;

  @Column(name="parameter_type")
  @Enumerated(EnumType.STRING)
  private AddonPluginTestDataFunctionParameterType type;

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


  @ManyToOne
  @JoinColumn(name = "test_data_function_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @Fetch(value = FetchMode.SELECT)
  private AddonPluginTestDataFunction pluginTestDataFunction;

}
