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
@Table(name = "reports")
@Data
@ToString
@EqualsAndHashCode
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @OneToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "module_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ReportModule reportModule;

    @OneToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "config_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ReportConfiguration reportConfiguration;

    @OneToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "version_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private WorkspaceVersion workspaceVersion;

}
