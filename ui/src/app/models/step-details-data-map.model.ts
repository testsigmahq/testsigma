/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */
import {Base} from "../shared/models/base.model";
import {Deserializable} from "../shared/models/deserializable";
import {alias, custom, deserialize, list, object, optional, primitive, serializable, serialize} from 'serializr';
import {ResultConstant} from "../enums/result-constant.enum";
import {TestDataType} from "../enums/test-data-type.enum";
import {TestStepForLoop} from "./test-step-for-loop.model";
import {TestStepTestDataFunction} from "./test-step-test-data-function.model";
import {CustomStep} from "./custom-step.model";
import {AddonTestStepTestData} from "./addon-test-step-test-data.model";
import { TestDataMapValue } from "./test-data-map-value.model";

export class StepDetailsDataMap extends Base implements Deserializable {
  @serializable(optional(list(primitive())))
  public conditionIf: ResultConstant[];
  @serializable(optional())
  public whileCondition: String;
  @serializable(custom(v => {
    if(!v){
      return v;
    }
    let returnData = new Map<string, TestDataMapValue>();
    Object.keys(v).forEach(item => {
      returnData[item] = v[item].serialize();
    })
    return returnData;
  }, v => {
    if(!v){
      return v;
    }
    let returnData = new Map<string, TestDataMapValue>();
    Object.keys(v).forEach(item => {
      returnData[item] = new TestDataMapValue().deserialize(v[item]);
    })
    return returnData;
  }))
  public testData: Map<string, TestDataMapValue>;
  @serializable(alias('test-data-function', optional(custom(v => {
    if (!v)
      return v;
    return v.serialize();
  }, v => {
    return new TestStepTestDataFunction().deserialize(v)
  }))))
  public testDataFunction: TestStepTestDataFunction;
  @serializable(alias('addon_test_data_function', optional(custom(v => {
    if (!v)
      return v;
    return v.serialize();
  }, v => {
    return new AddonTestStepTestData().deserialize(v)
  }))))
  addonTDF: AddonTestStepTestData;
  @serializable(alias('custom-step', optional(custom(v => {
    if (!v)
      return v;
    return v.serialize()
  }, v => {
    return new CustomStep().deserialize(v)
  }))))
  public customStep: CustomStep;
  @serializable
  public testDataType: TestDataType;
  @serializable(alias('element',optional()))
  public elementString: String;
  @serializable(alias('fromElement',optional()))
  public fromElementString: String;
  @serializable(alias('to-element',optional()))
  public toElementString: String;
  @serializable
  public attribute: String;
  @serializable(alias('for_loop', optional(object(TestStepForLoop))))
  public forLoop: TestStepForLoop;

  deserialize(input: any): this {
    let conditionIf: ResultConstant[] = [];
    if (input['conditionIf'] instanceof Array)
      input['conditionIf'].forEach(key => {
        if (key == "0")
          conditionIf.push(ResultConstant.SUCCESS)
        else if (key == "1")
          conditionIf.push(ResultConstant.FAILURE)
        else if (key == "2")
          conditionIf.push(ResultConstant.ABORTED)
        else if (key == "3")
          conditionIf.push(ResultConstant.NOT_EXECUTED)
        else if (key == "4")
          conditionIf.push(ResultConstant.QUEUED)
        else if (key == "5")
          conditionIf.push(ResultConstant.STOPPED)
      });
    this.conditionIf = conditionIf;
    if (input['test-data-type'])
      input['testDataType'] = input['test-data-type'];
    if (input['test-data'])
      input['testData'] = input['test-data'];
    if (input['element'])
      input['elementString'] = input['element'];
    if (input['from-element'])
      input['fromElementString'] = input['fromElement'];
    if (input['to-element'])
      input['toElementString'] = input['toElement'];
    if (input['toElementString'])
      input['to-element'] = input['toElementString'];
    if (input['fromElementString'])
      input['from-element'] = input['fromElementString'];
    if(input['testDataFunction'])
      input['test-data-function'] = input['testDataFunction']
    if(input['addonTDF'])
      input['addon_test_data_function'] = input['addonTDF']
    if(input['visual_enabled'])
      input['visual_enabled'] = input['visualEnabled']
    return Object.assign(this, deserialize(StepDetailsDataMap, input));
  }

  public serialize(): JSON {
    let output = serialize(this);
    let conditionIf: string[] = [];
    this.conditionIf?.forEach(key => {
      if (key == ResultConstant.SUCCESS)
        conditionIf.push("0")
      else if (key == ResultConstant.FAILURE)
        conditionIf.push("1")
      else if (key == ResultConstant.ABORTED)
        conditionIf.push("2")
      else if (key == ResultConstant.NOT_EXECUTED)
        conditionIf.push("3")
      else if (key == ResultConstant.QUEUED)
        conditionIf.push("4")
      else if (key == ResultConstant.STOPPED)
        conditionIf.push("5")
    });
    output['condition_if'] = conditionIf;
    output['test-data-type'] = this.testDataType;
    output['test-data'] = this.testData;
    output['element'] = this.elementString;
    if(this.fromElementString)
      output['from-element'] = this.fromElementString;
    if(this.toElementString)
      output['to-element'] = this.toElementString;
    delete output.conditionIf;
    delete output.testDataType;
    delete output.testData;
    delete output.fromElement;
    delete output.toElement;
    return output;
  }

  get isTestDataTypeOfParameterType() {
    return this.testDataType && this.testDataType == TestDataType.parameter;
  }
}
