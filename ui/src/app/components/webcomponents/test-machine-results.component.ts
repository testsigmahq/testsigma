import {Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {fromEvent, Subscription} from "rxjs";
import {ResultConstant} from "../../enums/result-constant.enum";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {formatDate} from "@angular/common";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import { NotificationsService } from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-machine-results',
  templateUrl: './test-machine-results.component.html',
  styles: []
})
export class TestMachineResultsComponent extends BaseComponent implements OnInit {
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
  @ViewChild('searchMachineInput') searchMachineInput: ElementRef;

  public testDeviceResults: InfiniteScrollableDataSource;
  public isFilterApplied: boolean;
  public filterResult: ResultConstant[];
  public isRunMachineFetchComplete: boolean = false;
  inputValue: any;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private environmentResultService: TestDeviceResultService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes["showFilter"] && !changes["showFilter"].firstChange) {
      return
    }
    this.testDeviceResults = new InfiniteScrollableDataSource(this.environmentResultService, ",testPlanResultId:" + this.testPlanResult.id);
    this.isRunMachineFetchComplete = true;
  }

  toggleAutoRefresh(isDisabledAutoRefresh: boolean) {
    this.isDisabledAutoRefresh = isDisabledAutoRefresh;
    this.toggleAutoRefreshAction.emit(this.isDisabledAutoRefresh);
  }

  changeAutoRefreshTime(event: number) {
    this.autoRefreshInterval = event;
    this.autoRefreshIntervalAction.emit(this.autoRefreshInterval);
  }


  toggleFilter(communicateToParent?: Boolean) {
    this.showRunDetails = false;
    this.showFilter = !this.showFilter;
    if (communicateToParent)
      this.toggleFilterAction.emit(false);
  }

  clearSearch() {
    this.testDeviceResults = undefined;
    let query = "testPlanResultId:" + this.testPlanResult.id;
    this.testDeviceResults = new InfiniteScrollableDataSource(this.environmentResultService, query);
    this.inputClear();
  }
  inputClear(){
    setTimeout(() => this.searchMachineInput.nativeElement.value = null,500);
    this.inputValue = null;
  };

  toggleDetails(communicateToParent?: Boolean) {
    this.showFilter = false;
    this.showRunDetails = !this.showRunDetails;
    if (communicateToParent)
      this.toggleDetailsAction.emit(false);
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

    this.testDeviceResults = new InfiniteScrollableDataSource(this.environmentResultService, query);
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
      this.testDeviceResults = new InfiniteScrollableDataSource(this.environmentResultService, query);
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
            query += ",environmentName:*" + this.searchMachineInput.nativeElement.value + "*";
            this.testDeviceResults = new InfiniteScrollableDataSource(this.environmentResultService, query);
          } else {
            this.testDeviceResults = new InfiniteScrollableDataSource(this.environmentResultService, query);
          }
        })
      )
      .subscribe();
  }

  getDeviceAllocationTime(environmentResult) {
    return "Created At:\n" + formatDate(environmentResult.executionInitiatedOn, 'MMM d, h:mm:ss', 'en-US') +
      "\nDevice Allocated At:\n" + formatDate(environmentResult.deviceAllocatedOn, 'MMM d, h:mm:ss', 'en-US') +
      "\nSession Created At:\n" + formatDate(environmentResult.sessionCreatedOn, 'MMM d, h:mm:ss', 'en-US') +
      "\nSession Completed At:\n" + formatDate(environmentResult.sessionCompletedOn, 'MMM d, h:mm:ss', 'en-US');
  }
}
