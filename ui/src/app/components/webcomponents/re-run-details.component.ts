import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {TestPlanResultService} from "../../services/test-plan-result.service";

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
    private testDeviceResultService: TestDeviceResultService,
    private testPlanResultService: TestPlanResultService,
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
    if(environmentResult?.lastRun?.reRunParentId) {
      this.testDeviceResultService.show(environmentResult?.lastRun?.reRunParentId).subscribe( res => {
        this.activeTestSuiteResult = null;
        environmentResult.childResult = res;
        this.activeEnvironmentResult = environmentResult;
        this.activeTestCaseResult = null;
        this.activeExecutionResult = null;
        this.view = null;
        setTimeout(() => this.view = 'TCR', 10);
      }
      )
    }
  }

  showTestSuiteResult(testSuiteResult: TestSuiteResult) {
    if(testSuiteResult?.lastRun?.reRunParentId){
      this.testSuiteResultService.show(testSuiteResult?.lastRun?.reRunParentId).subscribe( res =>{
          testSuiteResult.childResult = res;
          this.activeTestSuiteResult = testSuiteResult;
          this.activeEnvironmentResult = null;
          this.activeTestCaseResult = null;
          this.activeExecutionResult = null;
          this.view = null;
          setTimeout(()=> this.view = 'TCR', 10);
        }
      )
    }
  }

  showExecutionResult(){
    if(this.options.testPlanResult.lastRun?.reRunParentId) {
      this.testPlanResultService.show(this.options.testPlanResult.lastRun?.reRunParentId).subscribe( res =>{
        this.activeExecutionResult = this.options.testPlanResult;
        this.activeExecutionResult.childResult = res;
        this.activeTestSuiteResult = null;
        this.activeEnvironmentResult = null;
        this.activeTestCaseResult = null;
        this.view = null;
        setTimeout(() => this.view = 'TCR', 10);
      })
    }
  }

  showTestSuiteResultId(suiteResultId: number){
    this.testSuiteResultService.show(suiteResultId).subscribe(res => this.showTestSuiteResult(res));
    if(this.activeTestSuiteResult?.lastRun?.reRunParentId){
      this.testSuiteResultService.show(this.activeTestSuiteResult?.lastRun?.reRunParentId).subscribe( res =>{
          this.activeTestSuiteResult.childResult = res ;
        }
      )
    }
  }
}
