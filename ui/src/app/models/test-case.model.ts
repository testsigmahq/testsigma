import {alias, custom, deserialize, object, optional, serializable, SKIP} from "serializr";
import {PageObject} from "../shared/models/page-object";
import {Base} from "../shared/models/base.model";
import {TestCaseStatus} from "../enums/test-case-status.enum";
import {Requirement} from "./requirement.model";
import {TestData} from "./test-data.model";
import {TestCaseResult} from "./test-case-result.model";
import {TestCasePriority} from "./test-case-priority.model";
import {TestCaseType} from "./test-case-type.model";

import * as moment from 'moment';

export class TestCase extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable
  public comments: String;
  @serializable
  public description: String;
  @serializable
  public status: TestCaseStatus;
  @serializable
  sendMailNotification: Boolean = false ;
  @serializable
  public isStepGroup: Boolean;
  @serializable
  public testDataId: number;
  @serializable
  public requirementId: number;
  @serializable(optional(object(Requirement)))
  public requirement: Requirement;
  @serializable(alias('priority'))
  public priorityId: Number;
  @serializable
  public type: Number;
  @serializable
  public preRequisite: number;
  @serializable
  public isDataDriven: Boolean;
  @serializable(optional(object(TestData)))
  public testData: TestData;
  @serializable
  public workspaceVersionId: number;
  @serializable(custom((v) => SKIP, v => {
    if (v)
      return new TestCaseResult().deserialize(v);
  }))
  public lastRun: TestCaseResult;
  @serializable(custom(() => SKIP, (v) => {
    if (v)
      return moment(v)
  }))
  public draftAt: Date;
  @serializable(custom(() => SKIP, (v) => {
    if (v)
      return moment(v)
  }))
  public obsoleteAt: Date;
  @serializable(custom(() => SKIP, (v) => {
    if (v)
      return moment(v)
  }))
  public readyAt: Date;
  @serializable
  public deleted: boolean;
  @serializable(custom(v=> (v==-1? null : v), v=>v))
  public testDataStartIndex: Number;
  @serializable(custom(v=> (v==-1? null : v), v=>v))
  public testDataEndIndex: Number;
  @serializable(optional(object(TestCase)))
  public preRequisiteCase: TestCase;
  @serializable(custom(v => v, v => v))
  public tags: String[];

  public testCasePriority: TestCasePriority;
  public testCaseType: TestCaseType;
  public parentCase: TestCase;
  public startUrl: String;
  public isChecked: boolean;

  get isInReview() {
    return this.status == TestCaseStatus.IN_REVIEW;
  }

  get isInReady() {
    return this.status == TestCaseStatus.READY;
  }

  get isDraft() {
    return this.status == TestCaseStatus.DRAFT || this.status == TestCaseStatus.IN_REVIEW;
  }

   get isOnlyDraft() {
    return this.status == TestCaseStatus.DRAFT;
  }

  protected  equal(testCase:TestCase){
    return this.id==testCase.id;
  }
  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestCase, input));
  }
}
