import {Component, OnInit, ViewChild} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {ActivatedRoute, Params, Router} from '@angular/router';
import {BaseComponent} from "../../shared/components/base.component";

import {TestCaseResultsComponent} from "../webcomponents/test-case-results.component";
import {TestSuiteResultsComponent} from "../webcomponents/test-suite-results.component";
import {TestMachineResultsComponent} from "../webcomponents/test-machine-results.component";
import {interval, Subscription} from "rxjs";
import {TestPlanType} from "../../enums/execution-type.enum";

@Component({
  selector: 'app-run-details',
  templateUrl: './run-details.component.html',
  styles: []
})
export class RunDetailsComponent extends BaseComponent implements OnInit {


  public testPlanResult: TestPlanResult;
  public originalExecutionResult: TestPlanResult = new TestPlanResult();
  private runId: Number;
  public showList: String = 'TCR';
  public showFilter: Boolean;
  public showRunDetails: Boolean = true;
  public autoRefreshSubscription: Subscription;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public isExecutionRunning: boolean = false;
  public isExecutionFetchingCompleted: boolean = false;

  @ViewChild(TestCaseResultsComponent)
  private testCaseResults: TestCaseResultsComponent;
  @ViewChild(TestSuiteResultsComponent)
  private testSuiteResults: TestSuiteResultsComponent;
  @ViewChild(TestMachineResultsComponent)
  private testMachineResults: TestMachineResultsComponent;

  constructor(
    private route: ActivatedRoute,
    private testPlanResultService: TestPlanResultService,
    private router: Router) {
    super();
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      this.runId = params.runId;
      this.fetchExecutionResult();
      this.attachAutoRefreshEvents();
    });
  }

  getCrossBrowser(){
    return TestPlanType.CROSS_BROWSER
  }

  ngOnDestroy(): void {
    this.removeAutoRefresh();
  }

  attachAutoRefreshEvents() {
    document.addEventListener("visibilitychange", () => {
      document.hidden ? this.removeAutoRefresh() : this.addAutoRefresh(true);
    });
  }

  addAutoRefresh(listenerChangeTrue?:boolean) {
    if (listenerChangeTrue && this.testPlanResult?.lastRun?.isExecuting && !this.isDisabledAutoRefresh){
      this.fetchExecutionResult();
    }
    this.removeAutoRefresh();
    if ((this.testPlanResult?.lastRun?.isExecuting || this.testPlanResult?.isExecuting) && !this.isDisabledAutoRefresh)
      this.autoRefreshSubscription = interval(this.autoRefreshInterval).subscribe(() => {
        this.fetchExecutionResult();
      });
  }

  removeAutoRefresh() {
    if (this.autoRefreshSubscription)
      this.autoRefreshSubscription.unsubscribe();
  }

  toggleAutoRefresh(isDisabledAutoRefresh: boolean) {
    this.isDisabledAutoRefresh = isDisabledAutoRefresh;
    if (this.isDisabledAutoRefresh) {
      this.removeAutoRefresh();
    } else {
      this.addAutoRefresh();
    }
  }

  changeAutoRefreshTime(event: number) {
    this.autoRefreshInterval = event;
    this.addAutoRefresh()
  }

  fetchExecutionResult() {
    this.isExecutionRunning = false;
    this.isExecutionFetchingCompleted = false;
    this.testPlanResultService.show(this.runId).subscribe(result => {
      this.testPlanResult = result;
      Object.assign(this.originalExecutionResult, result);
      if(this.testPlanResult?.reRunParentId){
        let firstParentResult;
      this.testPlanResultService.findFirstParentExecutionResult(this.testPlanResult.id).subscribe(
        (res) => {
          firstParentResult = res;
          this.router.navigate(['/td', 'runs', firstParentResult.id]);
        });
    }
      if (!this.testPlanResult.isExecuting && !this.testPlanResult?.lastRun?.isExecuting) {
        this.removeAutoRefresh();
      } else if ((this.testPlanResult?.isExecuting || this.testPlanResult?.lastRun?.isExecuting) && this.testPlanResult?.testPlan) {
        this.isExecutionRunning = true;
        this.addAutoRefresh();
      }
      // this.userService.show(this.testPlanResult.executedById).subscribe(user => this.testPlanResult.executedBy = user);
      this.isExecutionFetchingCompleted = true;
    }, error => {
      this.isExecutionFetchingCompleted = true;
    });
  }

  toggleView(view: String) {
    this.resetExecutionResultCounts();
    this.showList = view;
    this.toggleDetails(true);
  }

  toggleFilter(showFilter: Boolean) {
    this.showFilter = showFilter
  }

  toggleDetails(showRunDetails: Boolean) {
    this.showRunDetails = showRunDetails
  }

  get showTestCaseResults() {
    return this.showList == 'TCR';
  }

  get showTestSuiteResults() {
    return this.showList == 'TSR';
  }

  get showTestMachineResults() {
    return this.showList == 'TMR';
  }

  private resetExecutionResultCounts() {
    let originalExecutionResult = new TestPlanResult();
    Object.assign(originalExecutionResult , this.originalExecutionResult)
    this.testPlanResult.failedCount = originalExecutionResult.failedCount;
    this.testPlanResult.abortedCount = originalExecutionResult.abortedCount;
    this.testPlanResult.stoppedCount = originalExecutionResult.stoppedCount;
    this.testPlanResult.notExecutedCount = originalExecutionResult.notExecutedCount;
    this.testPlanResult.queuedCount = originalExecutionResult.queuedCount;
    this.testPlanResult.passedCount = originalExecutionResult.passedCount;
  }
}
