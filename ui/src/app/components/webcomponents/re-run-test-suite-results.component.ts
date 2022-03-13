import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {TestDeviceResult} from "../../models/test-device-result.model";

@Component({
  selector: 'app-re-run-test-suite-results',
  templateUrl: './re-run-test-suite-results.component.html',
  styles: [
  ]
})
export class ReRunTestSuiteResultsComponent implements OnInit {
  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Input('environmentResult') environmentResult?: TestDeviceResult;
  @Output('onTestSuiteResultShow') onTestSuiteResultShow = new EventEmitter<TestSuiteResult>();
  @Output('onTestMachineResultShow') onTestMachineResultShow = new EventEmitter<TestDeviceResult>();

  public suiteResults: InfiniteScrollableDataSource;

  constructor(
    private testSuiteResultService: TestSuiteResultService) { }

  ngOnInit(): void {
    let query = "childRunId:"+this.testPlanResult.childResult.id;
    if(this.environmentResult){
      query +=",environmentResultId:"+this.environmentResult.id;
    }
    this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, query);
  }

  showTestSuiteResult(suiteResult: TestSuiteResult) {
    this.onTestSuiteResultShow.emit(suiteResult);
  }

  showTestMachineResult(environmentResult: TestDeviceResult) {
    this.onTestMachineResultShow.emit(environmentResult);
  }
}
