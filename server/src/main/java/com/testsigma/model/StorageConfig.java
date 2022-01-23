package com.testsigma.model;


import lombok.Data;

import javax.persistence.*;


@Entity
@Table(name = "storage_config")
@Data
public class StorageConfig {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "storage_type")
  @Enumerated(EnumType.STRING)
  private StorageType storageType;

  @Column(name = "aws_bucket_name")
  private String awsBucketName;
  @Column(name = "aws_region")
  private String awsRegion;
  @Column(name = "aws_endpoint")
  private String awsEndpoint;
  @Column(name = "aws_access_key")
  private String awsAccessKey;
  @Column(name = "aws_secret_key")
  private String awsSecretKey;

  @Column(name = "azure_blob_container_name")
  private String azureContainerName;
  @Column(name = "azure_blob_connection_string")
  private String azureConnectionString;

  @Column(name = "on_premise_root_directory")
  private String onPremiseRootDirectory;

  @Transient
  private Integer azureBlobPreSignedURLTimeout = 300;

  @Transient
  private Integer awsS3PreSignedURLTimeout = 300;

}
