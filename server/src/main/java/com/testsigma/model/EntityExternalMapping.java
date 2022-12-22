/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.testsigma.annotation.AuditColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;

@Entity
@Table(name = "entity_external_mappings", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
@Data
public class EntityExternalMapping implements Serializable {

    @Transient
    Map<String, Object> fields;

    @Transient
    Boolean linkToExisting = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AuditColumn
    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "external_id")
    private String externalId;

    @Column
    private String misc;

    @Column(name = "push_failed")
    private Boolean pushFailed;

    @Column
    private String message;

    @Column(name = "assets_push_failed")
    private Boolean assetsPushFailed;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    @WhereJoinTable(clause = "type ='TEST_STEP'")
    private TestStep testStep;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    @WhereJoinTable(clause = "type ='TEST_CASE'")
    private TestCase testCase;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    @WhereJoinTable(clause = "type ='TEST_SUITE'")
    private TestSuite testSuite;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    @WhereJoinTable(clause = "type ='TEST_PLAN'")
    private TestPlan execution;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    @WhereJoinTable(clause = "type ='TEST_CASE_RESULT'")
    private TestCaseResult testCaseResult;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    @WhereJoinTable(clause = "type ='ELEMENT'")
    private Element element;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    @WhereJoinTable(clause = "type ='WORKSPACE_VERSION'")
    private WorkspaceVersion workspaceVersion;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "entity_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    @WhereJoinTable(clause = "type ='TEST_SUITE_RESULT'")
    private TestSuiteResult testSuiteResult;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "application_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @NotFound(action = NotFoundAction.IGNORE)
    private Integrations application;

    @Transient
    private Boolean canPushAssets = Boolean.FALSE;

    @Transient
    private Long suiteResultId;

}