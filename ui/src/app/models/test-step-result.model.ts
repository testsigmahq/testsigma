/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {ResultBase} from "./result-base.model";
import {PageObject} from "../shared/models/page-object";
import {custom, deserialize, object, optional, serializable, SKIP} from 'serializr';
import {StepDetails} from "./step-details.model";
import {NaturalTextActions} from "./natural-text-actions.model";
import {TestStepResultMetadata} from "./test-step-result-metadata.model";
import {TestDataType} from "../enums/test-data-type.enum";
import {TestCase} from "./test-case.model";
import {Page} from "../shared/models/page";
import {StepResultScreenshotComparision} from "./step-result-screenshot-comparision.model";
import {TestStepPriority} from "../enums/test-step-priority.enum";
import {TestStep} from "./test-step.model";
import {AddonNaturalTextAction} from "./addon-natural-text-action.model";
import {AddonElementData} from "./addon-element-data.model";
import {AddonTestStepTestData} from "./addon-test-step-test-data.model";
import {TestDataDetails} from "./step-details-test-data-map.model";
import {ElementDetails} from "./element-details.model";

export class TestStepResult extends ResultBase implements PageObject {
  @serializable
  public envRunId: Number;
  @serializable
  public testCaseId: number;
  @serializable
  public stepId: Number;
  @serializable
  public stepGroupId: Number;
  @serializable
  public groupResultId: Number;
  @serializable
  public errorCode: Number;
  @serializable
  public message: String;
  @serializable(custom(v => SKIP, v => new TestStepResultMetadata().deserialize(v)))
  public metadata: TestStepResultMetadata;
  @serializable
  public parentResultId: Number;
  @serializable
  public screenshotName: String;
  @serializable
  public testCaseResultId: Number;
  @serializable
  public webDriverException: String;
  @serializable(optional(object(StepDetails)))
  public stepDetails: StepDetails;
  @serializable
  public screenShotURL: String;
  @serializable
  public waitTime: Number;
  @serializable
  public visualEnabled: Boolean;
  @serializable
  public priority: TestStepPriority;

  @serializable
  public addonActionLogs: String;
  @serializable
  public naturalTextActionId: Number;

  @serializable(custom(v => {
    if (!v)
      return v;
    let returnValue = new Map<string, AddonElementData>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new AddonElementData().deserialize(v[key]);
    }
    return v;
  }, v => {
    if (!v)
      return v;
    let returnValue = new Map<string, JSON>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new AddonElementData().serialize();
    }
    return v;
  }))
  addonElements: Map<string, AddonElementData>;

  @serializable(custom(v => {
    if (!v)
      return v;
    let returnValue = new Map<string, AddonTestStepTestData>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new AddonTestStepTestData().deserialize(v[key]);
    }
    return v;
  }, v => {
    if (!v)
      return v;
    let returnValue = new Map<string, JSON>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new AddonTestStepTestData().serialize();
    }
    return v;
  }))
  addonTestData: Map<string, AddonTestStepTestData>;

  @serializable(custom(v => SKIP, v => {
      return v;
    }
  ))
  public testDataDetails: Map<string, TestDataDetails>;
  @serializable(custom(v => {
    if (!v)
      return v;
    let returnValue = new Map<string, ElementDetails>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new ElementDetails().deserialize(v[key]);
    }
    return v;
  }, v => {
    if (!v)
      return v;
    let returnValue = new Map<string, JSON>();
    for (const key in v) {
      if (v.hasOwnProperty(key))
        returnValue[key] = new ElementDetails().serialize();
    }
    return v;
  }))
  public elementDetails: Map<string, ElementDetails>;

  public template: NaturalTextActions;
  public addonTemplate: AddonNaturalTextAction;
  public parentResult: TestStepResult;
  public stepGroup: TestCase;
  public stepGroupResults: Page<TestStepResult>;
  public stepResultScreenshotComparison: StepResultScreenshotComparision;
  public testStep: TestStep;
  public isEditing: Boolean;
  public isDelete: Boolean;
  public isElementChanged: Boolean;
  public testCase: TestCase;
  public childResult: TestStepResult;
  public stepDisplayNumber: any;
  public childIndex: number;
  public isOpen = false;

  get stepDetail(): StepDetails {
    return this.stepDetails || this.metadata.stepDetails;
  }

  get isVisualFailed(): Boolean {
    return this.stepResultScreenshotComparison && this.stepResultScreenshotComparison.diffCoordinates.length > 0;
  }

  get leftIndent(): number {
    let indent = (this.parentResult && !this.isConditionalWhileLoop) ? this.parentResult.leftIndent + 1 : 0;
    if (this.isConditionalElse || this.isConditionalElseIf) {
      indent -= 1
    }
    return indent;
  }

  get parsedStep(): String {
    let parsedStep = this.template.naturalText;
    parsedStep = this.replaceTestData(parsedStep, this.stepDetail && (this.stepDetail.dataMap.testDataType || this.metadata.testDataType || this.stepDetail.dataMap.testData));
    if (this.stepDetail && (this.stepDetail.dataMap.elementString || this.stepDetail.dataMap.fromElementString || this.stepDetail.dataMap.toElementString)) {
      parsedStep = this.replaceElement(parsedStep);
    }
    if (this.stepDetails && this.stepDetails.dataMap && this.stepDetails.dataMap.attribute) {
      parsedStep = this.replaceAttribute(parsedStep);
    }
    return parsedStep;
  }

  get parseAddonLogs() {
    let parsedStep = this.addonActionLogs.replace('\n', '<br/>');
    return parsedStep;
  }

  replaceAddonTestData(type, value) {
    switch (type) {
      case TestDataType.random:
        value = '~|' + value + '|';
        break;
      case TestDataType.runtime:
        value = '$|' + value + '|';
        break;
      case TestDataType.environment:
        value = '*|' + value + '|';
        break;
      case TestDataType.parameter:
        value = '@|' + value + '|';
        break;
      case TestDataType.function:
        value = '!|' + value + '|';
        break;
    }
    return value;
  }

  parsedAddonStep(addonTestData, addonElements, grammar?): String {
    let parsedStep = grammar;
    Object.keys(addonTestData).forEach(key => {
      let value = addonTestData[key]?.value;
      value = this.replaceAddonTestData(addonTestData[key]?.type, value);
      parsedStep = parsedStep.replace(key, '<TSTESTDAT ref="' + key + '">' + value + '</TSTESTDAT>')
    })
    Object.keys(addonElements).forEach(key => {
      parsedStep = parsedStep.replace(key, '<TSELEMENT ref="' + key + '">' + addonElements[key]?.name + '</TSELEMENT>')
    })
    Object.keys(addonTestData).forEach(key => {
      parsedStep = parsedStep.replace('<TSTESTDAT ref="' + key + '">', '<span class="test_data" data-reference="' + key + '">')
    })
    Object.keys(addonElements).forEach(key => {
      parsedStep = parsedStep.replace('<TSELEMENT ref="' + key + '">', '<span class="element" data-reference="' + key + '">')
    })
    parsedStep = parsedStep.replace(new RegExp('</TSTESTDAT>', 'g'), '</span>')
    parsedStep = parsedStep.replace(new RegExp('</TSELEMENT>', 'g'), '</span>')
    return parsedStep;
  }

  get isConditionalIf(): Boolean {
    return this.stepDetail && this.stepDetail.isConditionalIf;
  }

  get isConditionalElseIf(): Boolean {
    return this.stepDetail && this.stepDetail.isConditionalElseIf;
  }

  get isConditionalElse(): Boolean {
    return this.stepDetail && this.stepDetail.isConditionalElse;
  }

  get isForLoop(): Boolean {
    return this.stepDetail && this.stepDetail.isForLoop;
  }

  get isWhileLoop(): Boolean {
    return this.stepDetail && this.stepDetail.isWhileLoop;
  }

  get isConditionalWhileLoop(): Boolean {
    return this.stepDetail && this.stepDetail.isConditionalWhile;
  }

  get isStepGroup(): Boolean {
    return this.stepDetail && this.stepDetail.isStepGroup;
  }

  get isRestStep(): Boolean {
    return this.stepDetail && this.stepDetail.isRestStep;
  }

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestStepResult, input));
  }

  private replaceTestData(parsedStep: String, dataType): String {
    switch (dataType) {
      case TestDataType.raw:
        parsedStep = this.replaceTestDataRaw(parsedStep);
        break;
      case TestDataType.parameter:
        parsedStep = this.replaceTestDataParameter(parsedStep);
        break;
      case TestDataType.runtime:
        parsedStep = this.replaceTestDataRuntime(parsedStep);
        break;
      case TestDataType.environment:
        parsedStep = this.replaceTestDataEnvironment(parsedStep);
        break;
      case TestDataType.random:
        parsedStep = this.replaceTestDataRandom(parsedStep);
        break;
      case TestDataType.function:
        parsedStep = this.replaceTestDataFunction(parsedStep);
        break;
      default://TODO this.stepDetail.dataMap.testData this is not stored test data type[JAYAVEL S]
        parsedStep = this.replaceTestDataRaw(parsedStep);
        break;
    }
    return parsedStep;
  }

  private replaceTestDataRandom(parsedStep: String): String {
    let testData = this.stepDetail && this.stepDetail.dataMap ? this.stepDetail.dataMap.testData : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>~|" + testData + "|</span>");
  }

  private replaceTestDataFunction(parsedStep: String): String {
    let testData = this.stepDetail && this.stepDetail.dataMap ? this.stepDetail.dataMap.testData : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>!|" + testData + "|</span>");
  }

  private replaceTestDataEnvironment(parsedStep: String): String {
    let testData = this.stepDetail && this.stepDetail.dataMap ? this.stepDetail.dataMap.testData : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>*|" + testData + "|</span>");
  }


  private replaceTestDataRuntime(parsedStep: String): String {
    let testData = this.stepDetail && this.stepDetail.dataMap ? this.stepDetail.dataMap.testData : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>$|" + testData + "|</span>");
  }

  private replaceTestDataParameter(parsedStep: String): String {
    let testData = this.stepDetail && this.stepDetail.dataMap ? this.stepDetail.dataMap.testData : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>@|" + testData + "|</span>");
  }

  private replaceTestDataRaw(parsedStep: String): String {
    let testData = this.stepDetail && this.stepDetail.dataMap ? this.stepDetail.dataMap.testData : '';
    let span_class= this.template.allowedValues?'action-selected-data':'action-test-data';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class="+span_class+">" + this.getTestData(testData)+ "</span>");
  }

  private replaceElement(parsedStep: String): String {
    let elementString = this.stepDetail && this.stepDetail.dataMap ? this.stepDetail.dataMap.elementString : '';
    if (Boolean(elementString))
      return parsedStep.replace(new RegExp("#{.*?}"), "<span class='action-element'>" + elementString + "</span>");
    else if (this.stepDetail && this.stepDetail.dataMap) {
      let fromAndToElement = parsedStep.replace(new RegExp("#{.*?}"), "<span class='action-element'>" + this.stepDetail.dataMap.fromElementString + "</span>");
      return fromAndToElement.replace(new RegExp("#{.*?}"), "<span class='action-element'>" + this.stepDetail.dataMap.toElementString + "</span>");
    }
  }

  private replaceAttribute(parsedStep: String): String {
    let attributeString = this.stepDetail && this.stepDetail.dataMap ? this.stepDetail.dataMap.attribute : '';
    return parsedStep.replace(new RegExp("@{.*?}"), "<span class='action-attribute'>" + attributeString + "</span>");
  }

  get canShowFixElement(): Boolean {
    return this.isFailed && !(this.isStepGroup || this.isForLoop || this.isRestStep || this.stepGroup) && !!this.metadata.element;
  }

  get canShowMatchNotMatchLabel() {
    return this.isConditionalIf || this.isConditionalElseIf || this?.testStep?.isBreakLoop || this?.testStep?.isContinueLoop || this?.testStep?.isConditionalWhileLoop;
  }

  get isBreakContinueLoopStep() {
    return !(this.testStep.isContinueLoop || this.testStep.isBreakLoop)
  }

  get canShowConditionalStepActions() {
    return (this?.isConditionalIf || this?.isConditionalElse || this?.isConditionalElseIf || this?.isConditionalWhileLoop)
  }

  get canShowIgnoreStepResultLabel() {
    return !(this.stepDetail?.isConditionalIf || this.stepDetail?.isConditionalElse || this.stepDetail?.isConditionalElseIf || this.stepDetail?.isConditionalWhile || this.stepDetail?.isForLoop)
  }

  setResultStepDisplayNumber(testStepResults: TestStepResult[]) {
    let nestedIndex = 0;
    testStepResults.forEach((step: TestStepResult, index) => {
      step.stepDisplayNumber = (index + 1);
      if (step.parentResultId) {
        step.parentResult = testStepResults.find(res => step.parentResultId == res.id);
      }
      if ((step.isConditionalElseIf || step.isConditionalElse || step.isConditionalIf || step.isForLoop || step.isConditionalWhileLoop)) {
        step.childIndex = 0;
      }
      let _parentStep = this.getConditionalParentStep(step);
      if (step.parentResult && (!(step.isConditionalElseIf || step.isConditionalElse) || _parentStep)) {
        if (_parentStep && (step.isConditionalElseIf || step.isConditionalElse)) {
          _parentStep.childIndex = _parentStep.childIndex + 1;
        } else {
          step.parentResult.childIndex = (step.parentResult.childIndex | 0) + 1;
        }
      }
      if (step.parentResult && !(step.isConditionalElseIf || step.isConditionalElse)) {
        step.stepDisplayNumber = step.parentResult?.stepDisplayNumber + "." + step.parentResult.childIndex;
        ++nestedIndex;
      } else if (step.isConditionalElseIf || step.isConditionalElse) {
        step.stepDisplayNumber = step.incrementParentStepDisplayNumberLastDigit();
        if (step?.parentResult && this.getConditionalParentStep(step))
          ++nestedIndex;
      } else {
        step.stepDisplayNumber = (index + 1 - nestedIndex);
      }

    });

  }

  getConditionalParentStep(testStep: TestStepResult) {
    if (testStep.parentResult) {
      if (testStep.parentResult?.isConditionalIf) {
        return testStep.parentResult?.parentResult;
      } else {
        return this.getConditionalParentStep(testStep.parentResult);
      }
    } else {
      return false;
    }
  }

  incrementParentStepDisplayNumberLastDigit() {
    if (this.parentResult?.stepDisplayNumber?.toString()?.indexOf(".") > -1) {
      let array = this.parentResult.stepDisplayNumber.split(".");
      let lastDigit = parseInt(array[array.length - 1]);
      array = array.slice(0, array.length - 1);
      return array.join(".") + "." + (lastDigit + 1);
    } else {
      return this.parentResult?.stepDisplayNumber + 1;
    }
  }
  get isCoordinateStep() {
    let template_ids = [1060, 10164, 20091, 20139, 20164, 30090, 30128, 30162];
    return template_ids.includes(this.testStep.naturalTextActionId as number);
  }

  formatCoordinates(coordinates: string) {
    if(!this.isCoordinateStep) return (coordinates || '');
    return (coordinates || '').split(/\s*,\s*/).map(num=> parseFloat(num).toFixed(2)).join(', ');
  }
  private getTestData(value: any) {
    return this.isCoordinateStep? this.formatCoordinates(value):value;
  }

}
