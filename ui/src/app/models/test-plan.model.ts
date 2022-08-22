import {custom, deserialize, object, serializable, SKIP, list, optional, alias} from 'serializr';
import {WorkspaceVersion} from "./workspace-version.model";
import {TestPlanLabType} from "../enums/test-plan-lab-type.enum";
import {TestPlanType} from "../enums/execution-type.enum";
import {Screenshot} from "../enums/screenshot.enum";
import {RecoverAction} from "../enums/recover-action.enum";
import {OnAbortedAction} from "../enums/on-aborted-action.enum";
import {PreRequisiteAction} from "../enums/pre-requisite-action.enum";
import {TestPlanResult} from "./test-plan-result.model";
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {TestDevice} from "./test-device.model";
import {Environment} from "./environment.model";
import {ResultConstant} from "../enums/result-constant.enum";
import { ReRunType } from 'app/enums/re-run-type.enum';

export class TestPlan extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public workspaceVersionId: number;
  @serializable
  public name: String;
  @serializable
  public description: String;
  @serializable
  public elementTimeOut: number;
  @serializable
  public pageTimeOut: number;
  @serializable
  public environmentId: number;
  @serializable
  public screenshot: Screenshot;
  @serializable
  public recoveryAction: RecoverAction;
  @serializable
  public onAbortedAction: OnAbortedAction;
  @serializable
  public onSuitePreRequisiteFail: PreRequisiteAction;
  @serializable
  public onTestCasePreRequisiteFail: PreRequisiteAction;
  @serializable
  public onStepPreRequisiteFail: RecoverAction;
  @serializable
  public reRunType: ReRunType;
  @serializable
  public testPlanLabType: TestPlanLabType;
  @serializable
  public testPlanType: TestPlanType;
  @serializable
  public lastRunId: number;
  @serializable(object(WorkspaceVersion))
  public workspaceVersion: WorkspaceVersion;

  @serializable(custom((v) => SKIP, v => {
    if (v)
      return new TestPlanResult().deserialize(v);
  }))
  public lastRun: TestPlanResult;
  @serializable(optional())
  public retrySessionCreation: Boolean;
  @serializable(optional())
  public retrySessionCreationTimeout: number;
  @serializable(optional(list(object(TestDevice))))
  public testDevices: TestDevice[];

  @serializable
  public matchBrowserVersion: boolean;
  @serializable(custom(v => v, v => v))
  public tags: String[];
  public selected: Boolean;
  public environment: Environment;

  get isCustomPlan(): boolean {
    return this.testPlanType == TestPlanType.DISTRIBUTED;
  }

  get isTestsigmaLab() {
    return this.testPlanLabType === TestPlanLabType.TestsigmaLab;
  }

  get isHybrid() {
    return this.testPlanLabType === TestPlanLabType.Hybrid;
  }

  get isPrivateLab() {
    return this.testPlanLabType === TestPlanLabType.PrivateGrid;
  }


  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestPlan, input));
  }

}
