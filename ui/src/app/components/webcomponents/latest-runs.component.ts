import {Component, Input, OnInit} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {FilterTimePeriod} from "../../enums/filter-time-period.enum";
import * as moment from 'moment';
import {Page} from "../../shared/models/page";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";

@Component({
  selector: 'app-latest-runs',
  templateUrl: './latest-runs.component.html',
  styles: []
})
export class LatestRunsComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  public period: FilterTimePeriod = FilterTimePeriod.TODAY;
  public testPlanResults: InfiniteScrollableDataSource;
  public dayResults: InfiniteScrollableDataSource;
  public weekResults: InfiniteScrollableDataSource;
  public monthResults: InfiniteScrollableDataSource;
  public quarterResults: InfiniteScrollableDataSource;
  public filterTimePeriod = FilterTimePeriod;
  public environmentResults: Page<TestDeviceResult>;
  public activeExecutionResult: TestPlanResult;

  constructor(
    private environmentResultService: TestDeviceResultService,
    private testPlanResultService: TestPlanResultService,) {
  }

  ngOnInit(): void {
    this.testPlanResults = this.dayResults = this.fetchTestPlanResults(this.period);
    this.weekResults = this.fetchTestPlanResults(FilterTimePeriod.LAST_SEVEN_DAYS);
    this.monthResults = this.fetchTestPlanResults(FilterTimePeriod.LAST_30_DAYS);
    this.quarterResults = this.fetchTestPlanResults(FilterTimePeriod.LAST_90_DAYS);
    this.setFirstActiveResult();
  }

  fetchTestPlanResults(period: FilterTimePeriod) {
    let query = "entityType:TEST_PLAN,workspaceVersionId:" + this.version.id;
    query += ",startTime>" + this.getFormattedDate(period);
    return new InfiniteScrollableDataSource(this.testPlanResultService, query, "id,desc");
  }

  filter(period: FilterTimePeriod) {
    this.period = period;
    switch (this.period) {
      case FilterTimePeriod.TODAY:
        this.testPlanResults = this.dayResults;
        break;
      case FilterTimePeriod.LAST_SEVEN_DAYS:
        this.testPlanResults = this.weekResults;
        break;
      case FilterTimePeriod.LAST_30_DAYS:
        this.testPlanResults = this.monthResults;
        break;
      case FilterTimePeriod.LAST_90_DAYS:
        this.testPlanResults = this.quarterResults;
        break;
    }
    this.setFirstActiveResult();
  }

  private getFormattedDate(key: FilterTimePeriod): String {
    switch (key) {
      case FilterTimePeriod.TODAY:
        return moment().format("YYYY-MM-DD");
      case FilterTimePeriod.LAST_SEVEN_DAYS:
        return moment().subtract(7, 'd').format("YYYY-MM-DD");
      case FilterTimePeriod.LAST_30_DAYS:
        return moment().subtract(30, 'd').format("YYYY-MM-DD");
      case FilterTimePeriod.LAST_90_DAYS:
        return moment().subtract(90, 'd').format("YYYY-MM-DD");
      case FilterTimePeriod.LAST_180_DAYS:
        return moment().subtract(180, 'd').format("YYYY-MM-DD");
    }
  }

  private setFirstActiveResult() : void{
    if(this.testPlanResults.isFetching)
      setTimeout(()=> this.setFirstActiveResult(), 500);
    else
      if(this.testPlanResults.isEmpty) this.environmentResults = null;
      this.activeExecutionResult = <TestPlanResult>this.testPlanResults['cachedItems'][0];
      if(this.activeExecutionResult)
        this.fetchEnvironmentResults();
  }

  private fetchEnvironmentResults(): void {
    this.environmentResults=null;
    this.environmentResultService.findAll("testPlanResultId:"+this.activeExecutionResult.id).subscribe(res => {
      this.environmentResults= res;
    });
  }

  public setActiveResult(result: TestPlanResult){
    this.activeExecutionResult = result;
    this.fetchEnvironmentResults();
  }
}
