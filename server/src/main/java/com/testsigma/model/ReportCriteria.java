package com.testsigma.model;


import com.testsigma.specification.SearchOperation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "report_criteria")
@Data
@ToString
@EqualsAndHashCode
public class ReportCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @Fetch(value = FetchMode.SELECT)
    @JoinColumn(name = "criteria_field", referencedColumnName = "id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private FieldConfiguration criteriaField;

    @Column(name = "criteria_value")
    private String criteriaValue;

    @Column(name = "criteria_condition")
    @Enumerated(EnumType.STRING)
    private SearchOperation criteriaCondition;

    @OneToMany(mappedBy = "criteria", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    List<ReportConfigurationCriteriaMappings> reportCriteriaMappings;
}
