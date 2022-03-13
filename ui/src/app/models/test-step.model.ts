import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, list, object, optional, primitive, serializable, serialize} from "serializr";
import {TestDataType} from "../enums/test-data-type.enum";
import {NaturalTextActions} from "./natural-text-actions.model";
import {TestCase} from "./test-case.model";
import {TestStepType} from "../enums/test-step-type.enum";
import {TestStepConditionType} from "../enums/test-step-condition-type.enum";
import {TestStepPriority} from "../enums/test-step-priority.enum";
import {Page} from "../shared/models/page";
import {TestData} from "./test-data.model";
import {RestStepEntity} from "./rest-step-entity.model";
import {AddonNaturalTextAction} from "./addon-natural-text-action.model";
import {AddonTestStepTestData} from "./addon-test-step-test-data.model";
import {AddonElementData} from "./addon-element-data.model";
import {ResultConstant} from "../enums/result-constant.enum";
import {AddonNaturalTextActionParameter} from "./addons-parameter.model";
import {StepDetailsDataMap} from "./step-details-data-map.model";

export class TestStep extends Base implements PageObject {
  @serializable
  public testCaseId: Number;
  @serializable
  public position: number;
  @serializable
  public parentId: Number;
  @serializable
  public copiedFrom: number;
  @serializable
  public type: TestStepType;
  @serializable(custom(v => v, v => {
    switch (v) {
      case 1:
      case TestStepConditionType.CONDITION_IF:
        return TestStepConditionType.CONDITION_IF;
      case 3 :
      case  TestStepConditionType.CONDITION_ELSE_IF:
        return TestStepConditionType.CONDITION_ELSE_IF;
      case 5:
      case  TestStepConditionType.CONDITION_ELSE:
        return TestStepConditionType.CONDITION_ELSE;
      case 6:
      case  TestStepConditionType.LOOP_FOR:
        return TestStepConditionType.LOOP_FOR;
      case 7:
      case TestStepConditionType.LOOP_WHILE:
        return TestStepConditionType.LOOP_WHILE;
    }
  }))
  public conditionType: TestStepConditionType;
  @serializable
  public action: String;
  @serializable
  public priority: TestStepPriority;
  @serializable
  public naturalTextActionId: Number;
  @serializable
  public stepGroupId: number;
  @serializable
  public testDataParameterName: String;
  @serializable
  public testDataParameterValue: String;
  @serializable
  public preRequisiteStepId: Number;
  @serializable
  public waitTime: Number;

  @serializable(optional(list(primitive())))
  public conditionIf: ResultConstant[];
  @serializable(alias("testData"))
  public testDataVal: String;
  @serializable
  public testDataType: TestDataType;
  @serializable
  public attribute: String;
  @serializable
  public element: String;
  @serializable
  public forLoopStartIndex: number;
  @serializable
  public forLoopEndIndex: number;
  @serializable
  public forLoopTestDataId: number;
  @serializable
  public testDataFunctionId: number;
  @serializable(custom(v => v, v => v))
  public testDataFunctionArgs: JSON;


  @serializable
  public exceptedResult: String;
  @serializable(optional(object(RestStepEntity)))
  public restStep: RestStepEntity;

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
  @serializable( optional(custom(v => {
    if (!v)
      return v;
    return v.serialize();
  }, v => {
    if(!v)
      return v;
    return new AddonTestStepTestData().deserialize(v)
  })))
  addonTDF: AddonTestStepTestData;
  @serializable(optional())
  public addonActionId: number;

  @serializable(custom(v => {
    if (v == null)
      return false;
    return v;
  }, v => v))
  public disabled: boolean;

  public template: NaturalTextActions;
  public addonTemplate: AddonNaturalTextAction;

  @serializable(optional(object(TestStep)))
  public parentStep: TestStep;
  public childStep: TestStep;
  public stepGroup: TestCase;
  public stepGroupSteps: Page<TestStep>;
  public isSelected: boolean;
  public isStepsExpanded: boolean;
  public testData: TestData;
  public isDirty: boolean;
  public removeFromDom: boolean;
  public highlight: boolean;
  public preRequisiteStep: TestStep;
  public isEditing: boolean;
  public isAfter: boolean;
  public isBefore: boolean;
  public siblingStep: TestStep;
  public isNeedToUpdate: boolean;
  public stepDisplayNumber: any;
  public childIndex: number;

  get leftIndent(): number {
    let indent = this.parentStep ? this.parentStep.leftIndent + 1 : 0;
    if (this.isConditionalElse || this.isConditionalElseIf || this.isConditionalWhileLoop) {
      indent -= 1
    }
    return indent;
  }

  get parsedStep(): String {
    let parsedStep = this.template.naturalText;
    parsedStep = this.replaceTestData(parsedStep, this?.testDataType);
    if (this?.element) {
      parsedStep = this.replaceElement(parsedStep);
    }
    if (this?.attribute) {
      parsedStep = this.replaceAttribute(parsedStep);
    }
    return parsedStep;
  }

  get parsedAddonStep(): String {
    let parsedStep = this.addonTemplate.naturalText;
    if (this.addonTestData && this.addonElements)
      this.addonTemplate.parameters?.forEach(parameter => {
        let referenceName = new RegExp(parameter.reference);
        if (parameter.isTestData) {
          let value = this.addonTestData[parameter.reference]?.value;
          switch (this.addonTestData[parameter.reference]?.type) {
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
          parsedStep = parsedStep.replace(referenceName, '<TSTESTDAT ref="' + parameter.reference + '">' + value + '</TSTESTDAT>')
        } else if (parameter.isElement) {
          parsedStep = parsedStep.replace(referenceName, '<TSELEMENT ref="' + parameter.reference + '">' + this.addonElements[parameter.reference]?.name + '</TSELEMENT>')
        }
      })

    this.addonTemplate.parameters?.forEach((parameter: AddonNaturalTextActionParameter) => {
      if (parameter.isTestData) {
        parsedStep = parsedStep.replace('<TSTESTDAT ref="' + parameter.reference + '">', '<span class="test_data" data-reference="' + parameter.reference + '">')
      } else if (parameter.isElement) {
        parsedStep = parsedStep.replace('<TSELEMENT ref="' + parameter.reference + '">', '<span class="element" data-reference="' + parameter.reference + '">')
      }
    })
    parsedStep = parsedStep.replace(new RegExp('</TSTESTDAT>', 'g'), '</span>')
    parsedStep = parsedStep.replace(new RegExp('</TSELEMENT>', 'g'), '</span>')
    return parsedStep;
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
    return this.type == TestStepType.WHILE_LOOP;
  }

  get isBreakLoop(): Boolean {
    return this.type == TestStepType.BREAK_LOOP;
  }

  get isContinueLoop(): Boolean {
    return this.type == TestStepType.CONTINUE_LOOP;
  }

  get isConditionalWhileLoop(): Boolean {
    return this.conditionType == TestStepConditionType.LOOP_WHILE;
  }

  get isConditionalType(): Boolean {
    return this.conditionType == TestStepConditionType.LOOP_FOR ||
      this.conditionType == TestStepConditionType.CONDITION_ELSE ||
      this.conditionType == TestStepConditionType.CONDITION_ELSE_IF ||
      this.conditionType == TestStepConditionType.CONDITION_IF ||
      this.conditionType == TestStepConditionType.LOOP_WHILE
  }

  get isAction(): Boolean {
    return this.type == TestStepType.ACTION_TEXT;
  }

  get isStepGroup(): Boolean {
    return this.type == TestStepType.STEP_GROUP;
  }

  get isRestStep(): Boolean {
    return this.type == TestStepType.REST_STEP;
  }

  get isForLoopStep(): Boolean {
    return this.type == TestStepType.FOR_LOOP;
  }

  get testDataId(): Number {
    if (this.isForLoop && this.forLoopTestDataId)
      return this.forLoopTestDataId;
  }

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

    return Object.assign(this, deserialize(TestStep, input));
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
    output["conditionIf"] = conditionIf;
    return output;
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
      default://TODO this.dataMap.testData this is not stored test data type[JAYAVEL S]
        parsedStep = this.replaceTestDataRaw(parsedStep);
        break;
    }
    return parsedStep;
  }

  private replaceTestDataRandom(parsedStep: String): String {
    let testData = this.testDataVal ? this.testDataVal : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>~|" + testData + "|</span>");
  }

  private replaceTestDataFunction(parsedStep: String): String {
    let testData = this.testDataVal ? this.testDataVal : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>!|" + testData + "|</span>");
  }

  private replaceTestDataEnvironment(parsedStep: String): String {
    let testData = this.testDataVal ? this.testDataVal : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>*|" + testData + "|</span>");
  }

  private replaceTestDataRuntime(parsedStep: String): String {
    let testData = this.testDataVal ? this.testDataVal : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>$|" + testData + "|</span>");
  }

  private replaceTestDataParameter(parsedStep: String): String {
    let testData = this.testDataVal ? this.testDataVal : '';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class='action-test-data'>@|" + testData + "|</span>");
  }

  private replaceTestDataRaw(parsedStep: String): String {
    let testData = this.testDataVal ? this.testDataVal : '';
    let span_class= this.template.allowedValues?'action-selected-data':'action-test-data';
    return parsedStep.replace(new RegExp("\\${.*?}"), "<span class="+span_class+">"+testData+"</span>")
  }

  private replaceElement(parsedStep: String): String {
    let element = this.element ? this.element : '';
    return parsedStep.replace(new RegExp("#{.*?}"), "<span class='action-element'>" + element + "</span>");
  }

  private replaceAttribute(parsedStep: String): String {
    let attributeString = this.attribute ? this.attribute : '';
    return parsedStep.replace(new RegExp("@{.*?}"), "<span class='action-attribute'>" + attributeString + "</span>");
  }

  get testDataValue(): String {
    return this.testDataVal || this.testDataParameterValue;
  }

  get draggable(): boolean {
    return !(this.isConditionalElse || this.isConditionalElseIf || this.isConditionalIf || this.isForLoop || this.isConditionalWhileLoop || this.isWhileLoop || (this.parentStep && this.parentStep.isForLoop && this.isTestDataTypeOfParameterType));
  }

  deserializeCommonProperties(input: JSON) {

    if (input['waitTime']) {
      this.priority = input['priority'];
      this.waitTime = input['waitTime'];
      this.preRequisiteStepId = input['preRequisiteStepId'];
      this.conditionType = input['conditionType'];
      this.disabled = input['disabled'];
      if (input['dataMap'])
        this.conditionIf = input['conditionIf'];
    }
  }

  get isAddonAction() {
    return this.addonActionId;
  }

  getParentLoopDataId(testStep, testCase) {
    if (testStep.parentStep) {
      if (testStep.parentStep.isForLoop) {
        return testStep.parentStep?.forLoopTestDataId;
      } else {
        return this.getParentLoopDataId(testStep.parentStep, testCase);
      }
    } else {
      return testCase?.testDataId;
    }
  }

  getParentLoopId(testStep) {
    if (testStep.parentStep) {
      if (testStep.parentStep.isConditionalWhileLoop || testStep.parentStep.isForLoop) {
        return testStep.parentStep.id;
      } else {
        return this.getParentLoopId(testStep.parentStep);
      }
    } else {
      return false;
    }
  }

  get isTestDataTypeOfParameterType() {
    return this.testDataType && this.testDataType == TestDataType.parameter;
  }

  getConditionalParentStep(testStep) {
    if (testStep.parentStep) {
      if (testStep.parentStep?.isConditionalIf) {
        return testStep.parentStep?.parentStep;
      } else {
        return this.getConditionalParentStep(testStep.parentStep);
      }
    } else {
      return false;
    }
  }

  setStepDisplayNumber(testSteps: TestStep[]) {
    console.log(testSteps);
    let nestedIndex = 0;
    testSteps.forEach((step: TestStep, index) => {
      step.stepDisplayNumber = (index + 1);
      if (step.parentId) {
        step.parentStep = testSteps.find(res => step.parentId == res.id);
      }
      if ((step.isConditionalElseIf || step.isConditionalElse || step.isConditionalIf || step.isForLoop || step.isWhileLoop || step.isConditionalWhileLoop)) {
        step.childIndex = 0;
      }
      let _parentStep = this.getConditionalParentStep(step);
      if (step.parentStep && (!(step.isConditionalElseIf || step.isConditionalElse) || _parentStep)) {
        if (_parentStep && (step.isConditionalElseIf || step.isConditionalElse)) {
          _parentStep.childIndex = _parentStep.childIndex + 1;
        } else {
          step.parentStep.childIndex = step.parentStep.childIndex + 1;
        }
      }
      if (step.parentStep && !(step.isConditionalElseIf || step.isConditionalElse)) {

        if(step.isConditionalWhileLoop) {
          step.stepDisplayNumber = step.parentStep?.stepDisplayNumber;
        } else {
          step.stepDisplayNumber = step.parentStep?.stepDisplayNumber + "." + step.parentStep.childIndex;
        }
        ++nestedIndex;
      } else if (step.isConditionalElseIf || step.isConditionalElse) {
        step.stepDisplayNumber = step.incrementParentStepDisplayNumberLastDigit();
        if (step?.parentStep && this.getConditionalParentStep(step))
          ++nestedIndex;
      } else {
        step.stepDisplayNumber = (index + 1 - nestedIndex);
        // if(!step.parentStep && (step.isConditionalWhileLoop || step.isConditionalIf || step.isForLoop))
        //   ++nestedIndex;
        }
    })
  }

  incrementParentStepDisplayNumberLastDigit(conditionalSibling?: boolean, testStep?: TestStep) {
    let step = Boolean(conditionalSibling) ? testStep : this.parentStep;
    if (step?.stepDisplayNumber?.toString()?.indexOf(".") > -1) {
      let array = step.stepDisplayNumber.split(".");
      let lastDigit = parseInt(array[array.length - 1]);
      array = array.slice(0, array.length - 1);
      return array.join(".") + "." + (lastDigit + 1);
    } else {
      return step?.stepDisplayNumber + 1;
    }
  }

  decrementParentStepDisplayNumberLastDigit() {
    if (this.parentStep?.stepDisplayNumber?.toString()?.indexOf(".") > -1) {
      let array = this.parentStep.stepDisplayNumber.split(".");
      let lastDigit = parseInt(array[array.length - 1]);
      array = array.slice(0, array.length - 1);
      return array.join(".") + "." + (lastDigit - 1);
    } else if (this.parentStep) {
      return this.parentStep.stepDisplayNumber;
    }
  }

  get isMajor(): boolean {
    return this.priority == TestStepPriority.MAJOR;
  };

}
