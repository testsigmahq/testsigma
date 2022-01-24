import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ResultConstant} from "../../enums/result-constant.enum";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {Page} from "../../shared/models/page";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {Pageable} from "../../shared/models/pageable";


@Component({
  selector: 'app-test-machine-details-quick-info',
  templateUrl: './test-machine-details-quick-info.component.html',
  styles: []
})
export class TestMachineDetailsQuickInfoComponent implements OnInit {
  @Input('showList') showList: String;
  @Input('environmentResult') environmentResult: TestDeviceResult;
  @Output('toggleDetailsAction') toggleDetailsAction = new EventEmitter<Boolean>();
  @Output('filterAction') filterAction = new EventEmitter<any>();
  @Output('buildNo') buildNo = new EventEmitter<number>()
  public showRunDetails: Boolean;
  public resultConstant: typeof ResultConstant = ResultConstant;

  public results: Page<TestSuiteResult>;

  constructor(
    private testSuiteResultService: TestSuiteResultService) {
  }

  ngOnInit() {
    // this.userService.show(this.testDeviceResult.executionResult.executedById).subscribe(user => this.testDeviceResult.executionResult.executedBy = user);
  }

  ngOnChanges() {
    let pageable = new Pageable();
    pageable.pageSize = 50;
    this.testSuiteResultService.findAll("environmentResultId:" + this.environmentResult.id, undefined, pageable).subscribe(res => this.results = res);
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


  get passedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isPassed).length) / this.results.totalElements) * 100);
  }

  get failedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isFailed).length) / this.results.totalElements) * 100);
  }

  get notExecutedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isNotExecuted).length) / this.results.totalElements) * 100);
  }

  get abortedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isAborted).length) / this.results.totalElements) * 100);
  }

  get queuedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isQueued).length) / this.results.totalElements) * 100);
  }

  get stoppedPercentage(): Number {
    return Math.round(((this.results.content.filter(res => res.isStopped).length) / this.results.totalElements) * 100);
  }

  get passedCount(): Number {
    return this.results.content.filter(res => res.isPassed).length;
  }

  get failedCount(): Number {
    return this.results.content.filter(res => res.isFailed).length;
  }

  get notExecutedCount(): Number {
    return this.results.content.filter(res => res.isNotExecuted).length;
  }

  get abortedCount(): Number {
    return this.results.content.filter(res => res.isAborted).length;
  }

  get queuedCount(): Number {
    return this.results.content.filter(res => res.isQueued).length;
  }

  get stoppedCount(): Number {
    return this.results.content.filter(res => res.isStopped).length;
  }

  get totalCount(): Number {
    return this.results.content.length;
  }

  get canShowVideoScreenShort(): Boolean {
    return this.environmentResult && !(this.environmentResult.testPlanResult && (this.environmentResult.testPlanResult.testPlan.workspaceVersion.workspace.isRest ||
      this.environmentResult.isExecuting ||
      this.environmentResult.isFailed ||
      this.environmentResult.testPlanResult.testPlan.isHybrid));
  }
}
