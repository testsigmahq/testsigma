import {Component, Input, OnInit} from '@angular/core';
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {ResultConstant} from "../../enums/result-constant.enum";

@Component({
  selector: 'app-test-machine-test-case-results',
  templateUrl: './test-machine-test-case-results.component.html',
  styles: []
})
export class TestMachineTestCaseResultsComponent implements OnInit {
  @Input('environmentResult') environmentResult: TestDeviceResult;


  public testCaseResults: InfiniteScrollableDataSource;
  public isFilterApplied: boolean;
  public isMachineCaseFetchComplete: boolean = false;

  constructor(private testCaseResultService: TestCaseResultService) {
  }

  ngOnInit() {
  }

  ngOnChanges() {
    this.testCaseResults = new InfiniteScrollableDataSource(this.testCaseResultService, ",iteration:null,environmentResultId:" + this.environmentResult.id);
    this.isMachineCaseFetchComplete = true;
  }

  filterAction(event) {
    let query = ",iteration:null,environmentResultId:" + this.environmentResult.id;
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
  }

}
