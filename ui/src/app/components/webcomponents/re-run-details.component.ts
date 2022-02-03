import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestSuiteResultService} from "../../services/test-suite-result.service";

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

  constructor(
    private testSuiteResultService: TestSuiteResultService,
    @Inject(MAT_DIALOG_DATA) public options: {
      testCaseResult?: TestCaseResult,
      testSuiteResult?: TestSuiteResult,
      environmentResult?: TestDeviceResult,
      executionResult: TestPlanResult
    }) {
  }

  get runId() {
    return this.options.executionResult?.childResult?.id;
  }

  get parentRunId() {
    return this.options.executionResult?.id;
  }

  ngOnInit(): void {
    console.log(this.options);
    if(!this.options.testCaseResult && !this.options.testSuiteResult && !this.options.environmentResult)
      this.activeExecutionResult = this.options.executionResult;
    this.activeTestCaseResult = this.options.testCaseResult;
    this.activeEnvironmentResult = this.options.environmentResult;
    this.activeTestSuiteResult = this.options.testSuiteResult;
  }

  toggleView(view: string) {
    this.view = view;
  }

  showTestCaseResult(testCaseResult: TestCaseResult) {
    this.activeTestSuiteResult = null;
    this.activeEnvironmentResult = null;
    this.activeTestCaseResult = testCaseResult;
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
    this.activeExecutionResult = this.options.executionResult;
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
