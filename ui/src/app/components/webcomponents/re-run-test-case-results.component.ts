import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestSuiteResultService} from "../../services/test-suite-result.service";

@Component({
  selector: 'app-re-run-test-case-results',
  templateUrl: './re-run-test-case-results.component.html',
  styles: [
  ]
})
export class ReRunTestCaseResultsComponent implements OnInit {
  @Input('executionResult') executionResult: TestPlanResult;
  @Input('environmentResult') environmentResult: TestDeviceResult;
  @Input('testSuiteResult') testSuiteResult: TestSuiteResult;
  @Output('onTestCaseResultShow') onTestCaseResultShow = new EventEmitter<TestCaseResult>();
  @Output('onTestSuiteResultShow') onTestSuiteResultShow = new EventEmitter<TestSuiteResult>();
  @Output('onTestMachineResultShow') onTestMachineResultShow = new EventEmitter<TestDeviceResult>();

  public testCaseResultsDataSource: InfiniteScrollableDataSource;

  constructor(
    private testSuiteResultService: TestSuiteResultService,
    private testCaseResultService: TestCaseResultService) { }

  ngOnInit(): void {
    let query = "childRunId:"+this.executionResult.childResult.id+",iteration:null,testPlanResultId:" + this.executionResult.id;
    if(this.environmentResult)
      query+=",environmentResultId:"+this.environmentResult.id;
    if(this.testSuiteResult)
      query+=",suiteResultId:"+this.testSuiteResult.id;
    this.testCaseResultsDataSource = new InfiniteScrollableDataSource(this.testCaseResultService, query);
  }

  showTestCaseResult(testCaseResult: TestCaseResult) {
    this.onTestCaseResultShow.emit(testCaseResult);
  }

  showTestMachineResult(environmentResult: TestDeviceResult) {
    this.onTestMachineResultShow.emit(environmentResult);
  }

  showTestSuiteResult(testSuiteResultId: number) {
    this.testSuiteResultService.show(testSuiteResultId).subscribe(res => this.onTestSuiteResultShow.emit(res));
  }
}
