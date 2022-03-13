import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {ActivatedRoute, Params} from "@angular/router";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {Location} from "@angular/common";
import {EnvironmentService} from "../../services/environment.service";
import {fromEvent, interval, Subscription} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {TestMachineTestCaseResultsComponent} from "../webcomponents/test-machine-test-case-results.component";
import {TestMachineTestSuiteResultsComponent} from "../webcomponents/test-machine-test-suite-results.component";
import {MatDialog} from "@angular/material/dialog";
import {TestPlanResult} from "../../models/test-plan-result.model";

@Component({
  selector: 'app-environment-result-details',
  templateUrl: './test-machine-result-details.component.html',
  styles: []
})
export class TestMachineResultDetailsComponent extends BaseComponent implements OnInit {

  public currentDate: Date;
  public showQuickInfo: Boolean = false;
  public environmentResultId: number;
  public environmentResult: TestDeviceResult;
  public showFilter: Boolean = false;
  public showList: String;
  public activeTab: string;
  public isSearchEnable: boolean;
  @ViewChild('searchMachineInput') searchMachineInput: ElementRef;
  public autoRefreshSubscription: Subscription;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public isMachineRunning: boolean = false;
  public isFilterAppliedTCR: boolean = false;
  public isFilterAppliedTSR: boolean = false;
  @ViewChild(TestMachineTestCaseResultsComponent)
  private testCaseResults: TestMachineTestCaseResultsComponent;
  @ViewChild(TestMachineTestSuiteResultsComponent)
  private testSuiteResults: TestMachineTestSuiteResultsComponent;
  public isMachineFetchingCompleted: boolean = false;
  public isCaseLevelExecution: boolean = false;
  inputValue: any;

  constructor(
              private route: ActivatedRoute,
              private environmentService: EnvironmentService,
              private environmentResultService: TestDeviceResultService,
              private location: Location,
              private matModal: MatDialog) {
    super();
  }

  get showTestCaseResults() {
    return this.showList == 'TCR';
  }

  get showTestSuiteResults() {
    return this.showList == 'TSR';
  }

  get showTestCaseResultFilter() {
    return this.showFilter && this.showList == 'TCR';
  }

  get showTestSuiteResultFilter() {
    return this.showFilter && this.showList == 'TSR';
  }

  ngOnInit() {
    this.activeTab = 'steps';
    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      this.environmentResultId = params.resultId;
      this.fetchEnvironmentResult();
      this.attachAutoRefreshEvents();
    });
    this.currentDate = new Date();
  }

  ngOnDestroy(): void {
    this.removeAutoRefresh();
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

  fetchEnvironmentResult() {
    this.isMachineRunning = false;
    this.environmentResultService.show(this.environmentResultId).subscribe(res => {
      this.environmentResult = res;
      if (this.environmentResult.isQueued) {
        this.isMachineRunning = true;
        this.addAutoRefresh();
      } else if (!this.environmentResult.isQueued) {
        this.removeAutoRefresh();
      }
      if (this.environmentResult.testPlanResult.environmentId)
        this.environmentService.show(this.environmentResult.testPlanResult.environmentId).subscribe(res => this.environmentResult.testPlanResult.environment = res);
      this.showList = 'TCR';
      new TestPlanResult().consolidateCount(this.environmentResult)
      this.isMachineFetchingCompleted = true;
    });
  }


  loadVideo() {
    if (this.environmentResult.testDeviceSettings.createSessionAtCaseLevel)
      this.isCaseLevelExecution = true;
  }

  compareLogURLs(o1: string, o2: string): boolean {
    return o1 == o2;
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

  toggleView(view: String) {
    this.showList = view;
  }

  resetFilter() {
    this.filterAction({applyFilter: false});
    this.toggleFilter()
  }

  filterAction(event?) {
    if (event && this.showTestCaseResults) {
      this.isFilterAppliedTCR = event.applyFilter;
      this.testCaseResults && this.testCaseResults.filterAction(event);
    } else if (event && this.showTestSuiteResults) {
      this.isFilterAppliedTSR = event.applyFilter;
      this.testSuiteResults && this.testSuiteResults.filterAction(event);
    }
  }

  clearSearch() {
    this.inputClear();
  }

  inputClear() {
    this.testCaseResults && this.testCaseResults.filterAction({applyFilter: false});
    this.inputValue = null;
  }

  toggleSearch() {
    this.isSearchEnable = !this.isSearchEnable;
    if (this.isSearchEnable) {
      setTimeout(() => {
        this.searchMachineInput.nativeElement.focus();
        this.attachDebounceEvent();
      }, 10);
    } else if (this.showTestSuiteResults) {
      this.testCaseResults && this.testSuiteResults.filterAction({applyFilter: false});
    } else if (this.showTestCaseResults) {
      this.testCaseResults && this.testCaseResults.filterAction({applyFilter: false});
    }
  }

  attachDebounceEvent() {
    fromEvent(this.searchMachineInput.nativeElement, 'keyup')
      .pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((event: KeyboardEvent) => {
          if (this.searchMachineInput.nativeElement.value) {
            if (this.showTestSuiteResults) {
              this.testSuiteResults && this.testSuiteResults.filterAction({
                applyFilter: true,
                nameBased: "suiteName:*" + this.searchMachineInput.nativeElement.value + "*"
              });
            } else if (this.showTestCaseResults) {

              this.testCaseResults && this.testCaseResults.filterAction({
                applyFilter: true,
                nameBased: "testCaseName:*" + this.searchMachineInput.nativeElement.value + "*"
              });
            }
          } else {
            if (this.showTestSuiteResults) {
              this.testSuiteResults && this.testSuiteResults.filterAction({applyFilter: false});
            } else if (this.showTestCaseResults) {
              this.testCaseResults && this.testCaseResults.filterAction({applyFilter: false});
            }
          }
        })
      )
      .subscribe();
  }

  private attachAutoRefreshEvents() {
    document.addEventListener("visibilitychange", () => {
      document.hidden ? this.removeAutoRefresh() : this.addAutoRefresh(true);
    });
  }

  private addAutoRefresh(listenerChangeTrue?: boolean) {
    if (listenerChangeTrue && this.environmentResult?.isExecuting && !this.isDisabledAutoRefresh) {
      this.fetchEnvironmentResult();
    }
    this.removeAutoRefresh();
    if (this.environmentResult?.isExecuting && !this.isDisabledAutoRefresh)
      this.autoRefreshSubscription = interval(this.autoRefreshInterval).subscribe(() => {
        this.fetchEnvironmentResult();
      });
  }

  private removeAutoRefresh() {
    if (this.autoRefreshSubscription)
      this.autoRefreshSubscription.unsubscribe();
  }

}
