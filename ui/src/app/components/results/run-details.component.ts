import {Component, OnInit, ViewChild} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {ActivatedRoute, Params, Router} from '@angular/router';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
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


  public executionResult: TestPlanResult;
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
    private executionResultService: TestPlanResultService,
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
    if (listenerChangeTrue && this.executionResult?.isExecuting && !this.isDisabledAutoRefresh){
      this.fetchExecutionResult();
    }
    this.removeAutoRefresh();
    if ((this.executionResult?.isExecuting || this.executionResult?.childResult?.isExecuting) && !this.isDisabledAutoRefresh)
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
    this.executionResultService.show(this.runId).subscribe(result => {
      this.executionResult = result;
      Object.assign(this.originalExecutionResult, result);
      if(this.executionResult?.reRunParentId)
        this.router.navigate(['/td', 'runs', this.executionResult?.reRunParentId]);
      if (!this.executionResult.isExecuting && !this.executionResult?.childResult?.isExecuting) {
        this.removeAutoRefresh();
      } else if ((this.executionResult?.isExecuting || this.executionResult?.childResult?.isExecuting) && this.executionResult?.testPlan) {
        this.isExecutionRunning = true;
        this.addAutoRefresh();
      }
      // this.userService.show(this.executionResult.executedById).subscribe(user => this.executionResult.executedBy = user);
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
    this.executionResult.failedCount = originalExecutionResult.failedCount;
    this.executionResult.abortedCount = originalExecutionResult.abortedCount;
    this.executionResult.stoppedCount = originalExecutionResult.stoppedCount;
    this.executionResult.notExecutedCount = originalExecutionResult.notExecutedCount;
    this.executionResult.queuedCount = originalExecutionResult.queuedCount;
    this.executionResult.passedCount = originalExecutionResult.passedCount;
  }
}
