import {Component, Input, OnInit} from '@angular/core';
import {TestCaseResultService} from "../../services/test-case-result.service";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {ResultConstant} from "../../enums/result-constant.enum";

@Component({
  selector: 'app-test-suite-test-case-results',
  templateUrl: './test-suite-test-case-results.component.html',
  styles: []
})
export class TestSuiteTestCaseResultsComponent implements OnInit {
  @Input('testSuiteResult') testSuiteResult: TestSuiteResult;

  public testCaseResults: InfiniteScrollableDataSource;
  public isFilterApplied: boolean = false;
  public isSuiteCaseFetchComplete: boolean = false;

  constructor(private testCaseResultService: TestCaseResultService) {
  }

  ngOnInit() {
  }

  ngOnChanges() {
    this.filterAction("");
  }

  filterAction(event) {
    let query = ",iteration:null,suiteResultId:" + this.testSuiteResult.id;
    this.isFilterApplied = false;
    let applyFilter: Boolean = event.applyFilter;
    if (!event.nameBased && applyFilter) {
      let filterResult: ResultConstant[] = event.filterResult;
      let filterTestCaseTypes: string[] = event.filterTestCaseTypes;
      let filterTestCasePriorities: string[] = event.filterTestCasePriorities;
      this.isFilterApplied = true;
      if (filterResult && filterResult.length) {
        query += ",result@" + filterResult.join("#");
      }
      if (filterTestCaseTypes && filterTestCaseTypes.length) {
        query += ",testCaseTypeId@" + filterTestCaseTypes.join("#");
      }
      if (filterTestCasePriorities && filterTestCasePriorities.length) {
        query += ",priorityId@" + filterTestCasePriorities.join("#");
      }
    } else if (event.nameBased && applyFilter) {
      query += "," + event.nameBased;
      this.isFilterApplied = true;
    }
    this.testCaseResults = new InfiniteScrollableDataSource(this.testCaseResultService, query);
    this.isSuiteCaseFetchComplete = true;
  }


}
