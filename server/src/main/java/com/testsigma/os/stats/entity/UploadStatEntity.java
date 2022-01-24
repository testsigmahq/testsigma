package com.testsigma.os.stats.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UploadStatEntity extends BaseStatEntity {
  private Long uploadId;
  private String uploadExtension;
}
