import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {TestCaseResult} from "../../models/test-case-result.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestCaseDataDrivenResultService} from "../../services/test-case-data-driven-result.service";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {TestCaseDataDrivenResult} from "../../models/test-case-data-driven-result.model";

@Component({
  selector: 'app-re-run-data-driven-results',
  templateUrl: './re-run-data-driven-results.component.html',
  styles: []
})
export class ReRunDataDrivenResultsComponent implements OnInit {
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Output('onTestCaseResultShow') onTestCaseResultShow = new EventEmitter<TestCaseResult>();

  public testCaseDataDrivenResults: InfiniteScrollableDataSource;

  constructor(
    private testCaseResultService: TestCaseResultService,
    private testCaseDataDrivenResultService: TestCaseDataDrivenResultService) {
  }

  ngOnInit(): void {
    let query = "childRunId:"+this.testPlanResult.lastRun.id+",testCaseResultId:" + this.testCaseResult.id;
    this.testCaseDataDrivenResults = new InfiniteScrollableDataSource(this.testCaseDataDrivenResultService, query);
    this.testCaseDataDrivenResults.latestFetchData.subscribe(drivenResults => {
      let drivenResultIds = [];
      drivenResults.forEach((result: TestCaseDataDrivenResult) => drivenResultIds.push(result.iterationResult.reRunParentId));
      this.testCaseResultService.findAll(`id@${drivenResultIds.join('#')}`).subscribe(previousResults => {
        previousResults.content.forEach((previousResult: TestCaseResult) => {
          drivenResults.forEach((drivenResult: TestCaseDataDrivenResult) => {
            if (drivenResult.iterationResult.reRunParentId == previousResult.id)
              drivenResult.iterationResult.parentResult = previousResult;
          })
        })
      })
    })
  }

  showTestCaseResult(iterationResult: TestCaseResult) {
    this.testCaseResultService.show(iterationResult.id).subscribe(res => this.onTestCaseResultShow.emit(res));
  }
}
