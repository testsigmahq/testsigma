/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.model;


import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import javax.persistence.*;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
@Log4j2
@MappedSuperclass
public abstract class BaseModel<T> implements Serializable, Cloneable {

  @Transient
  public Map<Field, Object> previousStateFields = new HashMap<>();

  @Transient
  public Map<Field, Object> changedFields = new HashMap<>();

  @PostLoad
  public void savePostLoadState() {
    for (Field field : this.getClass().getDeclaredFields()) {
      try {
        if (!field.getName().equals("log")) {
          Object fieldValue = new PropertyDescriptor(field.getName(),
            this.getClass()).getReadMethod().invoke(this);
          previousStateFields.put(field, fieldValue);
        } else {
          log.info("skipping unwanted field - " + field.getName());
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  @PrePersist
  public void savePrePersistState() {
    for (Field field : this.getClass().getDeclaredFields()) {
      if (!field.getName().equals("log")) {
        try {
          previousStateFields.put(field, null);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
    }
  }

  @PostPersist
  @PostUpdate
  public void postPersistActions() {
    checkModifiedFields();
  }

  public void checkModifiedFields() {
    previousStateFields.forEach((field, oldFieldValue) -> {
      try {
        Object newFieldValue = new PropertyDescriptor(field.getName(),
          this.getClass()).getReadMethod().invoke(this);

        if (isFieldChanged(newFieldValue, oldFieldValue)) {
          changedFields.put(field, newFieldValue);
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });
  }

  protected boolean isFieldChanged(Object newValue, Object oldValue) {
    return (((oldValue != null) && (newValue == null)) || ((newValue != null) && !fieldUnChanged(newValue, oldValue)));
  }

  private boolean fieldUnChanged(Object newValue, Object oldValue) {
    if (newValue instanceof JSONObject) {
      return ((JSONObject) newValue).similar(oldValue);
    }
    return newValue.equals(oldValue);
  }

}
