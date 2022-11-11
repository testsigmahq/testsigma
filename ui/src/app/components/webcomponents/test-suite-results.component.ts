import {Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {fromEvent, Subscription} from "rxjs";
import {ResultConstant} from "../../enums/result-constant.enum";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {EntityExternalMappingService} from "../../services/entity-external-mapping.service";
import {EntityExternalMapping} from "../../models/entity-external-mapping.model";
import {EntityType} from "../../enums/entity-type.enum";
import {BaseComponent} from "../../shared/components/base.component";

@Component({
  selector: 'app-test-suite-results',
  templateUrl: './test-suite-results.component.html',
  styles: []
})
export class TestSuiteResultsComponent extends BaseComponent implements OnInit {

  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Input('showFilter') showFilter: Boolean;
  @Input('showRunDetails') showRunDetails: Boolean;
  @Input('isExecutionRunning') isExecutionRunning: Boolean;
  @Output('toggleFilterAction') toggleFilterAction = new EventEmitter<Boolean>();
  @Output('toggleDetailsAction') toggleDetailsAction = new EventEmitter<Boolean>();
  @Output('toggleViewAction') toggleViewAction = new EventEmitter<String>();
  @Output('autoRefreshIntervalAction') autoRefreshIntervalAction = new EventEmitter<Number>();
  @Output('toggleAutoRefreshAction') toggleAutoRefreshAction = new EventEmitter<Boolean>();
  public autoRefreshSubscription: Subscription;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public isSearchEnable: boolean = false;
  public isRunSuiteFetchComplete: boolean = false;
  public entityExternalMapping: EntityExternalMapping;
  public suiteResultEntityType: EntityType = EntityType.TEST_SUITE_RESULT;
  @ViewChild('searchMachineInput') searchMachineInput: ElementRef;
  inputValue: any;

  public suiteResults: InfiniteScrollableDataSource;
  public isFilterApplied: boolean;
  public filterResult: ResultConstant[];


  constructor(private testSuiteResultService: TestSuiteResultService,
              public translate: TranslateService,
              public entityExternalMappingService: EntityExternalMappingService) {
    super();
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes["showFilter"] && !changes["showFilter"].firstChange) {
      return
    }
    this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, "testPlanResultId:"
      + this.testPlanResult.id);
    this.isRunSuiteFetchComplete = true;
  }

  toggleAutoRefresh(isDisabledAutoRefresh: boolean) {
    this.isDisabledAutoRefresh = isDisabledAutoRefresh;
    this.toggleAutoRefreshAction.emit(this.isDisabledAutoRefresh);
  }

  changeAutoRefreshTime(event: number) {
    this.autoRefreshInterval = event;
    this.autoRefreshIntervalAction.emit(this.autoRefreshInterval);
  }

  toggleFilter() {
    this.showRunDetails = false;
    this.showFilter = !this.showFilter;
    this.toggleFilterAction.emit(this.showFilter);
  }

  clearSearch() {
    this.suiteResults = undefined;
    let query = "testPlanResultId:" + this.testPlanResult.id;
    this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, query);
    this.inputClear();
  }
  inputClear(){
    setTimeout(() => this.searchMachineInput.nativeElement.value = null,500);
    this.inputValue = null;
  };

  toggleDetails() {
    this.showFilter = false;
    this.showRunDetails = !this.showRunDetails;
    this.toggleDetailsAction.emit(this.showRunDetails);
  }

  toggleView(view: String) {
    this.showFilter = false;
    this.showRunDetails = false;
    this.toggleFilterAction.emit(false);
    this.toggleDetailsAction.emit(false);
    this.toggleViewAction.emit(view);
  }

  resetFilter() {
    this.filter({applyFilter: false});
    this.toggleFilter();
  }

  filter(event) {
    let applyFilter: boolean = event.applyFilter;
    let filterResult: ResultConstant[] = event.filterResult;
    let query = "testPlanResultId:" + this.testPlanResult.id;
    this.isFilterApplied = false;
    this.filterResult = undefined;
    if (applyFilter) {
      this.isFilterApplied = true;
      this.filterResult = filterResult;
      if (this.filterResult && this.filterResult.length) {
        query += ",result@" + this.filterResult.join("#");
      }
    }
    this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, query);
  }

  toggleSearch() {
    this.isSearchEnable = !this.isSearchEnable;
    let query = "testPlanResultId:" + this.testPlanResult.id;
    if (this.isSearchEnable) {
      setTimeout(() => {
        this.searchMachineInput.nativeElement.focus();
        this.attachDebounceEvent();
      }, 10);
    } else {
      this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, query);
    }
  }

  attachDebounceEvent() {
    fromEvent(this.searchMachineInput.nativeElement, 'keyup')
      .pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((event: KeyboardEvent) => {
          let query = "testPlanResultId:" + this.testPlanResult.id;
          if (this.searchMachineInput.nativeElement.value) {
            query += ",suiteName:*" + this.searchMachineInput.nativeElement.value + "*";
            this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, query);
          } else {
            this.suiteResults = new InfiniteScrollableDataSource(this.testSuiteResultService, query);
          }
        })
      )
      .subscribe();
  }

  rePushInitialized(){
    this.translate.get("Push Results initialized Successfully").subscribe((res: string) => {
      this.showNotification(NotificationType.Success, res);
    });
  }

  rePushFailed(){
    this.translate.get("Push Results Failed..! please try again").subscribe((res: string) => {
      this.showNotification(NotificationType.Error, res);
    });
  }
}
