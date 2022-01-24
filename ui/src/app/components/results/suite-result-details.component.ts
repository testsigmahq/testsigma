import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute, Params, Router} from "@angular/router";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {Location} from "@angular/common";
import {EnvironmentService} from "../../services/environment.service";
import {fromEvent, interval, Subscription} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {TestSuiteTestCaseResultsComponent} from "../webcomponents/test-suite-test-case-results.component";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {MatDialog} from "@angular/material/dialog";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-suite-result-details',
  templateUrl: './suite-result-details.component.html',
  styles: []
})
export class SuiteResultDetailsComponent extends BaseComponent implements OnInit {
  public currentDate: Date;
  public showQuickInfo: boolean = false;
  public suiteResultId: number;
  public suiteResult: TestSuiteResult;
  public showFilter: boolean = false;
  public activeTab: string;
  public isSuiteFetchingCompleted: boolean = false;
  public videoURL: URL;
  public isCaseLevelExecution: boolean = false;

  @ViewChild(TestSuiteTestCaseResultsComponent)
  private testCaseResults: TestSuiteTestCaseResultsComponent;
  public autoRefreshSubscription: Subscription;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public isSearchEnable: boolean = false;
  public isSuiteRunning: boolean = false;
  @ViewChild('searchSuiteInput') searchSuiteInput: ElementRef
  public isFilterApplied: Boolean = false;
  inputValue: any;

  constructor(
              private route: ActivatedRoute,
              private environmentService: EnvironmentService,
              private testSuiteResultService: TestSuiteResultService,
              private location: Location,
              public environmentResultService: TestDeviceResultService,
              private matModal: MatDialog,
              private router: Router) {
    super();
  }

  ngOnInit() {
    this.activeTab = 'steps';

    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      this.suiteResultId = params.resultId;
      this.fetchSuiteResult();
      this.attachAutoRefreshEvents();
    });
    this.currentDate = new Date();
  }

  ngOnDestroy(): void {
    this.removeAutoRefresh();
  }

  clearSearch() {
    this.inputClear();
  }

  inputClear() {
    this.testCaseResults && this.testCaseResults.filterAction({applyFilter: false});
    this.inputValue = null;
  }

  private attachAutoRefreshEvents() {
    document.addEventListener("visibilitychange", () => {
      document.hidden ? this.removeAutoRefresh() : this.addAutoRefresh(true);
    });
  }

  private addAutoRefresh(listenerChangeTrue?: boolean) {
    if (listenerChangeTrue && this.suiteResult?.isExecuting && !this.isDisabledAutoRefresh) {
      this.fetchSuiteResult();
    }
    this.removeAutoRefresh();
    if (this.suiteResult?.isExecuting && !this.isDisabledAutoRefresh)
      this.autoRefreshSubscription = interval(this.autoRefreshInterval).subscribe(() => {
        this.fetchSuiteResult();
      });
  }

  private removeAutoRefresh() {
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

  fetchSuiteResult() {
    this.isSuiteRunning = false;
    this.testSuiteResultService.show(this.suiteResultId).subscribe(res => {
      this.suiteResult = res;
      if (this.suiteResult.reRunParentId)
        this.router.navigate(['/td', 'suite_results', this.suiteResult.reRunParentId]);
      if (this.suiteResult.isQueued) {
        this.isSuiteRunning = true;
        this.addAutoRefresh();
      } else if (!this.suiteResult.isQueued) {
        this.removeAutoRefresh();
      }
      if (this.suiteResult.testDeviceResult.testPlanResult.environmentId)
        this.environmentService.show(this.suiteResult.testDeviceResult.testPlanResult.environmentId).subscribe(res => this.suiteResult.testDeviceResult.testPlanResult.environment = res);
      new TestPlanResult().consolidateCount(this.suiteResult)
      this.isSuiteFetchingCompleted = true;
    });
  }


  goBack() {
    this.location.back();
  }

  toggleFilter() {
    this.showQuickInfo = false;
    this.showFilter = !this.showFilter;
  }

  toggleDetails() {
    this.showQuickInfo = !this.showQuickInfo;
    this.showFilter = false;
  }

  resetFilter() {
    this.filterAction({applyFilter: false});
    this.toggleFilter();
  }

  filterAction(event?) {
    this.isFilterApplied = !!(event && event.applyFilter);
    this.testCaseResults && this.testCaseResults.filterAction(event);
  }

  toggleSearch() {
    this.isSearchEnable = !this.isSearchEnable;
    if (this.isSearchEnable) {
      setTimeout(() => {
        this.searchSuiteInput.nativeElement.focus();
        this.attachDebounceEvent();
      }, 10);
    } else {
      this.testCaseResults && this.testCaseResults.filterAction({applyFilter: false});
    }
  }

  attachDebounceEvent() {
    fromEvent(this.searchSuiteInput.nativeElement, 'keyup')
      .pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((event: KeyboardEvent) => {
          if (this.searchSuiteInput.nativeElement.value)
            this.testCaseResults && this.testCaseResults.filterAction({
              applyFilter: true,
              nameBased: "testCaseName:*" + this.searchSuiteInput.nativeElement.value + "*"
            });
          else
            this.testCaseResults && this.testCaseResults.filterAction({applyFilter: false});
        })
      )
      .subscribe();
  }
}
