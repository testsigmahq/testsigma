/******************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 *****************************************************************************/
package com.testsigma.model;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDValueGenerator implements AnnotationValueGeneration<GeneratedUUID> {
  private Class<?> propertyType;

  private IdGenerator idGenerator;

  private boolean useBase64;

  private String prefix;

  private static String convertToBase64(UUID uuid) {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return Base64.encodeBase64URLSafeString(bb.array()).substring(0, 16);
  }

  @Override
  public void initialize(GeneratedUUID annotation, Class<?> propertyType) {
    this.propertyType = propertyType;
    if (annotation.useNonJdkImplementation()) {
      this.idGenerator = new AlternativeJdkIdGenerator();
    } else {
      this.idGenerator = new JdkIdGenerator();
    }
    this.useBase64 = annotation.base64();
    if (!annotation.prefix().equals("")) {
      this.prefix = annotation.prefix();
    }
  }

  @Override
  public GenerationTiming getGenerationTiming() {
    return GenerationTiming.INSERT;
  }

  @Override
  public ValueGenerator<?> getValueGenerator() {
    return ((session, owner) -> {
      UUID uuid = idGenerator.generateId();
      if (propertyType.equals(String.class) && !useBase64) {
        return uuid.toString();
      } else if (propertyType.equals(String.class) && useBase64) {
        return (prefix == null) ? convertToBase64(uuid) : (prefix + convertToBase64(uuid));
      } else if (propertyType.equals(UUID.class)) {
        return uuid;
      } else {
        throw new IllegalStateException("Type " + propertyType.getName() + " is not supported.");
      }
    });
  }

  @Override
  public boolean referenceColumnInSql() {
    return true;
  }

  @Override
  public String getDatabaseGeneratedReferencedColumnValue() {
    return null;
  }
}
