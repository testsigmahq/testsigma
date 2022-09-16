/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Component, ElementRef, Inject, Input, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestCaseDataDrivenResultService} from "../../services/test-case-data-driven-result.service";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {Router} from '@angular/router';
import {TestCaseDataDrivenResult} from "../../models/test-case-data-driven-result.model";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";
import {MatDialog, MatDialogConfig, MatDialogRef} from '@angular/material/dialog';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TestCaseDataDrivenResultListComponent} from "../../src/app/components/webcomponents/test-case-data-driven-result-list.component";
import {ResultConstant} from "../../enums/result-constant.enum";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {EnvironmentService} from "../../services/environment.service";

@Component({
  selector: 'app-test-case-data-driven-results',
  templateUrl: './test-case-data-driven-results.component.html',
  styles: []
})
export class TestCaseDataDrivenResultsComponent implements OnInit {
  @Input('resultEntity') resultEntity: TestCaseResult;
  public testCaseDataDrivenResults: InfiniteScrollableDataSource;
  public currentTestCaseDataDrivenResult: TestCaseDataDrivenResult;
  @ViewChild('filter') overlayDir: CdkConnectedOverlay;
  @ViewChild('searchInput', {static: true}) searchInput: ElementRef;
  public isDataDrivenFetchCompleted: boolean = false;
  public showDataList: boolean = false;

  @ViewChild('dataDrivenListBtn') public dataDrivenListBtn: ElementRef;
  private dataDrivenResultList: MatDialogRef<TestCaseDataDrivenResultListComponent>;

  constructor(
    @Inject(TestCaseDataDrivenResultService) public testCaseDataDrivenResultService: TestCaseDataDrivenResultService,
    public environmentService: EnvironmentService,
    @Inject(TestCaseResultService) public testCaseResultService: TestCaseResultService,
    public router: Router,
    public matModal: MatDialog,
    public authGuard: AuthenticationGuard) {
  }

  ngOnInit(): void {
    this.fetchTestCaseResult();
    this.fetchIterations("")
  }

  ngOnChanges(changes: SimpleChanges) {
    if (((this.resultEntity?.isExecuting || this.resultEntity?.lastRun?.isExecuting)) || this.getStatus(changes)) {
     this.fetchTestCaseResult()
    }
    this.fetchIterations("")
  }

  getStatus(changes) {
    if (changes["resultEntity"] && changes["resultEntity"].firstChange) {
      return false
    } else if(changes["resultEntity"] && changes["resultEntity"]["previousValue"]) {
      return  changes["resultEntity"]["previousValue"]["result"] == ResultConstant.QUEUED ||
        changes["resultEntity"]["previousValue"]["lastRun"]?.result == ResultConstant.QUEUED;
    }
  }

  fetchTestCaseResult() {
    this.testCaseResultService.show(this.resultEntity?.id).subscribe(res => {
        this.resultEntity = res;
        if (this.resultEntity?.testDeviceResult?.testPlanResult?.environmentId)
          this.environmentService.show(this.resultEntity?.testDeviceResult?.testPlanResult?.environmentId).subscribe(res => {
            this.resultEntity.testDeviceResult.testPlanResult.environment = res;
          })
    })
  }

  fetchIterations(query?: string) {
    query += ",testCaseResultId:" + this.resultEntity.parentId;
    this.testCaseDataDrivenResults = new InfiniteScrollableDataSource(this.testCaseDataDrivenResultService, query);
    this.isDataDrivenFetchCompleted = true;
    this.setCurrentIteration();
    if(Boolean(this.dataDrivenResultList)) {
      this.dataDrivenResultList.componentInstance.data = null;
      this.dataDrivenResultList.componentInstance.data = {
        resultEntity: this.resultEntity,
        testCaseDataDrivenResultService: this.testCaseDataDrivenResultService
      };
      this.dataDrivenResultList.componentInstance.fetchIterations("");
    }
  }

  // setIteration(id) {
  //   this.testCaseDataDrivenResults.cachedItems.find((item: TestCaseDataDrivenResult) => {
  //     if (item['iterationResultId'] == id)
  //       this.currentTestCaseDataDrivenResult = item;
  //   });
  // }

  setCurrentIteration() {
    if (this.testCaseDataDrivenResults?.cachedItems[0]) {
      this.currentTestCaseDataDrivenResult = <TestCaseDataDrivenResult>this.testCaseDataDrivenResults.cachedItems.find((item: TestCaseDataDrivenResult) => {
        return item.iterationResultId == this.resultEntity.id;
      });
      if(!this.currentTestCaseDataDrivenResult){
        this.fetchIterations("iterationResultId:" + this.resultEntity.id);
      }
      // if (!this.currentTestCaseDataDrivenResult) {
      //   this.currentTestCaseDataDrivenResult = <TestCaseDataDrivenResult>this.testCaseDataDrivenResults.cachedItems[0]
      // } else {
      //   this.setIteration(this.currentTestCaseDataDrivenResult.id)
      // }
    } else {
      setTimeout(() => this.setCurrentIteration(), 300);
    }
  }

  openSecondaryMenu() {
    this.dataDrivenResultList = this.matModal.open(TestCaseDataDrivenResultListComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      width: 'calc((100% - 35px) - 77%)',
      height: "calc(100vh - 207px)",
      panelClass: ['mat-overlay'],
      data: {resultEntity: this.resultEntity, testCaseDataDrivenResultService: this.testCaseDataDrivenResultService}
    });


    const matDialogConfig = new MatDialogConfig();
    const rect: DOMRect = this.dataDrivenListBtn.nativeElement.getBoundingClientRect();
    matDialogConfig.position = {left: `${rect.left-45}px`, top: `${rect.bottom+3}px`,bottom: '0'}
    this.dataDrivenResultList.updatePosition(matDialogConfig.position);
    this.dataDrivenResultList.afterOpened().subscribe(()=> {
      this.showDataList = true;
    })
    this.dataDrivenResultList.afterClosed().subscribe(()=> {
      this.showDataList = false;
    })

    this.dataDrivenResultList.componentInstance.setDrivenResult.subscribe((drivenResult) => {
      this.currentTestCaseDataDrivenResult = drivenResult;
    })
  }


}
