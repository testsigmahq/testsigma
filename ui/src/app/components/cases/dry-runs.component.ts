/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, OnInit} from '@angular/core';
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TranslateService} from '@ngx-translate/core';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {BaseComponent} from "../../shared/components/base.component";
import {ResultConstant} from "../../enums/result-constant.enum";
import {StatusConstant} from "../../enums/status-constant.enum";
import {ActivatedRoute, Router} from '@angular/router';
import {DryRunFormComponent} from "../webcomponents/dry-run-form.component";
import {MatDialog} from "@angular/material/dialog";
import { interval, Subscription } from 'rxjs';
import {TestCaseService} from "../../services/test-case.service";
import {TestCase} from "../../models/test-case.model";
import {DryTestPlan} from "../../models/dry-test-plan.model";
import {TestStepService} from "../../services/test-step.service";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {DryTestPlanService} from "../../services/dry-test-plan.service";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {Page} from "../../shared/models/page";
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestDeviceService} from "../../services/test-device.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-case-dry-runs',
  templateUrl: './dry-runs.component.html',
  styles: []
})
export class DryRunsComponent extends BaseComponent implements OnInit {

  public dryTestCaseResults: InfiniteScrollableDataSource;
  public testCaseId: number;
  public autoRefreshSubscription: Subscription;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public isRunning: boolean;
  public testCase: TestCase;
  public inTransit: Boolean;
  noSteps: any;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    private testPlanResultService: TestPlanResultService,
    private testCaseResultService: TestCaseResultService,
    private dryTestPlanService: DryTestPlanService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private router: Router,
    private matDialog: MatDialog,
    private executionEnvironmentService: TestDeviceService,
    private testCaseService: TestCaseService,
    private testStepService: TestStepService) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit(): void {
    this.testCaseId = this.route.parent.snapshot.params.testCaseId;
    this.fetchTestCaseResults();
    this.fetchTestCase();
    this.attachAutoRefreshEvents();
    this.fetchTestSteps()
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

  fetchTestCase() {
    this.testCaseService.show(this.testCaseId).subscribe(res => {
      this.testCase = res;
    })
  }

  fetchTestCaseResults() {
    this.isRunning = false;
    this.removeAutoRefresh();
    this.dryTestCaseResults = new InfiniteScrollableDataSource(this.testCaseResultService, "entityType:ADHOC_TEST_PLAN,iteration:null,testCaseId:" + this.testCaseId, "id,desc");
    this.testCaseResultService.findAll("entityType:ADHOC_TEST_PLAN,iteration:null,testCaseId:" + this.testCaseId, "id,desc").subscribe(res => {
      res.content.filter(dryexecution => {
        if (dryexecution && dryexecution.isQueued) {
          this.isRunning = true;
          this.addAutoRefresh();
          return;
        }
      })
    })
  }

  stop(testPlanResult: TestPlanResult) {
    testPlanResult.result = ResultConstant.STOPPED;
    testPlanResult.status = StatusConstant.STATUS_COMPLETED;
    this.testPlanResultService.update(testPlanResult).subscribe(() => {
      this.translate.get("execution.stopped.success").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.fetchTestCaseResults();
      })
    })
  }

  reRun(execution: DryTestPlan) {
    this.inTransit=true;
    let dryExecution = new DryTestPlan().deserialize(execution.serialize());
    this.executionEnvironmentService.findAll("testPlanId:"+execution.id).subscribe((res)=> {
      dryExecution.environments = res.content;
      dryExecution.testCaseId = this.testCase.id;
      delete dryExecution.id;
      this.dryTestPlanService.create(dryExecution).subscribe((res: TestPlanResult) => {
        this.translate.get("execution.initiate.success").subscribe((message: string) => {
          this.showNotification(NotificationType.Success, message);
          this.testCaseResultService.findAll("testPlanResultId:"+res.id+",iteration:null", "id,desc").subscribe((res: Page<TestCaseResult>) => {
            this.router.navigate(['/td', 'test_case_results', res?.content[0]?.id]);
          });
        })
      }, error => {
        this.showAPIError(error, this.translate.instant("execution.initiate.failure"))
      })
    });
  }


  openDryRun() {
    this.matDialog.open(DryRunFormComponent, {
      height: "100vh",
      width: '60%',
      position: {top: '0px', right: '0px'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        testCaseId: this.testCaseId
      },
    })
  }
  attachAutoRefreshEvents() {
    document.addEventListener("visibilitychange", () => {
      document.hidden ? this.removeAutoRefresh() : this.addAutoRefresh(true);
    });
  }

  addAutoRefresh(listenerChangeTrue?: boolean) {
    if (listenerChangeTrue && this.isRunning && !this.isDisabledAutoRefresh){
      this.fetchTestCaseResults();
    }
    this.removeAutoRefresh();
    if (!this.isRunning || this.isDisabledAutoRefresh)
      return;
    this.autoRefreshSubscription = interval(this.autoRefreshInterval).subscribe(() => {
      this.fetchTestCaseResults();
    });
  }

  removeAutoRefresh() {
    if (this.autoRefreshSubscription) {
      this.isRunning = false;
      this.autoRefreshSubscription.unsubscribe();
    }
  }

  fetchTestSteps() {
    let query = "testCaseId:" + this.testCaseId;
    this.testStepService.findAll(query).subscribe(res => {
      this.noSteps = res.empty;
      this.testCaseService.emitStepLength(res?.content?.length);
    });
  }
}
