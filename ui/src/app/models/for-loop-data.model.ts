import {Base} from "../shared/models/base.model";
import {custom, deserialize, optional, serializable} from 'serializr';
import {TestDataType} from "../enums/test-data-type.enum";
import {SearchOperator} from "../enums/search-operator.enum";
import {LoopIterationType} from "../enums/loop-iteration-type.enum";
import {LoopDataMap} from "./loop-data-map.model";
import {TestStep} from "./test-step.model";
import {TestData} from "./test-data.model";
import {TestStepTestDataFunction} from "./test-step-test-data-function.model";
import {AddonTestStepTestData} from "./addon-test-step-test-data.model";

export class ForLoopData extends Base{
  @serializable
  public testCaseId: Number;
  @serializable(optional())
  public testStepId: number;
  @serializable
  public testDataProfileId: Number;
  @serializable
  public iterationType: LoopIterationType;
  @serializable
  public leftParamType: TestDataType;
  @serializable
  public leftParamValue: String;
  @serializable(custom(v => {
    if(v) {
      switch (v){
        case "contains":
          return SearchOperator.CONTAINS;
          break
        case "startwith":
          return SearchOperator.STARTS_WITH;
          break
        case "Endswith":
          return SearchOperator.ENDS_WITH;
          break
        case "Equals":
          return SearchOperator.EQUALS;
          break
        case "IN":
          return SearchOperator.IN;
          break
        case "isEmpty":
          return SearchOperator.IS_EMPTY;
          break
        case "isNotEmpty":
          return SearchOperator.IS_NOT_EMPTY;
          break
      }
    } else {
      return v
    }
  }, v => {
    if(v) {
      switch (v){
        case SearchOperator.CONTAINS:
        case "contains":
          return "contains";
          break
        case SearchOperator.STARTS_WITH:
        case "startwith":
          return "startwith";
          break
        case SearchOperator.ENDS_WITH:
        case "Endswith":
          return "Endswith";
          break
        case SearchOperator.EQUALS:
        case "Equals":
          return "Equals";
          break
        case SearchOperator.IN:
        case "IN":
          return "IN";
          break
        case SearchOperator.IS_EMPTY:
        case "isEmpty":
          return "isEmpty";
          break
        case SearchOperator.IS_NOT_EMPTY:
        case "isNotEmpty":
          return "isNotEmpty";
          break
      }
    } else {
      return v
    }
  }))
  public operator: SearchOperator;
  @serializable
  public rightParamValue: String;
  @serializable
  public rightParamType: TestDataType;
  @serializable
  public testData: String;
  @serializable
  public testDataType: TestDataType;
  @serializable(optional(custom(v=> {
    if(v){
      return v.serialize();
    } else {
      return v;
    }

  }, v => {
    if(v) {
      return new LoopDataMap().deserialize(v)
    } else{
      return v
    }
  } )))
  public testDataMap: LoopDataMap;
  @serializable(optional(custom(v=> {
    if(v){
      return v.serialize();
    } else {
      return v;
    }

  }, v => {
    if(v) {
      return new LoopDataMap().deserialize(v)
    } else{
      return v
    }
  } )))
  public leftDataMap: LoopDataMap;
  @serializable(optional(custom(v=> {
    if(v){
      return v.serialize();
    } else {
      return v;
    }

  }, v => {
    if(v) {
      return new LoopDataMap().deserialize(v)
    } else{
      return v
    }
  } )))
  public rightDataMap: LoopDataMap;
  public testDataProfileData: TestData;
  public testDataProfileName: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ForLoopData, input));
  }

  deserializeRawValue(input: JSON, step: TestStep): this {
    let inputData= {};
    inputData['operator'] = input['operator']?.value?.includes('/') ?
      (input['operator']?.value?.includes('contains') ? SearchOperator.CONTAINS :
        SearchOperator.IS_NOT_EMPTY) : input['operator']?.value;
    inputData = this.assignDataForFields(input, 'right', 'right-data', inputData);
    inputData = this.assignDataForFields(input, 'left', 'left-data', inputData);
    inputData['testData'] = input['testData']?.value;
    inputData['testDataType'] = TestDataType[input['testData']?.type];
    inputData['testDataMap'] = input['testData']?.testDataFunction;
    inputData['testDataProfileId'] = <Number>input['test-data-profile']?.value;
    inputData['testCaseId'] = step.testCaseId;
    inputData['iterationType'] = this.setIterationType(step, inputData);
    if(step?.id)
      inputData['testStepId'] = step.id;
    let returnValue = Object.assign(this, deserialize(ForLoopData, inputData));
    return returnValue;
  }

  assignDataForFields(input: JSON, inputKey: string, key: string, inputData: Object) {
    let data = input[key];
    inputData[inputKey+'ParamType'] = TestDataType[data?.type] || TestDataType.raw;
    inputData[inputKey+'ParamValue'] = data?.value == 'start' && key == 'left-data' &&
    inputData[inputKey+'ParamType'] == TestDataType.raw ? "-1" : data?.value == 'end' &&
    key == 'right-data' && inputData[inputKey+'ParamType'] == TestDataType.raw ? "-1" : data?.value;
    if(data?.testDataFunction) {
      inputData[inputKey + 'DataMap'] = new LoopDataMap();
      inputData[inputKey + 'DataMap']['testDataMap'] = new TestStepTestDataFunction().deserialize(data?.testDataFunction);
    }
    if(data?.kibbutzTDF) {
      inputData[inputKey + 'DataMap'] = new LoopDataMap();
      inputData[inputKey + 'DataMap']['kibbutzPluginTDFEntityList'] = new AddonTestStepTestData().deserialize(data?.kibbutzTDF);
    }
    return inputData;
  }

  setIterationType(step: TestStep, inputData) {
    let type = LoopIterationType.SET_NAME;
    if(step?.template?.data?.testData)
      Object.keys(step?.template?.data?.testData)?.forEach(data => {
        if(data == 'left-data') {
          if (step?.template?.data?.testData[data]?.includes('parameter')) {
            type = LoopIterationType.PARAMETER_VALUE;
          } else if (step?.template?.data?.testData[data]?.includes('start') && !step?.template?.data?.testData[data]?.includes('start-set-name')) {
            type = LoopIterationType.INDEX;
          }
        }
      })
    if(!(!!step?.template?.data?.testData?.['left-data'] || !!step?.template?.data?.testData?.['right-data'])){
      type = LoopIterationType.INDEX;
      inputData['leftParamValue'] = -1;
      inputData['rightParamValue'] = -1;
    }
    return type;
  }

  setValuesParsed(type) {
    let returnData = {};
    switch (type) {
      case "test-data-profile":
        returnData['value'] = this.testDataProfileData?.name || this.testDataProfileName;
        break;
      case "left-data":
        returnData['value'] = this.leftParamValue?.toLowerCase() == "-1" && this.leftParamType == TestDataType.raw ? 'start' : this.leftParamValue;
        returnData['type'] = this.leftParamType;
        returnData['function'] = this.leftDataMap;
        break;
      case "right-data":
        returnData['value'] = this.rightParamValue?.toLowerCase() == "-1" && this.leftParamType == TestDataType.raw ? 'end' : this.rightParamValue;
        returnData['type'] = this.rightParamType;
        returnData['function'] = this.rightDataMap;
        break;
      case "testData":
        returnData['value'] = this.testData;
        returnData['type'] = this.testDataType;
        returnData['function'] = this.testDataMap;
        break;
      case "operator":
        returnData['value'] = this.operator;
    }
    return returnData;
  }
}
