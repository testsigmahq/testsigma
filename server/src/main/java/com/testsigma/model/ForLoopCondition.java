package com.testsigma.model;

import com.testsigma.dto.LoopDataMapDTO;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import javax.persistence.*;

@Entity
@Table(name = "for_step_conditions")
@Data
@EqualsAndHashCode(callSuper = false)
@Log4j2
public class ForLoopCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_case_id")
    private Long testCaseId;

    @Column(name = "test_step_id")
    private Long testStepId;

    @Column(name = "test_data_profile_id")
    private Long testDataProfileId;

    @Column(name = "iteration_type")
    @Enumerated(EnumType.STRING)
    private IterationType iterationType;

    @Column(name = "left_param_type")
    @Enumerated(EnumType.STRING)
    private TestDataType leftParamType;

    @Column(name = "left_param_value")
    private String leftParamValue;

    @Column(name = "operator")
    @Enumerated(EnumType.STRING)
    private Operator operator;

    @Column(name = "right_param_value")
    private String rightParamValue;

    @Column(name = "right_param_type")
    @Enumerated(EnumType.STRING)
    private TestDataType rightParamType;

    @Column(name = "test_data")
    private String testData;

    @Column(name = "left_function_id")
    private Long leftFunctionId;

    @Column(name = "right_function_id")
    private Long rightFunctionId;

    @Column(name = "left_data_map")
    private String leftDataMap;

    @Column(name = "right_data_map")
    private String rightDataMap;

    @Column(name = "copied_from")
    private Long copiedFrom;

    @Column(name = "imported_id")
    private Long importedId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ForLoopConditionType type = ForLoopConditionType.ORIGINAL;

    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JoinColumn(name = "test_step_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TestStep testStep;

    public LoopDataMapDTO getLeftDataMap() {
        ObjectMapperService mapperService = new ObjectMapperService();
        try {
            return mapperService.parseJsonModel(leftDataMap, LoopDataMapDTO.class);
        } catch(Exception e) {
            log.error(e,e);
        }
        return null;
    }

    public LoopDataMapDTO getRightDataMap() {
        ObjectMapperService mapperService = new ObjectMapperService();
        try {
            return mapperService.parseJsonModel(rightDataMap, LoopDataMapDTO.class);
        } catch(Exception e) {
            log.error(e,e);
        }
        return null;
    }

    public void setLeftDataMap(LoopDataMapDTO functionData) {
        this.leftDataMap = new ObjectMapperService().convertToJson(functionData);
    }

    public void setRightDataMap(LoopDataMapDTO functionData) {
        this.rightDataMap = new ObjectMapperService().convertToJson(functionData);
    }

}