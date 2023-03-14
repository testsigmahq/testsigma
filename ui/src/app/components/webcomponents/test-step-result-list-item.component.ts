/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, EventEmitter, Input, OnInit, Output, SimpleChanges} from '@angular/core';
import {TestStepResult} from "../../models/test-step-result.model";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {Router} from '@angular/router';
import {VisualTestingComponent} from "./visual-testing.component";
import {Page} from "../../shared/models/page";
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestStepListItemComponent} from "./test-step-list-item.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestStepService} from "../../services/test-step.service";
import {TestStep} from "../../models/test-step.model";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {AddonNaturalTextAction} from "../../models/addon-natural-text-action.model";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {SharedService} from "../../services/shared.service";
import {TestDataService} from "../../services/test-data.service";


@Component({
  selector: 'app-test-step-result-list-item',
  template: `
    <div
      [class.action-enable-edit-index]="testStepResult.isEditing">
      <a class="list-view green-highlight sm-pm pointer py-16 text-break result-color-highlight"
         [class.br-status-0]="testStepResult.isPassed"
         [class.br-status-1]="testStepResult.isFailed"
         [class.br-status-2]="testStepResult.isAborted"
         [class.br-status-3]="testStepResult.isNotExecuted && !testStepResult?.testStep?.parentStep?.isConditionalType"
         [class.br-status-8]="testStepResult.isNotExecuted && testStepResult?.testStep?.parentStep?.isConditionalType"
         [class.br-status-5]="testStepResult.isQueued"
         [class.br-status-6]="testStepResult.isStopped"
         (click)="fetchStepGroupResults(testStepResult)"
         [class.active]="isStepActive(testStepResult)"
         [class.ml-45]="!isStepGroupChild"
         [class.ml-60]="isStepGroupChild"
         [class.pr-45]="isEditEnabled"
         [class.ignore-result]="testStepResult?.stepDetails?.ignoreStepResult && !testStepResult?.stepDetails?.conditionType"
         [target]="isExpandStepGroup? '_blank' : undefined"
         [routerLink]="isExpandStepGroup? ['/td/cases', this.testStepResult?.stepGroupId, 'steps'] : ['..', testStepResult.testCaseResultId, 'step_results', testStepResult.id]">
        <div
          [style]="{'padding-left': (isStepGroupChild ?  20: 0)+(testStepResult?.leftIndent * 15) + 'px'}"
          class="d-flex flex-wrap ts-col-100 align-items-center">
          <div class="text-break d-flex align-items-center"
               [class.ts-col-70]="testStepResult?.canShowMatchNotMatchLabel"
               [class.ts-col-80]="!testStepResult?.canShowMatchNotMatchLabel">
            <span
              *ngIf="stepNumber||testStepResult?.stepDisplayNumber"
              class="list-number text-white x-sm-size mr-12 mt-0-i result-status-8 pill-number"
              [class.pill-number]="isStepGroupChild || testStepResult?.testStep?.parentStep"
              [textContent]="testStepResult?.stepDisplayNumber||stepNumber">
          </span>
            <span
              *ngIf="testStepResult.isStepGroup"
              class="theme-t-secondary mr-5 text-nowrap"
              [class.fa-plus-square-solid]="testStepResult.id != activeStepGroup?.id"
              [class.fa-minus-square-solid]="testStepResult.id == activeStepGroup?.id">
            </span>

            <span
              class="text-uppercase mr-5 text-nowrap"
              *ngIf="testStepResult?.canShowConditionalStepActions && !testStepResult?.isConditionalWhileLoop">
              <i *ngIf="testStepResult?.isConditionalIf" class="fa-conditional-if mr-5 text-nowrap text-warning"></i>
                <span class="text-warning"
                      [translate]="testStepResult?.testStep?.isWhileLoop ? 'step.while.label' : 'step.condition_type.'+testStepResult?.stepDetail?.conditionType"></span>
          </span>
            <span
              *ngIf="testStepResult.isForLoop">
            <i class="fa-power-loop mr-5 text-nowrap"></i>
            #{{testStepResult.metadata?.forLoop?.index}}
              ::&nbsp;
          </span>
            <span
              [matTooltip]="'test_step.type.REST_STEP' | translate"
              *ngIf="testStepResult.isRestStep" class="fa-rest-new text-warning mr-5 text-nowrap"></span>
            <span
              class="mw-80 lh-1point4"
              [class.text-warning]="testStepResult.testStep &&!testStepResult?.isBreakContinueLoopStep"
              [class.text-uppercase]="testStepResult.testStep &&!testStepResult?.isBreakContinueLoopStep"
              *ngIf="(testStepResult.template || testStepResult.addonTemplate) || (testStepResult.testStep &&!testStepResult?.isBreakContinueLoopStep)">
              <i *ngIf="testStepResult?.isConditionalWhileLoop" class="fa-while-loop mr-5 text-nowrap text-warning"></i>
              <span
                [innerHTML]="testStepResult.testStep && !testStepResult?.isBreakContinueLoopStep ? testStepResult?.stepDetail?.action : testStepResult?.testStep?.addonActionId ? testStepResult.parsedAddonStep(testStepResult?.addonTestData, testStepResult?.stepDetail?.action) : testStepResult.parsedStep"></span>
            </span>
            <span *ngIf="testStepResult.isStepGroup" [textContent]="testStepResult.stepGroup?.name"></span>
            <span
              *ngIf="(testStepResult.isRestStep || !testStepResult.template) && (testStepResult.testStep &&testStepResult?.isBreakContinueLoopStep) && !testStepResult.testStep?.isAddonAction && !(testStepResult?.addonTestData || testStepResult?.addonElements)"
              [innerHTML]="testStepResult?.stepDetail?.action"></span>
            <span
              *ngIf="(testStepResult?.addonElements || testStepResult?.addonTestData) || (testStepResult.testStep?.isAddonAction && testStepResult?.testStep?.addonTemplate) && !testStepResult.template">
              <i class="fa-addon mr-5 text-nowrap"></i><span
              [innerHTML]="testStepResult.parsedAddonStep(testStepResult?.addonTestData, testStepResult?.addonElements, testStepResult?.stepDetail?.action)"></span>
            </span>
          </div>

          <div class="text-wrap d-flex ml-auto">
            <span
              class="pr-10 btn icon-btn rounded-pill mr-4"
              *ngIf="testStepResult?.stepDetails?.ignoreStepResult && testStepResult?.canShowIgnoreStepResultLabel" [translate]="'test_step.details.ignore_step_result_label'" >
            </span>
            <span
              class="pr-7"
              *ngIf="testStepResult?.canShowMatchNotMatchLabel">
              <span
                *ngIf="testStepResult?.isPassed"
                class="stopped border-0 fz-12"
                [translate]="'step_result.is_matched'"></span>
              <span
                *ngIf="testStepResult?.isFailed"
                class="failed border-0 fz-12" [translate]="'step_result.is_not_matched'"></span>
            </span>
            <div class="ml-auto fz-12 text-t-secondary d-flex align-items-center text-nowrap">
              <app-duration-format *ngIf="!unExecutedCondition(testStepResult)"
                                   [duration]="testStepResult.duration"></app-duration-format>
              <span
                class="pl-10">
              <i
                *ngIf="(!(testStepResult?.canShowMatchNotMatchLabel) && !(testStepResult.isPassed || testStepResult.isFailed)) && !testStepResult.isNotExecuted"
                [class.fa-result-0]="testStepResult.isPassed"
                [class.fa-result-1]="testStepResult.isFailed"
                [class.fa-result-2]="testStepResult.isAborted"
                [class.fa-result-3]="testStepResult.isNotExecuted"
                [class.fa-result-5]="testStepResult.isQueued"
                [class.fa-result-6]="testStepResult.isStopped"
                class="pr-6"></i>
              <span
                *ngIf="(!(testStepResult?.canShowMatchNotMatchLabel) && !(testStepResult.isPassed || testStepResult.isFailed)) && !testStepResult.isNotExecuted"
                [translate]="'execution.result.'+testStepResult.result"
                [class.result-status-text-0]="testStepResult.isPassed"
                [class.result-status-text-1]="testStepResult.isFailed"
                [class.result-status-text-2]="testStepResult.isAborted"
                [class.result-status-text-3]="testStepResult.isNotExecuted"
                [class.result-status-text-5]="testStepResult.isQueued"
                [class.result-status-text-6]="testStepResult.isStopped"></span>
              <i
                *ngIf="testStepResult.stepResultScreenshotComparison"
                [matTooltip]="(testStepResult.isVisualFailed ? 'visual_test.hint.differences' : 'visual_test.hint.no_differences') | translate"
                class="fa-camera pl-10"
                (click)="openViComparison(testStepResult)"
                [class.result-status-text-1]="testStepResult.isVisualFailed"
                [class.result-status-text-0]="!testStepResult.isVisualFailed">
              </i>
            </span>
            </div>
          </div>
          <div *ngIf="testStepResult?.testStep" class="ml-auto d-flex fixed-right action-icons pl-5"
               [class.show-label]="testStepResult?.canShowMatchNotMatchLabel"
               style="right: 0px;top: auto;bottom: 1px">
            <div class="d-inline-block">
              <a class="action-icon fa-external-link-alt-solid" *ngIf="testStepResult.isStepGroup"
                 (click)="gotoStepGroup($event)"
                 [matTooltip]="'test_step.step_group.view_details' | translate"></a>
<!--              <a-->
<!--                *ngIf="!isStepGroupChild && !testStepResult?.testStep?.isConditionalElse && !testStepResult?.testStep.isWhileLoop && testStepResult?.isBreakContinueLoopStep"-->
<!--                (click)="editStepByResult(testStepResult)"-->
<!--                [matTooltip]="'hint.message.common.edit' | translate"-->
<!--                class="action-icon py-5 fa-pencil-on-paper">-->
<!--              </a>-->
<!--              <a-->
<!--                *ngIf="!isStepGroupChild"-->
<!--                (click)="deleteStepByResult(testStepResult)"-->
<!--                href="javascript:void(0);"-->
<!--                [matTooltip]="'hint.message.common.delete' | translate"-->
<!--                class="action-icon py-5 fa-trash-thin ml-5">-->
<!--              </a>-->
              <a
                *ngIf="testStepResult.stepResultScreenshotComparison"
                [matTooltip]="(testStepResult.isVisualFailed ? 'visual_test.hint.differences' : 'visual_test.hint.no_differences') | translate"
                class="
                fa-camera action-icon py-5 ml-5"
                (click)="openViComparison(testStepResult)"
                [class.result-status-text-1]="testStepResult.isVisualFailed"
                [class.result-status-text-0]="!testStepResult.isVisualFailed">
              </a>
            </div>
          </div>
        </div>
      </a>
    </div>
    <div
      *ngIf="testStepResult.isEditing"
      class="ts-col-100 d-flex flex-wrap justify-content-between">
      <app-test-step-form-container
        class="d-block mt-n10"
        style="flex: 0 0 calc(100% - 45px);max-width:calc(100% - 45px)"
        (onSuccessfulStepSave)="onSave($event, testStepResult)"
        (onCancel)="onCancel($event, testStepResult)"
        [testCase]="testcaseResult?.testCase"
        [testSteps]="testSteps"
        [version]="workspaceVersion"
        [testStep]="testStepResult?.testStep"
        [addonTemplates]="addonTemplates"
        [testCaseResultId]="testcaseResult?.id"
        [templates]="templates"></app-test-step-form-container>
      <div class="bg-grey-x-light p-10 ml-auto align-items-center d-flex" (mouseenter)="onTriggerDetails()">
        <i class="fa-long-arrow-alt-right-solid"></i>
      </div>
    </div>
    <div *ngIf="testStepResult.id == activeStepGroup?.id && activeStepGroup.stepGroupResults?.content.length == 0"
         [translate]="'test_step.no_steps'" class="empty-full-container ml-40"></div>
    <div
      *ngIf="activeStepGroup?.id == testStepResult.id">
      <span *ngFor='let childStepResult of activeStepGroup.stepGroupResults?.content; let childStepNumber = index;'>
        <app-test-step-result-list-item
          *ngIf="!childStepResult?.isWhileLoop"
          [stepNumber]="stepGroupStepNumber(childStepNumber, testStepResult)"
          [isStepGroupChild]=true
          [testcaseResult]="testcaseResult"
          [isEditEnabled]="isEditEnabled"
          [workspaceVersion]="workspaceVersion"
          [testSteps]="testSteps"
          [templates]="templates"
          [addonTemplates]="addonTemplates"
          [filteredTestStepResult]="filteredTestStepResult"
          [testStepResult]="childStepResult"
          (click)="setIfFailedStep(childStepResult)"></app-test-step-result-list-item>
      </span>
    </div>
  `,
  styles: []
})
export class TestStepResultListItemComponent extends TestStepListItemComponent implements OnInit {
  @Input('testStepResult') testStepResult: TestStepResult;
  @Input('activeStepGroup') activeStepGroup: TestStepResult;
  @Input('stepNumber') stepNumber: any;
  @Input('filteredTestStepResult') filteredTestStepResult: Page<TestStepResult>;
  @Input('isStepGroupChild') isStepGroupChild: Boolean = false;
  @Input('testcaseResult') testcaseResult: TestCaseResult;
  @Input('templates') templates: Page<NaturalTextActions>;
  @Input('addonTemplates') addonTemplates?: Page<AddonNaturalTextAction>;
  @Input('testSteps') testSteps: Page<TestStep>;
  @Input('workspaceVersion') workspaceVersion: WorkspaceVersion;
  @Input('isEditEnabled') isEditEnabled?: boolean = false;
  @Input('isDryRun') isDryRun: boolean;
  @Output('activeStepGroupAction') activeStepGroupAction = new EventEmitter<TestStepResult>();
  @Output('stepUpdateAction') stepUpdateAction = new EventEmitter<String>();
  @Output('onStepEditAction') onStepEditAction = new EventEmitter<any>();
  @Output('onStepDetails') onStepDetails = new EventEmitter<void>();
  @Output('onActiveStepGroup') onActiveStepGroup = new EventEmitter<void>();
  @Output('onFirstFailedStep') onFirstFailedStep= new EventEmitter<TestStepResult>();
  public isExpandStepGroup: boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public naturalTestActionService : NaturalTextActionsService,
    private matDialog: MatDialog,
    public router: Router,
    public sharedService : SharedService,
    public testStepService: TestStepService,
    public testDataService: TestDataService
    ) {
    super(authGuard, notificationsService, translate, toastrService, testStepService,naturalTestActionService, matDialog,sharedService);
  }

  ngOnInit() {
    if(this.testStepResult?.testStep){
      this.testStepResult.testStep['stepGroup'] = this.testStepResult?.stepGroup;
      if(this.addonTemplates?.totalElements){
        this.testStepResult.testStep.addonTemplate =  this.addonTemplates.content.find(template => template.id == this.testStepResult.testStep.addonActionId)
      }
    }
  }

  ngOnChanges() {
    if(this.testStepResult?.testStep){
      this.testStepResult.testStep['stepGroup'] = this.testStepResult?.stepGroup;
      if(this.addonTemplates?.totalElements){
        this.testStepResult.testStep.addonTemplate =  this.addonTemplates.content.find(template => template.id == this.testStepResult.testStep.addonActionId)
      }
    }
  }

  fetchStepGroupResults(testStepResult: TestStepResult) {
    if(this.activeStepGroup?.id == testStepResult.id)
      this.onActiveStepGroup.emit();
    else {
      this.activeStepGroupAction.emit(testStepResult);
    }
    if(this.isExpandStepGroup){
      setTimeout(() => this.isExpandStepGroup= false, 150);
    }
  }

  openViComparison(testStepResult) {
    this.matDialog.open(VisualTestingComponent, {
      width: '100vw',
      height: '100vh',
      position: {top: '0', left: '0', right: '0', bottom: '0'},
      data: {
        screenshotComparisonId: testStepResult?.stepResultScreenshotComparison?.id,
        filteredTestStepResult: this.filteredTestStepResult
      },
      panelClass: ['mat-dialog', 'full-width', 'rds-none']
    })

  }

  //TODO: [Pratheepv] Bad need to check why cdk virtual scroll and [routerActiveLink] not working together
  isStepActive(testStepResult: TestStepResult) {
    return this.router.url.indexOf("/step_results/" + testStepResult.id) > 0;
  }

  deleteStepByResult(testStepResult: TestStepResult) {
    this.filteredTestStepResult.content.filter(stepResult => stepResult.isDelete = false);
    super.deleteStep(testStepResult.testStep);
    testStepResult.isDelete = true;
  }

  editStepByResult(testStepResult: TestStepResult) {
    this.filteredTestStepResult.content.filter(stepResult => stepResult.isEditing = false);
    testStepResult.isEditing = true;
    testStepResult.testStep ? this.onStepEditAction.emit(true):'';
    // this.onStepEditAction.emit({value: true, step: event});
  }

  onSave(event, testStepResult: TestStepResult) {
    testStepResult.isEditing = false;
    super.onStepSave(event)
    this.onStepEditAction.emit(false);
    // this.onStepEditAction.emit({value: false, step: event});
  }

  onCancel(event, testStepResult: TestStepResult) {
    testStepResult.isEditing = false;
    super.onFormCancel(event)
    this.onStepEditAction.emit(false);
    // this.onStepEditAction.emit({value: false, step: event});
  }

  onTriggerDetails() {
    this.onStepDetails.emit()
  }

  gotoStepGroup($event: MouseEvent) {
    this.isExpandStepGroup = true;
    // $event.preventDefault();
    // $event.stopPropagation();
    // $event.stopImmediatePropagation();
  }

  unExecutedCondition(testStepResult: TestStepResult) {
    return (
    (
      testStepResult.parentResult?.stepDetails?.isConditionalIf||
      testStepResult.parentResult?.stepDetails?.isConditionalElseIf||
      testStepResult.parentResult?.stepDetails?.isConditionalElse) && testStepResult.isNotExecuted) || testStepResult.isNotExecuted;
  }

  stepGroupStepNumber(childStepNumber: number, testStepResult: TestStepResult): any {
    return testStepResult.stepDisplayNumber+'.'+(childStepNumber+1);
  }

  setIfFailedStep(childStepResult) {
    childStepResult.isFailed? this.onFirstFailedStep.emit(childStepResult):'';
  }
}
