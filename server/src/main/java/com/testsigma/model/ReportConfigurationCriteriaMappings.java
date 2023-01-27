/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "report_configuration_criteria_mappings")
@Data
public class ReportConfigurationCriteriaMappings {
    @Column(name = "criteria_id")
    Long criteriaId;

    @Column(name = "configuration_id")
    Long configId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    @CreationTimestamp
    private Timestamp createdDate;

    @Column(name = "updated_date")
    @UpdateTimestamp
    private Timestamp updatedDate;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @MapsId("criteria_id")
    @JoinColumn(name = "criteria_id")
    private ReportCriteria criteria;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @MapsId("configuration_id")
    @JoinColumn(name = "configuration_id")
    private ReportConfiguration reportConfiguration;

    public ReportConfigurationCriteriaMappings() {
        super();
    }
}
