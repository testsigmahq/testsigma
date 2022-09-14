import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {TestCaseResultService} from "../../services/test-case-result.service";

@Component({
  selector: 'app-re-run-details',
  templateUrl: './re-run-details.component.html',
  styles: []
})
export class ReRunDetailsComponent implements OnInit {
  public activeTestCaseResult: TestCaseResult;
  public activeTestSuiteResult: TestSuiteResult;
  public activeEnvironmentResult: TestDeviceResult;
  public activeExecutionResult: TestPlanResult;
  public view: String = 'TCR';
  public isShowSteps: boolean = false;

  constructor(
    private testSuiteResultService: TestSuiteResultService,
    private testCaseResultService: TestCaseResultService,
    @Inject(MAT_DIALOG_DATA) public options: {
      testCaseResult?: TestCaseResult,
      testSuiteResult?: TestSuiteResult,
      environmentResult?: TestDeviceResult,
      testPlanResult: TestPlanResult,
      showSteps: boolean
    }) {
    this.isShowSteps = this.options.showSteps;
  }

  get runId() {
    return this.options.testPlanResult?.lastRun?.id;
  }

  get parentRunId() {
    return this.options.testPlanResult?.id;
  }

  ngOnInit(): void {
    console.log(this.options);
    if(!this.options.testCaseResult && !this.options.testSuiteResult && !this.options.environmentResult)
      this.activeExecutionResult = this.options.testPlanResult;
    this.activeTestCaseResult = this.options.testCaseResult;
    this.activeEnvironmentResult = this.options.environmentResult;
    this.activeTestSuiteResult = this.options.testSuiteResult;
    this.refreshPreviousResult();
  }

  toggleView(view: string) {
    this.view = view;
  }

  refreshPreviousResult(){
    if(this.activeTestCaseResult!=null && this.activeTestCaseResult.reRunParentId != null){
      this.testCaseResultService.show(this.activeTestCaseResult?.reRunParentId).subscribe(res =>{
        this.activeTestCaseResult.parentResult = res;
      })
    }
  }

  showTestCaseResult(testCaseResult: TestCaseResult) {
    this.activeTestSuiteResult = null;
    this.activeEnvironmentResult = null;
    this.activeTestCaseResult = testCaseResult;
    this.refreshPreviousResult();
    this.activeExecutionResult = null;
  }

  showTestMachineResult(environmentResult: TestDeviceResult) {
    this.activeTestSuiteResult = null;
    this.activeEnvironmentResult = environmentResult;
    this.activeTestCaseResult = null;
    this.activeExecutionResult = null;
    this.view = null;
    setTimeout(()=> this.view = 'TCR', 10);
  }

  showTestSuiteResult(testSuiteResult: TestSuiteResult) {
    this.activeTestSuiteResult = testSuiteResult;
    this.activeEnvironmentResult = null;
    this.activeTestCaseResult = null;
    this.activeExecutionResult = null;
    this.view = null;
    setTimeout(()=> this.view = 'TCR', 10);
  }

  showExecutionResult(){
    this.activeExecutionResult = this.options.testPlanResult;
    this.activeTestSuiteResult = null;
    this.activeEnvironmentResult = null;
    this.activeTestCaseResult = null;
    this.view = null;
    setTimeout(()=> this.view = 'TCR', 10);
  }

  showTestSuiteResultId(suiteResultId: number){
    this.testSuiteResultService.show(suiteResultId).subscribe(res => this.showTestSuiteResult(res));
  }
}
