package com.testsigma.model;

import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addon_natural_text_action_parameters",
  uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@Data
@EntityListeners(AuditingEntityListener.class)
public class AddonNaturalTextActionParameter {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "addon_natural_text_action_id")
  private Long pluginActionId;

  @Column
  private String reference;

  @Column
  private String name;

  @Column
  @Lob
  private String description;

  @Column(name = "parameter_type")
  @Enumerated(EnumType.STRING)
  private KibbutzActionParameterType type;

  @ManyToOne
  @JoinColumn(name = "addon_natural_text_action_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  @Fetch(value = FetchMode.SELECT)
  private AddonNaturalTextAction pluginAction;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "allowed_values")
  private String allowedValues;

  public void setAllowedValues(List allowedValues){
    this.allowedValues = new ObjectMapperService().convertToJson(allowedValues);
  }


  public List getAllowedValues(){
    return new ObjectMapperService().parseJson(this.allowedValues, List.class);
  }
}
