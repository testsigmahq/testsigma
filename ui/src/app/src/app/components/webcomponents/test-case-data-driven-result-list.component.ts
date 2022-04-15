import {Component, ElementRef, EventEmitter, Inject, OnInit, Optional, Output, ViewChild} from '@angular/core';
import {InfiniteScrollableDataSource} from "../../../../data-sources/infinite-scrollable-data-source";
import {fromEvent, Subscription} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";
import {TestCaseResult} from "../../../../models/test-case-result.model";
import {TestCaseDataDrivenResultService} from "../../../../services/test-case-data-driven-result.service";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TestCaseDataDrivenResult} from "../../../../models/test-case-data-driven-result.model";
import { CdkVirtualScrollViewport } from '@angular/cdk/scrolling';

@Component({
  selector: 'app-test-case-data-driven-result-list',
  template: `
    <div class="theme-section-header pt-18 pb-15 px-15 border-0 ts-col-100 align-items-center">
      <div
        class="search-form-group ts-form text-right p-0 ts-col-100-36">
        <input
          class="form-control border-0 d-inline-block p-0 w-90"
          #searchInput
          [placeholder]="'step_result.data_set_name' | translate">
      </div>
      <div class="ml-auto">
    <span
      [matTooltip]="'hint.message.common.search' | translate"
      class=" section-action-icons" (click)="focusOnSearch()">
    <i class="fa-search fz-13"></i>
    </span>
      </div>
    </div>
    <cdk-virtual-scroll-viewport
      itemSize="87"
      minBufferPx="0px"
      maxBufferPx="0px"
      class="card-container virtual-scroll-viewport outer-sm-pm theme-only-items-scroll md-h viewport-height results-list ts-col-100"
      [style.pointer-events]="disableMouse ?'none': 'all'">
      <a class="list-card md-pm"
         [class.with-bg-0]="testCaseDataDrivenResult?.iterationResult?.isFailed"
         [class.with-bg-1]="testCaseDataDrivenResult?.iterationResult?.isPassed"
         [class.with-bg-2]="testCaseDataDrivenResult?.iterationResult?.isAborted"
         [class.with-bg-3]="testCaseDataDrivenResult?.iterationResult?.isNotExecuted"
         [class.with-bg-5]="testCaseDataDrivenResult?.iterationResult?.isQueued"
         [class.with-bg-6]="testCaseDataDrivenResult?.iterationResult?.isStopped"
         [routerLink]="[currentUrl[1], currentUrl[2], testCaseDataDrivenResult.iterationResult?.lastRun?.id || testCaseDataDrivenResult.iterationResultId]"
         [routerLinkActive]="'active'"
         (click)="setIteration(testCaseDataDrivenResult?.iterationResultId)"
         [class.active]="isIterationActive(testCaseDataDrivenResult.iterationResultId)"
         *cdkVirtualFor='let testCaseDataDrivenResult of testCaseDataDrivenResults; let iteration=index;'>
        <div class="ts-col-100 list-title fz-14">
          <span [textContent]="testCaseDataDrivenResult?.iterationResult?.testDataSetName || testCaseDataDrivenResult?.testData?.name"></span>
          <span class="pl-7 fz-11">(
            <i class="fa-info fz-9 mr-2" [matTooltip]="'hint.test_data.expected_to_fail'| translate"></i>
            <span [translate]="'test_data.expected_to_fail.'+(testCaseDataDrivenResult?.testData?.expectedToFail)"></span>
            )
          </span>
        </div>
        <div class="ts-col-100 d-flex align-items-center pt-5 chart-flex-wrap">
          <app-result-pie-chart-column
            class="row-chart-status test123"
            [width]="20"
            [height]="20"
            [resultEntity]="testCaseDataDrivenResult?.iterationResult?.lastRun || testCaseDataDrivenResult?.iterationResult"></app-result-pie-chart-column>
          <div class="ml-auto fz-12 text-t-secondary d-flex">
            <app-re-run-icon [resultEntity]="testCaseDataDrivenResult?.iterationResult"></app-re-run-icon>
            <app-duration-format
              *ngIf="!testCaseDataDrivenResult?.iterationResult?.isExecuting"
              [duration]="testCaseDataDrivenResult?.iterationResult?.duration"></app-duration-format>
          </div>
        </div>
      </a>
      <div *ngIf="testCaseDataDrivenResults?.isEmpty" class="empty-full-container-transparent-bg">
        <div class="empty-full-content">
          <div class="empty-run-xs"></div>
          <div
            class="text-t-secondary pt-30 pb-18"
            [translate]="'message.common.search.not_found'"></div>
        </div>
      </div>
      <app-placeholder-loader *ngIf="testCaseDataDrivenResults?.isFetching"></app-placeholder-loader>
    </cdk-virtual-scroll-viewport>
  `,
  styles: []
})
export class TestCaseDataDrivenResultListComponent implements OnInit {
  public testCaseDataDrivenResults: InfiniteScrollableDataSource;
  @ViewChild('searchInput', {static: true}) searchInput: ElementRef;
  public isDataDrivenFetchCompleted: boolean = false;
  public currentTestCaseDataDrivenResult: TestCaseDataDrivenResult;
  public currentUrl: string[];
  public testCaseDataDrivenResultService: TestCaseDataDrivenResultService;
  @Output() setDrivenResult = new EventEmitter<any>();
  @ViewChild(CdkVirtualScrollViewport) viewPort: CdkVirtualScrollViewport;
  scrollSubscription: Subscription;
  disableMouse: boolean = true;

  constructor(
    @Optional() @Inject(MAT_DIALOG_DATA) public data: {
      resultEntity: TestCaseResult,
      testCaseDataDrivenResultService: TestCaseDataDrivenResultService},
    public router: Router) {
    this.testCaseDataDrivenResultService = this.data.testCaseDataDrivenResultService;
  }

  ngOnInit(): void {
    this.fetchIterations("");
  }

  fetchIterations(query?: string) {
    this.scrollSubscription?.unsubscribe();
    this.currentUrl = this.router.url.split('/');
    query += ",testCaseResultId:" + this.data.resultEntity?.parentId;
    this.testCaseDataDrivenResults = new InfiniteScrollableDataSource(this.testCaseDataDrivenResultService, query);
    this.isDataDrivenFetchCompleted = true;
    this.searchDataSets();
    this.scrollIterationToView();
  }

  scrollIterationToView() {
    if(this.testCaseDataDrivenResults?.isFetching) {
      setTimeout(() => this.scrollIterationToView(), 500);
      return;
    }
    let activeItemInCache = this.testCaseDataDrivenResults.cachedItems?.find( testCaseResult =>
      testCaseResult['iterationResultId']==this.currentUrl[3]);
    let activeItemVisible = Boolean(activeItemInCache)? ((activeItemInCache['iterationResultId']
      - activeItemInCache['testCaseResultId'] ) <= this.viewPort.getRenderedRange().end)
      &&((activeItemInCache['iterationResultId']
        - activeItemInCache['testCaseResultId'] ) >= this.viewPort.getRenderedRange().start) : false;
    if(activeItemInCache && !activeItemVisible) {
      this.viewPort.scrollToIndex(((activeItemInCache['iterationResultId'] - activeItemInCache['testCaseResultId']) -1 ));
      this.scrollAndUnSubscribe();
    } else if(!Boolean(activeItemInCache)){
      if(this.viewPort.getRenderedRange().end ==0) return;
      this.viewPort.scrollToIndex(this.viewPort.getRenderedRange().end, "smooth");
      this.scrollAndUnSubscribe();
    }
    if(activeItemInCache && activeItemVisible)
      this.completeScroll(activeItemInCache);
    else
      this.disableMouse = true;
  }

  private completeScroll(activeItemInCache) {
    if (Boolean(this.viewPort.elementRef.nativeElement.querySelector("a.list-card.active"))) {
      if ((this.viewPort.elementRef.nativeElement.querySelector("a.list-card.active").getClientRects()[0].top - this.viewPort.elementRef.nativeElement.getClientRects()[0].top)
        < (87 * (this.testCaseDataDrivenResults.cachedItems.indexOf(activeItemInCache) + 1))){
        this.viewPort.scrollToIndex(this.testCaseDataDrivenResults.cachedItems.indexOf(activeItemInCache), "smooth");
        this.disableMouse=false;
      } else {
        setTimeout(() => this.completeScroll(activeItemInCache), 500);
      }
    } else {
      this.disableMouse=false;
    }
  }

  searchDataSets() {
    if (this.searchInput) {
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            this.fetchIterations("testDataName:*" + this.searchInput.nativeElement.value + "*");
          })
        )
        .subscribe();
    } else {
      setTimeout(() => this.searchDataSets(), 100);
    }
  }

  focusOnSearch() {
    this.searchInput.nativeElement.focus();
  }

  //TODO: [Pratheepv] Bad need to check why cdk virtual scroll and [routerActiveLink] not working together
  isIterationActive(id) {
    return this.router.url.indexOf("test_case_results/" + id) > 0;
  }

  setIteration(id) {
    this.disableMouse = true;
    this.testCaseDataDrivenResults.cachedItems.find((item: TestCaseDataDrivenResult) => {
      if (item['iterationResultId'] == id) {
        this.currentTestCaseDataDrivenResult = item;
        this.setDrivenResult.emit(item)
      }
    });
  }

  private scrollAndUnSubscribe() {
    this.scrollSubscription= this.viewPort.renderedRangeStream.subscribe(res => {
      if(!this.disableMouse) return;
      this.scrollSubscription?.unsubscribe();
      this.scrollIterationToView();
    });
  }

}
