import {Component, ElementRef, Input, OnInit, ViewChild, Output, EventEmitter, SimpleChanges} from '@angular/core';
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {RunListFilterComponent} from "./run-list-filter.component";
import {RunListInfoComponent} from "./run-list-info.component";
import {BaseComponent} from "../../shared/components/base.component";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {fromEvent, Subscription} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {ResultConstant} from "../../enums/result-constant.enum";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {FilterTimePeriod} from "../../enums/filter-time-period.enum";

import * as moment from 'moment';
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {ExecutionTriggeredType} from "../../enums/triggered-type.enum";
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';
import set = Reflect.set;

@Component({
  selector: 'app-run-list',
  templateUrl: './run-list.component.html',
  styles: []
})
export class RunListComponent extends BaseComponent implements OnInit {
  @Input('testPlan') testPlan: TestPlan;
  @Input('runId') currentRunId: number;
  @Input('testPlanResult') testPlanResult: TestPlanResult;
  @Output('onExecutionResult') onExecutionResult = new EventEmitter<TestPlanResult>();

  @ViewChild('searchInput') searchInput: ElementRef;
  @ViewChild('buildNoInput') buildNoInput: ElementRef;
  @ViewChild(CdkVirtualScrollViewport) viewPort: CdkVirtualScrollViewport;
  public testPlanResults: InfiniteScrollableDataSource;
  public isSearch: boolean = false;
  public activeExecutionResult: TestPlanResult;
  public disableMouse: boolean = true;

  @ViewChild('runListFilterBtn') public runListFilterBtn: ElementRef;
  public filterDialogRef: MatDialogRef<RunListFilterComponent>;
  public filterResult: ResultConstant[];
  public filterStartTime: FilterTimePeriod;
  public isFilterApplied: Boolean;
  private sortBy: string = "id,desc";
  public isRunListFetchCompleted: boolean = false;
  public  filterTriggeredType : ExecutionTriggeredType[];
  private scrollSubscription: Subscription;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testPlanResultService: TestPlanResultService,
    private matModal: MatDialog,
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.testPlanResults = new InfiniteScrollableDataSource(this.testPlanResultService, "reRunParentId:null,testPlanId:" + this.testPlan.id, this.sortBy);
    this.isRunListFetchCompleted = true;
    this.scrollRunIdIntoView();

  }

  ngOnChanges(changes: SimpleChanges) {
    if (((this.testPlanResult?.isExecuting || this.testPlanResult?.childResult?.isExecuting)) || this.getStatus(changes)) {
      this.testPlanResults = new InfiniteScrollableDataSource(this.testPlanResultService, "reRunParentId:null,testPlanId:" + this.testPlan.id, this.sortBy);
      this.isRunListFetchCompleted = true;
    }
  }

  getStatus(changes) {
    if (changes["executionResult"] && changes["executionResult"].firstChange) {
      return false
    } else if(changes["executionResult"] && changes["executionResult"]["previousValue"]) {
      return  changes["executionResult"]["previousValue"]["result"] == ResultConstant.QUEUED ||
        changes["executionResult"]["previousValue"]["childResult"]["result"] == ResultConstant.QUEUED;
    }
  }

  fetchTestPlanResults(query) {
    query += ",reRunParentId:null,testPlanId:" + this.testPlan.id;
    this.testPlanResults = new InfiniteScrollableDataSource(this.testPlanResultService, query, this.sortBy);
    this.isRunListFetchCompleted = true;
  }

  updateBuildId(event) {
    this.testPlanResultService.update(this.activeExecutionResult).subscribe((result) => {
      this.activeExecutionResult.buildNo = result.buildNo;
      this.onExecutionResult.emit(this.activeExecutionResult)
      this.translate.get("message.common.update.success", {FieldName: 'Build Number'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.activeExecutionResult = null;
      });
    }, () => {
      this.translate.get("message.common.update.failure", {FieldName: 'Build Number'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Error, res);
        this.activeExecutionResult = null;
      });
    });
    event.preventDefault();
    event.stopPropagation();
  }

  showBuildNumberForm(executionResult: TestPlanResult, event) {
    this.activeExecutionResult = executionResult;
    event.stopPropagation();
    event.preventDefault();
    setTimeout(() => {
      this.buildNoInput.nativeElement.focus();
    }, 10);
  }

  hidBuildNumberForm(event) {
    this.activeExecutionResult = undefined;
    event.stopPropagation();
    event.preventDefault();
  };

  openRunInfo() {
    this.matModal.open(RunListInfoComponent, {
      data: {execution: this.testPlan},
      panelClass: ['mat-dialog', 'rds-none']
    })
  }

  openRunFilter() {

    this.filterDialogRef = this.matModal.open(RunListFilterComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      height: 'calc(100% - 208px)',
      width: '364px',
      data: {filterResult: this.filterResult, filterStartTime: this.filterStartTime},
      panelClass: 'mat-overlay'
    });

    const matDialogConfig = new MatDialogConfig();
    const rect: DOMRect = this.runListFilterBtn.nativeElement.getBoundingClientRect();
    matDialogConfig.position = {left: `${rect.right + 30}px`, top: `${rect.top - 15}px`}
    this.filterDialogRef.updatePosition(matDialogConfig.position);

    this.filterDialogRef.componentInstance.filterAction.subscribe((applyFilter: Boolean) => {
      let query = "";
      this.isFilterApplied = false;
      this.filterResult = undefined;
      if (applyFilter) {
        this.isFilterApplied = true;
        this.filterResult = this.filterDialogRef.componentInstance.filterResult;
        this.filterStartTime = this.filterDialogRef.componentInstance.filterStartTime;
        this.filterTriggeredType = this.filterDialogRef.componentInstance.filterTriggeredType;
        if (this.filterResult)
          query = "result@" + this.filterResult.join("#");
        if (this.filterStartTime) {
          query += ",startTime>" + this.getFormattedDate(this.filterStartTime);
        }
        if(this.filterTriggeredType){
           query = "triggeredType@" + this.filterTriggeredType.join("#");
        }
      }
      this.fetchTestPlanResults(query);
    });
  }

  resetFilter() {
    this.isFilterApplied = false;
    this.filterResult = undefined;
    this.filterStartTime = undefined;
    this.fetchTestPlanResults("");
  }

  attachDebounceEvent() {
    fromEvent(this.searchInput.nativeElement, 'keyup')
      .pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((event: KeyboardEvent) => {
          if (this.searchInput.nativeElement.value)
            this.fetchTestPlanResults("term:*" + this.searchInput.nativeElement.value + "*");
          else
            this.fetchTestPlanResults("");
        })
      )
      .subscribe();
  }

  toggleSearch() {
    this.isSearch = !this.isSearch;
    if (this.isSearch) {
      setTimeout(() => {
        this.searchInput.nativeElement.focus();
        this.attachDebounceEvent();
      }, 10);
    } else {
      this.fetchTestPlanResults("");
    }
  }

  private getFormattedDate(key: FilterTimePeriod): String {
    switch (key) {
      case FilterTimePeriod.TODAY:
        return moment().format("yyyy-MM-DD");
      case FilterTimePeriod.LAST_SEVEN_DAYS:
        return moment().subtract(7, 'd').format("yyyy-MM-DD");
      case FilterTimePeriod.LAST_30_DAYS:
        return moment().subtract(30, 'd').format("yyyy-MM-DD");
      case FilterTimePeriod.LAST_90_DAYS:
        return moment().subtract(90, 'd').format("yyyy-MM-DD");
      case FilterTimePeriod.LAST_180_DAYS:
        return moment().subtract(180, 'd').format("yyyy-MM-DD");
    }
  }

  deleteRun(result: TestPlanResult) {
    this.translate.get('message.common.confirmation.message', {FieldName: "#"+result.id}).subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(res => {
          if (res) {
            this.destroy(result)
          }
        });
    })
    return false;
  }

  destroy(result: TestPlanResult) {
    this.testPlanResultService.destroy(result.id).subscribe(res => {
      this.fetchTestPlanResults("");
      this.translate.get("message.common.deleted.success", {FieldName: '#'+result.id}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
      })
    });
  }

  private scrollRunIdIntoView() {
    if(this.testPlanResults?.isFetching) {
      setTimeout(() => this.scrollRunIdIntoView(), 100);
      return;
    }
    let activeItemInCache = this.testPlanResults.cachedItems?.find( executionResult =>
      executionResult['id']==this.currentRunId);
    let activeItemVisible = Boolean(activeItemInCache)?
      ((this.testPlanResults.cachedItems.indexOf(activeItemInCache) < this.viewPort.getRenderedRange().end)
      && (this.testPlanResults.cachedItems.indexOf(activeItemInCache)) >= this.viewPort.getRenderedRange().start) : false;
    if(activeItemInCache && !activeItemVisible) {
      this.viewPort.scrollToIndex((this.testPlanResults.cachedItems.indexOf(activeItemInCache) -1 ));
      this.scrollAndUnSubscribe();
    } else if(!Boolean(activeItemInCache)){
      if(this.viewPort.getRenderedRange().end ==0) return;
      this.viewPort.scrollToIndex(this.viewPort.getRenderedRange().end);
      this.scrollAndUnSubscribe();
    }
    if(activeItemInCache && activeItemVisible)
      this.completeScroll(activeItemInCache);
    else if(activeItemInCache)
      this.disableMouse = true;
  }

  private completeScroll(activeItemInCache){
    if(Boolean(this.viewPort.elementRef.nativeElement.querySelector("a.list-view.active"))) {
      if ((this.viewPort.elementRef.nativeElement.querySelector("a.list-view.active").getClientRects()[0].top - this.viewPort.elementRef.nativeElement.getClientRects()[0].top)
        < (70 * (this.testPlanResults.cachedItems.indexOf(activeItemInCache) + 1))) {
        this.viewPort.scrollToIndex(this.testPlanResults.cachedItems.indexOf(activeItemInCache), "smooth");
        this.disableMouse = false;
      }else {
        setTimeout(()=> this.completeScroll(activeItemInCache), 500);
      }
    } else
      this.disableMouse=false;
  }

  private scrollAndUnSubscribe() {
    this.scrollSubscription= this.viewPort.renderedRangeStream.subscribe(res => {
      if(!this.disableMouse) return;
      this.scrollSubscription?.unsubscribe();
      setTimeout(()=>this.scrollRunIdIntoView(),10);
    });
  }
}
