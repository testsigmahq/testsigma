import {alias, custom, deserialize, object, optional, serializable, SKIP} from 'serializr';
import {TestPlan} from "app/models/test-plan.model";
import {StatusConstant} from "../enums/status-constant.enum";
import {PageObject} from "../shared/models/page-object";
import {Environment} from "./environment.model";
import {ResultBase} from "./result-base.model";
import {ReRunType} from "../enums/re-run-type.enum";
import {DryTestPlan} from "./dry-test-plan.model";
import {TestSuiteResult} from "./test-suite-result.model";
import {Page} from "../shared/models/page";
import {TestDeviceResult} from "./test-device-result.model";

export class TestPlanResult extends ResultBase implements PageObject {
  @serializable
  public id: number;
  @serializable(custom((v) => SKIP, v => {
    if(v)
      return new TestPlan().deserialize(v);
  }))
  public testPlan: TestPlan;

  @serializable(custom((v) => SKIP, v => {
    if(v)
      return new TestPlan().deserialize(v);
  }))
  public dryTestPlan: DryTestPlan;
  @serializable
  public testPlanId: number;
  @serializable
  public status: StatusConstant;
  @serializable
  public message: String;
  @serializable
  public buildNo: String;
  @serializable
  public environmentId: Number;
  @serializable(optional(object(Environment)))
  public environment: Environment;
  @serializable(optional(object(TestPlanResult)))
  public childResult: TestPlanResult;
  @serializable
  public reRunParentId: number;
  @serializable
  public reRunType: ReRunType;
  @serializable
  public isReRun: boolean;
  @serializable
  public parenttestPlanResultId: number;
  @serializable
  public isVisuallyPassed: boolean;
  @serializable
  public totalRunningCount:number;
  @serializable
  public totalQueuedCount:number;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestPlanResult, input));
  }

  get canReRun(){
    return !this.childResult && (this.isFailed || this.isAborted
        || (this.isNotExecuted && (this.notExecutedCount != this.totalCount)) || (this.isStopped && (this.stoppedCount != this.totalCount)));
  }

  consolidateCount(result?: TestPlanResult | TestDeviceResult | TestSuiteResult) {
    if(!Boolean(result)){
      result = this;
    }
    if (result.childResult){
      result.message = result.childResult.message;
      result.result = result.childResult.result;
      result.failedCount = result.childResult.failedCount;
      result.abortedCount = result.childResult.abortedCount;
      result.stoppedCount = result.childResult.stoppedCount;
      result.notExecutedCount = result.childResult.notExecutedCount;
      result.queuedCount = result.childResult.queuedCount;
      if( result instanceof TestPlanResult && result.childResult.reRunType == ReRunType.ONLY_FAILED_TESTS ||
        result instanceof TestDeviceResult && result.testPlanResult.childResult.reRunType == ReRunType.ONLY_FAILED_TESTS ||
        result instanceof TestSuiteResult && result.testDeviceResult.testPlanResult.childResult.reRunType == ReRunType.ONLY_FAILED_TESTS){
        result.passedCount += result.childResult.passedCount;
      } else {
        result.passedCount = result.childResult.passedCount;
      }
    }
  }

  consolidateListCount(res: Page<TestDeviceResult>|Page<TestSuiteResult>) {
    res.content.forEach( result =>{
      this.consolidateCount(result);
    });
  }
}
