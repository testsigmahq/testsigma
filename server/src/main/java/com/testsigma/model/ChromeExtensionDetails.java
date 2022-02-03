package com.testsigma.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "chrome_extention_details")
@Data
public class ChromeExtensionDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "exclude_attributes")
  private String excludeAttributes;

  @Column(name = "exclude_classes")
  private String excludeClasses;

  @Column(name = "include_classes")
  private String includeClasses;

  @Column(name = "include_attriutes")
  private String includeAttributes;

  @Column(name = "chrome_extention_details")
  private String chromeExtensionDetails;

  @Column(name = "userdefined_attributes")
  private String userDefinedAttributes;
}
