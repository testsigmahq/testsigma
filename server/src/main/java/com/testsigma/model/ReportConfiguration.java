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
@Table(name = "report_configurations")
@Data
@ToString
@EqualsAndHashCode
public class ReportConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "selected_columns")
    private String selectedColumns;

    @OneToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "order_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private FieldConfiguration orderByField;

    @OneToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "group_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private FieldConfiguration groupByField;

    @OneToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "chart_group_field", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private FieldConfiguration chartGroupField;

    @Column(name = "query_string")
    private String queryString;

    @Column(name = "chart_type")
    @Enumerated(EnumType.STRING)
    private ChartType chartType;

    @OneToMany(mappedBy = "reportConfiguration", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<ReportConfigurationCriteriaMappings> reportCriteriaMappings;
}
