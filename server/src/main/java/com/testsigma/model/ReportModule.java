/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "report_modules")
@Data
@ToString
@EqualsAndHashCode
public class ReportModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "module_name")
    private String moduleName;

    @Column(name = "model_class")
    private String moduleClass;

    @Column(name = "specification_class")
    private String specificationClass;

    @Column(name = "service_class")
    private String serviceClass;

    @Column(name = "builder_class")
    private String builderClass;
}
