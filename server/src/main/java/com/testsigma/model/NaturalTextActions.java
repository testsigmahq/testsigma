/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.converter.NaturalTextActionDataConverter;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "natural_text_actions")
@Data
public class NaturalTextActions {
  @Id
  private Long id;

  @Column(name = "workspace_type")
  @Enumerated(EnumType.STRING)
  private WorkspaceType workspaceType;

  @Column(name = "natural_text")
  private String naturalText;

  @Column
  @Convert(converter = NaturalTextActionDataConverter.class)
  private NaturalTextActionData data;

  @Column(name = "display_name")
  private String displayName;

  @Column
  private String action;

  @Column(name = "snippet_class")
  private String snippetClass;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "allowed_values")
  private String allowedValues;

  @Column(name = "condition_type")
  @Enumerated(EnumType.STRING)
  private StepActionType stepActionType;

  @Column(name = "import_to_web")
  private Long importToWeb;

  @Column(name = "import_to_mobile_web")
  private Long importToMobileWeb;

  @Column(name = "import_to_android_native")
  private Long importToAndroidNative;

  @Column(name = "import_to_ios_native")
  private Long importToIosNative;

  public void setAllowedValues(List allowedValues){
    this.allowedValues = new ObjectMapperService().convertToJson(allowedValues);
  }

  public LinkedHashMap<String, List> getAllowedValues() {
    ObjectMapperService objectMapperService = new ObjectMapperService();
    try {
      return (LinkedHashMap<String, List>) objectMapperService.parseJsonModel(this.allowedValues, Map.class);
    } catch(Exception e) {
      List list = objectMapperService.parseJson(this.allowedValues, List.class);
      if(list != null) {
        return new LinkedHashMap<>() {{
          put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA, list);
        }};
      }
    }
    return null;
  }
}
