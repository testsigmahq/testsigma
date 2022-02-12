import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ResultConstant} from "../../enums/result-constant.enum";
import {TestSuiteResult} from "../../models/test-suite-result.model";


@Component({
  selector: 'app-test-suite-details-quick-info',
  templateUrl: './test-suite-details-quick-info.component.html',
  styles: []
})
export class TestSuiteDetailsQuickInfoComponent implements OnInit {
  @Input('suiteResultResult') suiteResultResult: TestSuiteResult;
  @Output('toggleDetailsAction') toggleDetailsAction = new EventEmitter<Boolean>();
  @Output('filterAction') filterAction = new EventEmitter<any>();
  @Output('buildNo') buildNo = new EventEmitter<number>()
  public showRunDetails: Boolean;
  public resultConstant: typeof ResultConstant = ResultConstant;
  public isEditBuildNo: boolean = false;
  public updateBuildNumber: number;

  constructor() {
  }

  ngOnInit() {
    // this.userService.show(this.suiteResultResult.testDeviceResult.testPlanResult.executedById).subscribe(user => this.suiteResultResult.testDeviceResult.testPlanResult.executedBy = user);
  }

  toggleDetails(communicateToParent?: Boolean) {
    this.showRunDetails = !this.showRunDetails;
    if (communicateToParent)
      this.toggleDetailsAction.emit(communicateToParent);
  }

  filter(query) {
    this.filterAction.emit({
      applyFilter: true,
      filterResult: [query]
    });
  }

}
