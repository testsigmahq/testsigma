import {Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild} from '@angular/core';
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {ResultConstant} from "../../enums/result-constant.enum";
import {fromEvent, Subscription} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import { AuthenticationGuard } from 'app/shared/guards/authentication.guard';
import { NotificationsService } from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from "../../shared/components/base.component";
import {EntityType} from "../../enums/entity-type.enum";

@Component({
  selector: 'app-test-case-results',
  templateUrl: './test-case-results.component.html',
  styles: []
})
export class TestCaseResultsComponent extends BaseComponent implements OnInit {
  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Input('showFilter') showFilter: Boolean;
  @Input('showRunDetails') showRunDetails: Boolean;
  @Input('isExecutionRunning') isExecutionRunning: Boolean;
  @Output('toggleFilterAction') toggleFilterAction = new EventEmitter<Boolean>();
  @Output('toggleDetailsAction') toggleDetailsAction = new EventEmitter<Boolean>();
  @Output('toggleViewAction') toggleViewAction = new EventEmitter<String>();
  @Output('autoRefreshIntervalAction') autoRefreshIntervalAction = new EventEmitter<Number>();
  @Output('toggleAutoRefreshAction') toggleAutoRefreshAction = new EventEmitter<Boolean>();

  public testCaseResultsDataSource: InfiniteScrollableDataSource;
  public filterResult: ResultConstant[];
  public isFilterApplied: Boolean;
  public autoRefreshSubscription: Subscription;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public isSearchEnable: boolean = false;
  public isRunTestcaseFetchComplete: boolean = false;
  public runResultEntityType: EntityType = EntityType.RUN_RESULT;
  @ViewChild('searchMachineInput') searchMachineInput: ElementRef;
  inputValue: any;

  constructor(
    private testCaseResultService: TestCaseResultService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.testCaseResultsDataSource = new InfiniteScrollableDataSource(this.testCaseResultService, "iteration:null,testPlanResultId:" + this.testPlanResult.id);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes["showFilter"] && !changes["showFilter"].firstChange) {
      return
    }
    this.testCaseResultsDataSource = new InfiniteScrollableDataSource(this.testCaseResultService, "iteration:null,testPlanResultId:" + this.testPlanResult.id);
    this.isRunTestcaseFetchComplete = true;
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
    this.testCaseResultsDataSource = undefined;
    let query = "iteration:null,testPlanResultId:" + this.testPlanResult.id;
    this.testCaseResultsDataSource = new InfiniteScrollableDataSource(this.testCaseResultService, query);
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
    let applyFilter: Boolean = event.applyFilter;
    let filterResult: ResultConstant[] = event.filterResult;
    let filterTestCaseTypes: string[] = event.filterTestCaseTypes
    let filterTestCasePriorities: string[] = event.filterTestCasePriorities
    let query = "iteration:null,testPlanResultId:" + this.testPlanResult.id;
    this.isFilterApplied = false;
    this.filterResult = undefined;
    if (applyFilter) {
      this.isFilterApplied = true;
      this.filterResult = filterResult;
      if (this.filterResult && this.filterResult.length) {
        query += ",result@" + this.filterResult.join("#");
      }
      if (filterTestCaseTypes && filterTestCaseTypes.length) {
        query += ",testCaseTypeId@" + filterTestCaseTypes.join("#");
      }
      if (filterTestCasePriorities && filterTestCasePriorities.length) {
        query += ",priorityId@" + filterTestCasePriorities.join("#");
      }
    }
    this.testCaseResultsDataSource = new InfiniteScrollableDataSource(this.testCaseResultService, query);
  }

  toggleSearch() {
    this.isSearchEnable = !this.isSearchEnable;
    let query = "iteration:null,testPlanResultId:" + this.testPlanResult.id;
    if (this.isSearchEnable) {
      setTimeout(() => {
        this.searchMachineInput.nativeElement.focus();
        this.attachDebounceEvent();
      }, 10);
    } else {
      this.testCaseResultsDataSource = new InfiniteScrollableDataSource(this.testCaseResultService, query);
    }
  }

  attachDebounceEvent() {
    fromEvent(this.searchMachineInput.nativeElement, 'keyup')
      .pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((event: KeyboardEvent) => {
          let query = "iteration:null,testPlanResultId:" + this.testPlanResult.id;
          if (this.searchMachineInput.nativeElement.value) {
            query += ",testCaseName:*" + this.searchMachineInput.nativeElement.value + "*";
            this.testCaseResultsDataSource = new InfiniteScrollableDataSource(this.testCaseResultService, query);
          } else {
            this.testCaseResultsDataSource = new InfiniteScrollableDataSource(this.testCaseResultService, query);
          }
        })
      )
      .subscribe();
  }
}
