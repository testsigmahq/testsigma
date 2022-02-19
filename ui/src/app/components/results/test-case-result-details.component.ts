import {Component, Inject, Input, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {TestCaseResultSummaryComponent} from "../webcomponents/test-case-result-summary.component";
import {TestCaseResult} from "../../models/test-case-result.model";
import {ActivatedRoute, Params, Router, RouterOutlet} from '@angular/router';
import {TestCaseResultService} from "../../services/test-case-result.service";
import {EnvironmentService} from "../../services/environment.service";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {interval, Subscription} from 'rxjs';
import {ReportBugComponent} from "../webcomponents/report-bug.component";
import {WhyTestCaseFailedHelpComponent} from "../webcomponents/why-test-case-failed-help.component";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {ResultConstant} from "../../enums/result-constant.enum";
import {StatusConstant} from "../../enums/status-constant.enum";
import {TestPlanResultService} from "../../services/test-plan-result.service";
import {TestCaseStatusFormComponent} from "../webcomponents/test-case-status-form.component";
import { CdkConnectedOverlay } from '@angular/cdk/overlay';
import {TestStepResult} from "../../models/test-step-result.model";
import {TestCase} from "../../models/test-case.model";
import {TestCaseService} from "../../services/test-case.service";
import {TestSuiteResultService} from "../../services/test-suite-result.service";
import {DryTestPlanService} from "../../services/dry-test-plan.service";
import {Page} from "../../shared/models/page";
import {DryTestPlan} from "../../models/dry-test-plan.model";
import {TestDeviceService} from "../../services/test-device.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-case-result-details',
  templateUrl: './test-case-result-details.component.html',
  styles: []
})
export class TestCaseResultDetailsComponent extends BaseComponent implements OnInit {

  public testCaseResultId: Number;
  public testCaseResult: TestCaseResult;
  public testCase: TestCase;
  public preReqDryTestCaseResult: TestCaseResult;
  public testStepDetailsOpen = false;
  @ViewChild('detailsRef') overlayDir: CdkConnectedOverlay;

  public activeTab: string;
  public autoRefreshSubscription: Subscription;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public isTestCaseFetchingCompleted: boolean = false;
  public isEditStep: boolean= false;
  public failTestStepResult: TestStepResult;
  public isFetching: boolean;
  public isCaseLevelExecution: boolean = false;
  public isParallelExecution: boolean = false;
  public startSync: boolean = false;
  public queueSizeErrorMessage: string;

  @ViewChild(RouterOutlet) outlet: RouterOutlet;

  constructor(public authGuard: AuthenticationGuard,
              public notificationsService: NotificationsService,
              public translate: TranslateService,
              public toastrService: ToastrService,
              public route: ActivatedRoute,
              public router: Router,
              public environmentService: EnvironmentService,
              public testCaseService: TestCaseService,
              public testSuiteResultService: TestSuiteResultService,
              public environmentResultService: TestDeviceResultService,
              public testCaseResultService: TestCaseResultService,
              public dryTestPlanService: DryTestPlanService,
              public matModal: MatDialog,
              public testPlanResultService: TestPlanResultService,
              public executionEnvironmentService: TestDeviceService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get canShowVideoScreenShort(): Boolean {
    return !((this.isRest ||
        this.testCaseResult?.testDeviceResult?.isExecuting ||
        this.testCaseResult?.isFailed ||
        this.testCaseResult?.isNotExecuted ||
        this.testCaseResult?.isStopped && this.testCaseResult?.stoppedPercentage == 100));
  }

  get isRest(): Boolean {
    return this.testCaseResult?.testDeviceResult?.testPlanResult?.testPlan?.workspaceVersion?.workspace?.isRest;
  }

  get canShowWhyThisFailed(): Boolean {
    return (this.testCaseResult?.isFailed || this.testCaseResult?.isAborted || this.testCaseResult?.isNotExecuted) &&
      !(this.isRest);
  }

  ngOnInit() {
    this.activeTab = 'steps';
    this.route.params.subscribe((params: Params) => {
      this.pushToParent(this.route, params);
      this.testCaseResultId = params.resultId;
      this.fetchTestCaseResult();
      this.attachAutoRefreshEvents();
    });
  }

  ngOnDestroy(): void {
    this.removeAutoRefresh();
  }

  attachAutoRefreshEvents() {
    document.addEventListener("visibilitychange", () => {
      document.hidden ? this.removeAutoRefresh() : this.addAutoRefresh(true);
    });
  }

  addAutoRefresh(listenerChangeTrue?: boolean) {
    if (listenerChangeTrue && this.testCaseResult?.isExecuting && !this.isDisabledAutoRefresh){
      this.fetchTestCaseResult();
    }
    if (this.testCaseResult?.isExecuting && !this.isDisabledAutoRefresh) {
      this.removeAutoRefresh()
      this.autoRefreshSubscription = interval(this.autoRefreshInterval).subscribe(() => {
        this.fetchTestCaseResult();
      });
    }
  }

  removeAutoRefresh() {
    if (Boolean(this.autoRefreshSubscription)) {
      this.autoRefreshSubscription.unsubscribe();
    }
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

  fetchTestCaseResult() {
    this.testCaseResultService.show(this.testCaseResultId).subscribe(res => {
      if(res.childResult)
        this.router.navigate(['/td/test_case_results', res.childResult.id]);
      if (res.isDataDriven) {
        this.testCaseResultService.findAll("parentId:" + res.id).subscribe(res => {
          if(res.content[0])
          this.navigate(res.content[0]);
        });
      } else {
        this.testCaseResult = res;
        if(this.testCaseResult?.message == 'Your Current Plan Reached Max Allowed Queue Size. Please upgrade for higher limits.'){
          this.queueSizeErrorMessage = "Your current plan reached max allowed queue size. Please <a class= 'text-link text-decoration-none px-2' href='javascript:fcWidget.open()' style='text-decoration: none;'>" +
            "contact support</a> to upgrade for higher limits"
        }
        console.log("TestCaseResult", this.testCaseResult);
        this.isTestCaseFetchingCompleted = true;
        this.handleAutoRefresh();
      }
      if (this.testCaseResult?.testCaseId) {
        this.postTestCaseFetch();
      }
    })
  }

  handleAutoRefresh() {
    if (!(this.testCaseResult?.isExecuting || this.testCaseResult?.parentResult?.isExecuting)) {
      this.removeAutoRefresh();
    }
    this.addAutoRefresh();
  }

  openSummary() {
    //TODO revert that fix compile build error resolved
    //let testCaseResult = this.getTestCaseResult();
    if (this.testCaseResult.parentResult)
      this.testCaseResult.isDataDriven = true;
    this.matModal.open(TestCaseResultSummaryComponent, {
      height: '550px',
      width: '60%',
      data: {testcaseResult: this.testCaseResult },
      panelClass: ['mat-dialog', 'rds-none']
    })
  }

  navigate(testCaseResult: TestCaseResult) {
    if(testCaseResult)
    this.router.navigate(['/td/test_case_results', testCaseResult.id]);
  }


  reportBug() {
    this.matModal.open(ReportBugComponent, {
      width: '80%',
      height: '100vh',
      position: {top: '0', right: '0', bottom: '0'},
      data: {testCaseResult: this.testCaseResult},
      panelClass: ['mat-overlay']
    })
  }

  openResultFailedHelp() {
    this.matModal.open(WhyTestCaseFailedHelpComponent, {
      width: '40%',
      data: {testStepResult: this.failTestStepResult},
      panelClass: ['mat-dialog', 'rds-none']
    })
  }

  stopExecution(testPlanResult: TestPlanResult) {
    testPlanResult.result = ResultConstant.STOPPED;
    testPlanResult.status = StatusConstant.STATUS_COMPLETED;
    this.testPlanResultService.update(testPlanResult).subscribe(() => {
      this.translate.get("execution.stopped.success").subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
        this.fetchTestCaseResult();
      })
    })
  }

  startExecution(execution: DryTestPlan) {
    this.startSync = true;
    let dryTestPlan = new DryTestPlan().deserialize(execution.serialize());
    this.executionEnvironmentService.findAll("testPlanId:"+execution.id).subscribe((res)=> {
      dryTestPlan.environments = res.content;
      dryTestPlan.testCaseId = this.testCaseResult.testCaseId;
      delete dryTestPlan.id;
      this.dryTestPlanService.create(dryTestPlan).subscribe((res: TestPlanResult) => {
        this.startSync = false;
        this.translate.get("execution.initiate.success").subscribe((message: string) => {
          this.showNotification(NotificationType.Success, message);
          this.testCaseResultService.findAll("testPlanResultId:"+res.id+",iteration:null", "id,desc").subscribe((res: Page<TestCaseResult>) => {
            this.router.navigate(['/td', 'test_case_results', res?.content[0]?.id]);
          });
        })
      }, error => {
        this.startSync = false;
        this.showAPIError(error, this.translate.instant("execution.initiate.failure"))
      })
    })
  }

  openUpdateStatus() {
    this.matModal.open(TestCaseStatusFormComponent, {
      width: '40%',
      height: '100vh',
      position: {top: '0', right: '0', bottom: '0'},
      data: {testcase: this.testCaseResult.testCase},
      panelClass: ['mat-overlay']
    })
  }

  setEditToggle(isEdit: any) {
    this.isEditStep = isEdit;
  }

  triggerPopup() {
    this.testStepDetailsOpen = true;
    setTimeout(() => {
      this.overlayDir.overlayRef.backdropClick().subscribe(res => {
        this.overlayDir.overlayRef.detach();
        this.testStepDetailsOpen = false;
      });
    }, 200);
  }

  navigateToStepResult(stepResult: TestStepResult) {
    if(this.outlet.isActivated) {
      this.outlet.detach();
      this.outlet.deactivate();
    }
    if(stepResult.isFailed || stepResult.isAborted )
      this.failTestStepResult = stepResult;
    this.router.navigate(['step_results', stepResult.id], {relativeTo: this.route});
  }
  //Used For Dry Run
  postTestCaseFetch() {
    this.fetchTestCase(this.testCaseResult?.testCaseId);
  }

  fetchTestCase(testCaseId) {
    this.testCaseService.show(testCaseId).subscribe(res => {
      this.testCase = res;
      if(res.preRequisite)
        this.fetchPreReqDryTestCaseResult(res.preRequisite);
    });
  }

  fetchPreReqDryTestCaseResult(preReqId){
    this.testCaseResultService.findAll('testCaseId:' + preReqId +
      ',environmentResultId:' + this.testCaseResult.environmentResultId).subscribe(res => {
      this.preReqDryTestCaseResult = res.content[0];
    }, error => console.log(error));
  }

  preReqNavigate() {
    this.router.navigate(['/td/test_case_results', this.preReqDryTestCaseResult.id], {relativeTo: this.route});
  }

  getTestCaseResult(){
    if(this.testCaseResult?.parentResult){
      return this.testCaseResult?.parentResult;
    } else {
      return this.testCaseResult;
    }
  }

  setFirstFailedStep(testStepResult: TestStepResult) {
    if( (testStepResult.isFailed || testStepResult.isAborted ||
        testStepResult.isNotExecuted)
      && !testStepResult.isStepGroup)
    this.failTestStepResult = testStepResult;
  }

  get isDry() {
    return !!this.testCaseResult?.testDeviceResult?.testPlanResult?.dryTestPlan;
  }

}
