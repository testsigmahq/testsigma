/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Component, ElementRef, Input, OnInit, ViewChild, EventEmitter, Output} from '@angular/core';
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestStepResultService} from "../../services/test-step-result.service";
import {Page} from "../../shared/models/page";
import {TestStepResult} from "../../models/test-step-result.model";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {TestStepResultFilterComponent} from "./test-step-result-filter.component";
import {WorkspaceType} from "../../enums/workspace-type.enum";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {ActivatedRoute, Router} from '@angular/router';
import {TestCaseService} from "../../services/test-case.service";
import {TestCase} from "../../models/test-case.model";
import {fromEvent} from 'rxjs';
import {debounceTime, distinctUntilChanged, filter, tap} from 'rxjs/operators';
import {ResultConstant} from "../../enums/result-constant.enum";
import {StepResultScreenshotComparisonService} from "../../services/step-result-screenshot-comparison.service";
import {Pageable} from "../../shared/models/pageable";
import {CdkVirtualScrollViewport} from '@angular/cdk/scrolling';
import {TestStepPriority} from "../../enums/test-step-priority.enum";
import {TestStepService} from "../../services/test-step.service";
import {TestStep} from "../../models/test-step.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestCaseStepsListComponent} from "./test-case-steps-list.component";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {AddonActionService} from "../../services/addon-action.service";
import {AddonNaturalTextAction} from "../../models/addon-natural-text-action.model";
import {TestStepConditionType} from "../../enums/test-step-condition-type.enum";
import {TestStepType} from "../../enums/test-step-type.enum";
import {ChromeRecorderService} from "../../services/chrome-recoder.service";

@Component({
  selector: 'app-test-step-results',
  templateUrl: './test-step-results.component.html',
  styles: []
})
export class TestStepResultsComponent extends TestCaseStepsListComponent implements OnInit {
  @Input('resultEntity') resultEntity: TestCaseResult;
  @ViewChild('searchInput') searchInput: ElementRef;
  @Output('onStepEditAction') onStepEditAction = new EventEmitter<boolean>();
  @Output('onStepDetails') onStepDetails = new EventEmitter<void>();
  @Output('onStepNavigate') onStepNavigate = new EventEmitter<TestStepResult>();
  @Output('onFirstFailedStep') onFirstFailedStep= new EventEmitter<TestStepResult>();

  public testStepResults: Page<TestStepResult>;
  public NotFilteredTestStepResult: Page<TestStepResult>;
  public filteredTestStepResults: Page<TestStepResult>;
  public testSteps: Page<TestStep>;
  public activeStepGroup: TestStepResult;
  public isStepsFetchComplete: boolean = false;
  public templates: Page<NaturalTextActions>;
  public addonTemplates: Page<AddonNaturalTextAction>;
  public workspaceVersion: WorkspaceVersion;
  public isEditEnabled: boolean = false;


  @ViewChild('stepResultFilterBtn') public stepResultFilterBtn: ElementRef;
  public filterDialogRef: MatDialogRef<TestStepResultFilterComponent, any>;
  public isFilterApplied: boolean;
  public filterStepResult: ResultConstant[];
  public filterStepPriority: TestStepPriority[];

  @ViewChild('stepResultsViewPort') public stepResultsViewPort: CdkVirtualScrollViewport;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public router: Router,
    public route: ActivatedRoute,
    public naturalTextActionsService: NaturalTextActionsService,
    public addonActionService: AddonActionService,
    public testStepResultService: TestStepResultService,
    public testCaseService: TestCaseService,
    public stepResultScreenshotComparisionService: StepResultScreenshotComparisonService,
    public matModal: MatDialog,
    public testStepService: TestStepService,
    public versionService: WorkspaceVersionService,
    public chromeRecorderService : ChromeRecorderService,) {
    super(testStepService, testCaseService, authGuard, notificationsService, translate, toastrService, chromeRecorderService);
  }

  ngOnInit() {
  }

  ngOnChanges() {
    this.resultEntity = this.resultEntity.childResult? this.resultEntity.childResult : this.resultEntity;
    if(this.resultEntity) {
      this.fetchSteps();
      this.fetchVersion();
    }
  }

  fetchVersion() {
    this.versionService.show(this.resultEntity?.testCase?.workspaceVersionId).subscribe(res => {
      this.workspaceVersion = res;
    })
  }

  fetchSteps(query?: string) {
    let emptyQuery = query;
    query = query ? query+"," : "";
    query += "groupResultId:null,testCaseResultId:" + this.resultEntity.id;
    this.testStepResultService.findAll(query).subscribe(res => {
      if (query == "") {
        this.NotFilteredTestStepResult = res;
      }
      this.testStepResults = res;
      this.setFailedStepOnRefresh(res);
      res.content[0]?.setResultStepDisplayNumber(res.content);
      this.fetchTestSteps(this.resultEntity.testCase.id);
    });
  }

  setCaseTemplateDetails() {

    this.filteredTestStepResults = Object.assign({}, this.testStepResults);
    this.fetchNLActions(this.testStepResults);
    this.navigateToFirstFailedStep(null);
    this.fetchResultStepGroups();
    if (this.execution.visualTestingEnabled && this.testStepResults.content.length > 0)
      this.fetchVisualResults(this.testStepResults);
    this.scrollActiveToView();
    this.isStepsFetchComplete = true;
  }

  setGroupTemplateDetails(stepResults, groupResult, steps) {

    this.fetchNLActions(stepResults, steps, groupResult);
    if (this.execution.visualTestingEnabled && stepResults.content.length > 0)
      this.fetchVisualResults(stepResults);
  }

  fetchTestSteps(id, isTestGroup?, groupResult?) {
    let query = "testCaseId:" + id;
    this.testStepService.findAll(query, 'position', this.testStepResults.pageable).subscribe(res => {
      if (!isTestGroup) {
        this.testSteps = res;
        this.setCaseTemplateDetails()
      } else if (isTestGroup) {
        this.setGroupTemplateDetails(isTestGroup, groupResult, res)
      }
    })
  }

  navigateToFirstFailedStep(testStepResult) {
    if (this.router.url.indexOf("step_results") < 0) {
      if (this.testStepResults.content.length) {
        let failedStep = null;
        if(!testStepResult) {
           failedStep = this.testStepResults.content.find(step => {
            return (( step.isFailed || step.isAborted || step.isNotExecuted))
          }) || this.testStepResults.content[0];
        } else{
          failedStep = testStepResult.stepGroupResults.content.find(step => {
            return (( step.isFailed || step.isAborted || step.isNotExecuted))
          }) || testStepResult.stepGroupResults.content[0];
        }
        if (failedStep.isStepGroup && !testStepResult) {
          this.fetchStepGroupResults(failedStep);
        }
        this.navigate(failedStep);
      }
    } else {
      let testStepResultId = this.router.url.split("/")[this.router.url.split("/").length - 1];
      if (!this.testStepResults.content.find(step => {
        return step.id == parseInt(testStepResultId)
        || step.stepGroupResults?.content?.find(stepGroupResult => stepGroupResult.id == parseInt(testStepResultId))
      }))
        this.testStepResultService.show(parseInt(testStepResultId)).subscribe(resonse => {
          this.activeStepGroup = this.testStepResults.content.find(res => res.id == resonse.groupResultId);
          this.fetchStepGroupResults(this.activeStepGroup);
        });
    }
    this.addSearchActionStepsEvent();
  }

  navigate(stepResult: TestStepResult) {
    this.onStepNavigate.emit(stepResult);
    this.router.navigate(['/td/test_case_results', this.resultEntity.id, 'step_results', stepResult.id])
  }

  resetFilter() {
    this.isFilterApplied = false;
    this.filterStepResult = undefined;
    this.filterStepPriority = undefined;
    this.fetchSteps("");
  }

  openStepFilter() {

    this.filterDialogRef = this.matModal.open(TestStepResultFilterComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      height: 'calc(100% - 208px)',
      width: '364px',
      data: {filterResult: this.filterStepResult, filterPriority: this.filterStepPriority},
      panelClass: 'mat-overlay'
    });

    const matDialogConfig = new MatDialogConfig();
    const rect: DOMRect = this.stepResultFilterBtn.nativeElement.getBoundingClientRect();
    matDialogConfig.position = {left: `${rect.right + 23}px`, top: `${rect.top - 8}px`}
    this.filterDialogRef.updatePosition(matDialogConfig.position);

    this.filterDialogRef.componentInstance.filterAction.subscribe((applyFilter: Boolean) => {
      let query = "";
      this.isFilterApplied = false;
      this.filterStepResult = undefined;
      this.filterStepPriority = undefined;
      if (applyFilter) {
        this.isFilterApplied = true;
        this.filterStepResult = this.filterDialogRef.componentInstance.filterStepResult;
        this.filterStepPriority = this.filterDialogRef.componentInstance.filterStepPriority;
        if (this.filterStepResult && this.filterStepResult.length) {
          query += "result@" + this.filterStepResult.join("#");
        }
        if (this.filterStepPriority && this.filterStepPriority.length) {
          query += ",priority@" + this.filterStepPriority.join("#");
        }
      }
      this.fetchSteps(query);
    });
  }

  fetchNLActions(testStepResults: Page<TestStepResult>, isGroup?, groupResult?, isWhile?) {
    let workspaceType: WorkspaceType = this.execution?.workspaceVersion?.workspace?.workspaceType;
    let query = isWhile ? ",conditionType:"+TestStepConditionType.LOOP_WHILE : "";
    this.addonActionService.findAll("workspaceType:" + workspaceType).subscribe(res => {
      this.addonTemplates = res;
      if (res?.content?.length)
        this.setAddonTemplate(testStepResults, isGroup, this.addonTemplates)
    })
    this.naturalTextActionsService.findAll("workspaceType:" + workspaceType).subscribe(res => {
      this.templates = res;
      this.setTemplate(testStepResults, isGroup, this.templates)
      if (isGroup) {
        groupResult.stepGroupResults.content = [...testStepResults.content];
        this.activeStepGroup = groupResult;
      }
    });
    this.addonActionService.findAll("workspaceType:" + workspaceType+query).subscribe(res => {
      this.addonTemplates = res;
      if(res?.content?.length)
        this.setAddonTemplate(testStepResults, isGroup, this.addonTemplates)
    })
  }

  setTemplate(testStepResults: Page<TestStepResult>, isGroup, templates) {
    testStepResults.content.forEach((testStepResult) => {
      testStepResult.testStep = isGroup ? isGroup.content.find(step => step.id == testStepResult.stepId) : this.testSteps.content.find(step => step.id == testStepResult.stepId);
      if (testStepResult.stepDetail) {
        testStepResult.template = templates.content.find((template) => {
          return template.id == testStepResult.stepDetail.natural_text_action_id;
        });
        if (testStepResult.testStep) {
          testStepResult.testStep.template = testStepResult.template;
          if (testStepResult.testStep.addonActionId) {
            testStepResult.testStep.addonTemplate = templates.content.find(template => template.id == testStepResult.testStep.addonActionId)
          }
        }
      }
      testStepResult.parentResult = testStepResults.content.find(stepResults => testStepResult.parentResultId == stepResults.id);
    })
  }

  setAddonTemplate(testStepResults: Page<TestStepResult>, isGroup, templates) {
    testStepResults.content.filter(result => result.testStep?.addonActionId).forEach((testStepResult) => {
      if (testStepResult.testStep) {
        testStepResult.testStep.addonTemplate = templates.content.find(template => template.id == testStepResult.testStep.addonActionId)
      }
    })
  }

  fetchResultStepGroups() {
    let stepGroupIds = [];
    this.testStepResults.content.forEach((stepResult) => {
      if (stepResult.isStepGroup)
        stepGroupIds.push(stepResult.stepGroupId);
    });
    if (stepGroupIds.length > 0)
      this.testCaseService.findAll("id@" + stepGroupIds.join("#")).subscribe((testCases: Page<TestCase>) => {
        this.testStepResults.content.forEach((stepResult) => {
          if (stepResult.stepGroupId) {
            stepResult.stepGroup = testCases.content.find(testCase => testCase.id == stepResult.stepGroupId)
          }
        })
        this.isStepsFetchComplete = true;
      });
  }

  fetchStepGroupResults(testStepResult: TestStepResult) {
    if (!testStepResult || !testStepResult.isStepGroup) {
      return;
    }
    this.testStepResultService.findAll("groupResultId:" + testStepResult.id).subscribe(stepResults => {
      this.setFailedStepOnRefresh(stepResults);
      testStepResult.stepGroupResults = stepResults;
      this.fetchTestSteps(testStepResult.stepGroupId, stepResults, testStepResult);
      this.navigateToFirstFailedStep(testStepResult);
      this.scrollActiveToView();
    });
    //this.activeStepGroup = testStepResult;

  }

  setFailedStepOnRefresh(stepResults) {
    if(this.route.children[0]) {
      let failedStep = stepResults.content.find(step => {
        return (step.id == Number(this.route.children[0]?.url['_value'][1].path)) &&
          (step.isFailed || step.isAborted || step.isNotExecuted)
      });
      let firstFailedStep = stepResults.content.find(step => {
        return ( step.isFailed || step.isAborted || step.isNotExecuted);
      });
      failedStep = failedStep ? failedStep : firstFailedStep;
      if (failedStep?.isStepGroup) {
        this.fetchStepGroupResults(failedStep);
      } else if(failedStep)
        this.onFirstFailedStep.emit(failedStep);
    } else {
      setTimeout(()=> this.setFailedStepOnRefresh(stepResults), 100);
    }
  }

  addSearchActionStepsEvent() {
    if (this.searchInput) {
      fromEvent(this.searchInput.nativeElement, 'keyup')
        .pipe(
          filter(Boolean),
          debounceTime(500),
          distinctUntilChanged(),
          tap((event: KeyboardEvent) => {
            let term = this.searchInput.nativeElement.value;
            console.log(term);
            if (term) {
              this.filteredTestStepResults.content = this.testStepResults.content.filter((res) => {

                return ((res.stepDetail &&
                  res.stepDetail.action &&
                  res.stepDetail.action.toUpperCase().indexOf(term.toUpperCase()) > -1) ||
                  (res.stepGroup && res.stepGroup.name &&
                    res.stepGroup.name.toUpperCase().indexOf(term.toUpperCase()) > -1))
              });
            } else {
              this.filteredTestStepResults = Object.assign({}, this.testStepResults);
            }
          })
        )
        .subscribe();
    } else {
      setTimeout(() => {
        this.addSearchActionStepsEvent()
      }, 100);
    }
  }

  scrollActiveToView() {
    setTimeout(() => {
      let urlsSegments = this.router.url.split('step_results/');
      if (urlsSegments.length > 1) {
        let index = this.filteredTestStepResults && this.filteredTestStepResults.content.findIndex(step => step.id.toString() == urlsSegments[urlsSegments.length - 1])
        if (index == -1 && this.activeStepGroup) {
          index = this.filteredTestStepResults && this.filteredTestStepResults.content.findIndex(step => step.id == this.activeStepGroup.id);
          let childIndex = this.activeStepGroup.stepGroupResults &&
            this.activeStepGroup.stepGroupResults.content.findIndex(step => step.id.toString() == urlsSegments[urlsSegments.length - 1]);
          index = childIndex + index;
        }

        this.stepResultsViewPort && this.stepResultsViewPort.scrollToIndex(index - 3, 'smooth');
      }
    }, 100);
  }

  focusOnSearch() {
    this.searchInput.nativeElement.focus();
  }

  fetchVisualResults(testStepResults: Page<TestStepResult>) {
    let query = "testStepResultId@:" + testStepResults.content.map(key => key.id).join("#");
    let pageable = new Pageable();
    pageable.pageSize = testStepResults.content.length;
    this.stepResultScreenshotComparisionService.findAll(query, undefined, pageable).subscribe(res => {
      res.content.forEach(screenshotCom => {
        let stepResult = testStepResults.content.find(stepResult => stepResult.id == screenshotCom.testStepResultId);
        stepResult && (stepResult.stepResultScreenshotComparison = screenshotCom);
      });
    });
  }

  trackByIdx(i, item) {
    return item.id;
  }

  navigateTestcase() {
    //TODO two times loaded[JAYAVEL S]
    this.router.navigate(['../', this.resultEntity.id, {dummyData: (new Date).getTime()}], {relativeTo: this.route});
  }

  setEditAction(obj) {
    let isEdit  = obj?.value;
    let step = obj?.step;
    if(step?.conditionType == TestStepConditionType.LOOP_WHILE) {
      this.fetchNLActions(this.testStepResults, null, null, true);
    } else {
      this.fetchNLActions(this.testStepResults, null, null, false);
    }
    this.isEditEnabled = isEdit;
    this.onStepEditAction.emit(obj);
  }

  triggerDetails() {
    this.onStepDetails.emit()
  }

  onDestroySuccess() {
    this.translate.get("message.common.deleted.success", {FieldName: 'Test Steps'}).subscribe((res: string) => {
      this.showNotification(NotificationType.Success, res);
      this.testStepResults.content.forEach(res => {
        if(res.isDelete)
          delete res.testStep
      })
    });
  }

  setActiveStepGroup() {
    this.activeStepGroup = null;
  }

  setFirstFailedStep(testStepResult: TestStepResult) {
    if( (testStepResult.isFailed || testStepResult.isAborted ||
          testStepResult.isNotExecuted)
        && !testStepResult.isStepGroup) {
      this.onFirstFailedStep.emit(testStepResult);
    }
  }

  get execution() {
    return this.resultEntity?.testDeviceResult?.testPlanResult?.testPlan || this.resultEntity?.testDeviceResult?.testPlanResult?.dryTestPlan;
  }

  get isDryRun(){
    return !!this.resultEntity?.testDeviceResult?.testPlanResult?.dryTestPlan;
  }
}
