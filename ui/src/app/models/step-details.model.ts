/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */
import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {alias, custom, deserialize, serializable, SKIP} from 'serializr';
import {StepDetailsDataMap} from "./step-details-data-map.model";
import {TestStepType} from "../enums/test-step-type.enum";
import {TestStepPriority} from "../enums/test-step-priority.enum";
import {TestStepConditionType} from "../enums/test-step-condition-type.enum";

export class StepDetails extends Base implements Deserializable {
  @serializable
  public type: TestStepType;
  @serializable
  public conditionType: TestStepConditionType;
  @serializable
  public action: String;
  @serializable
  public order_id: Number;
  @serializable
  public priority: TestStepPriority;
  @serializable
  public natural_text_action_id: Number;
  @serializable
  public testDataParameterName: String;
  @serializable
  public testDataParameterValue: String;
  @serializable
  public maxIterations: number;
  @serializable
  public preRequisite: Number;
  @serializable(alias('wait_time'))
  public waitTime: Number;
  @serializable(alias('dataMap', custom(v => SKIP, v => {
      if(v) {
        return new StepDetailsDataMap().deserialize(v);
      }
    }
  )))
  public dataMap: StepDetailsDataMap;//TODO manual need to parse
  @serializable(alias('step_group_id'))
  public stepGroupId: number;

  @serializable
  public ignoreStepResult: boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(StepDetails, input));
  }

  get isConditionalIf(): Boolean {
    return this.conditionType == TestStepConditionType.CONDITION_IF;
  }

  get isConditionalElseIf(): Boolean {
    return this.conditionType == TestStepConditionType.CONDITION_ELSE_IF;
  }

  get isConditionalElse(): Boolean {
    return this.conditionType == TestStepConditionType.CONDITION_ELSE;
  }

  get isForLoop(): Boolean {
    return this.conditionType == TestStepConditionType.LOOP_FOR;
  }

  get isWhileLoop(): Boolean {
    return this.type== TestStepType.WHILE_LOOP;
  }

  get isConditionalWhile(): Boolean {
    return this.conditionType == TestStepConditionType.LOOP_WHILE;
  }

  get isActionText(): Boolean {
    return this.type == TestStepType.ACTION_TEXT;
  }

  get isStepGroup(): Boolean {
    return this.type == TestStepType.STEP_GROUP;
  }

  get isRestStep(): Boolean {
    return this.type == TestStepType.REST_STEP;
  }

  get testDataValue(): String {
    return this.dataMap && this.dataMap.testData || this.testDataParameterValue;
  }

  get isMajor(): boolean{
    return this.priority == TestStepPriority.MAJOR;
  };
}
