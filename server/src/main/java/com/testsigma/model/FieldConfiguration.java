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
@Table(name = "field_configurations")
@Data
@ToString
@EqualsAndHashCode
public class FieldConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "module_id", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private ReportModule reportModule;

    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "order_by_allowed", columnDefinition = "bit default 0", nullable = false)
    private Boolean isOrderAllowed;

    @Column(name = "group_by_allowed", columnDefinition = "bit default 0", nullable = false)
    private Boolean isGroupByAllowed;

    @Column(name = "criteria_allowed", columnDefinition = "bit default 0", nullable = false)
    private Boolean isCriteriaAllowed;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private FieldType fieldType;

}
