import {Component, Input, OnInit} from '@angular/core';
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {ResultConstant} from "../../enums/result-constant.enum";

@Component({
  selector: 'app-test-machine-test-suite-results',
  templateUrl: './test-machine-test-suite-results.component.html',
  styles: []
})
export class TestMachineTestSuiteResultsComponent implements OnInit {
  @Input('environmentResult') environmentResult: TestDeviceResult;

  public suiteResults: InfiniteScrollableDataSource;
  public isFilterApplied: boolean = false;
  public isMachineSuiteFetchComplete: boolean = false;

  constructor(private testSuiteResultService: TestSuiteResultService) {
  }

  ngOnInit() {
    this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, "environmentResultId:" + this.environmentResult.id);
    this.isMachineSuiteFetchComplete = true;
  }

  filterAction(event) {
    let query = "environmentResultId:" + this.environmentResult.id;
    let applyFilter: Boolean = event.applyFilter;
    this.isFilterApplied = false;
    if (applyFilter && !event.nameBased) {
      let filterResult: ResultConstant[] = event.filterResult;
      this.isFilterApplied = true;
      if (filterResult && filterResult.length) {
        query += ",result@" + filterResult.join("#");
      }
    } else if (event.nameBased && applyFilter) {
      this.isFilterApplied = true;
      query += "," + event.nameBased;
    }
    this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, query);
  }

}
