package com.testsigma.dto;

import com.testsigma.model.TagType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TagDTO {

  private Long id;

  private String name;

  private TagType type;

  private Integer count;
  private Timestamp createdDate;
  private Timestamp updatedDate;


}
