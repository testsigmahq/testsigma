/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "uploads")
@Data
@ToString
@EqualsAndHashCode
public class Upload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workspace_id")
    private Long workspaceId;

    @Column(name = "name")
    private String name;

    @Column(name = "latest_version_id")
    private Long latestVersionId;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @UpdateTimestamp
    private Timestamp updatedDate;

    @Transient
    private String preSignedURL;

    @OneToMany(mappedBy = "upload", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<ProvisioningProfileUpload> provisioningProfileUploads;


    @OneToOne
    @JoinColumn(name = "latest_version_id", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private UploadVersion latestVersion;

    @Column(name = "imported_id")
    private Long importedId;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "workspace_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Workspace workspace;
}
