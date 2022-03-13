import {Component, Input, OnInit} from '@angular/core';
import {TestStep} from "../../models/test-step.model";
import {StepSummaryComponent} from "./step-summary.component";
import {TestStepService} from "../../services/test-step.service";
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Page} from "../../shared/models/page";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {TestStepListItemComponent} from "./test-step-list-item.component";
import {TestData} from "../../models/test-data.model";
import {TestDataService} from "../../services/test-data.service";
import {AddonNaturalTextAction} from "../../models/addon-natural-text-action.model";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {FormGroup} from "@angular/forms";
import {MobileStepRecorderComponent} from "../../agents/components/webcomponents/mobile-step-recorder.component";
import {ActionElementSuggestionComponent} from "./action-element-suggestion.component";
import {ActionTestDataFunctionSuggestionComponent} from "./action-test-data-function-suggestion.component";
import {ActionTestDataParameterSuggestionComponent} from "./action-test-data-parameter-suggestion.component";
import {ActionTestDataEnvironmentSuggestionComponent} from "./action-test-data-environment-suggestion.component";
import {TestStepMoreActionFormComponent} from "./test-step-more-action-form.component";
import {ElementFormComponent} from "./element-form.component";


@Component({
  selector: 'app-action-test-step-list-item',
  template: `
    <app-test-step-form-container
      [testCase]="testCase"
      [testSteps]="testSteps"
      [testStep]="testStep.siblingStep"
      [version]="version"
      [templates]="templates"
      [addonTemplates]="addonTemplates"
      (onSuccessfulStepSave)="onStepSave($event)"
      (onStepType)="onStepChangeAction($event)"
      (onCancel)="onFormCancel($event)"
      [stepRecorderView]="stepRecorderView"
      *ngIf="testStep.isBefore && testStep.siblingStep && !isAnyStepEditing"
      class="action-before-container ts-col-100"></app-test-step-form-container>
    <div
      *ngIf="!testStep.isWhileLoop"
      [class.action-enable-edit-index]="testStep.isEditing"
      [class.action-after-index]="testStep.isAfter"
      [class.action-before-index]="testStep.isBefore"
      [class.pl-50]="!isChild"
      class="ts-col-100 action-list-item-container hover">
      <div class="add-before-container">
        <div
          class="action-add-before-placeholder"
          (click)="addBeforeStep(testStep)"
          *ngIf="canShowBeforeAfterStep"
          [translate]="'testcase.details.steps.add_a_step'"></div>
        <div
          class="action-add-before-placeholder"
          style="margin-left: -96px"
          (click)="addBreakContinueStep(testStep, templates, false, true)"
          *ngIf="canShowBreakContinueStep" [translate]="'testcase.details.steps.add_a_break'"></div>
        <div
          class="action-add-before-placeholder"
          style="margin-left: 90px"
          (click)="addBreakContinueStep(testStep, templates, false, false)"
          *ngIf="canShowBreakContinueStep" [translate]="'testcase.details.steps.add_a_continue'"></div>
      </div>
      <a
        [class.destroy-item-bottom]="testStep.removeFromDom"
        [class.add-item-bottom]="testStep.highlight"
        [class.active]="testStep.isSelected"
        [class.disabled-step]="testStep.disabled || testStep.childStep?.disabled"
        [style]="{'cursor': isDragEnable? 'move': 'pointer'}"
        class="list-view green-highlight lg-pm pointer pl-5 ts-col-100 py-16 text-break">
        <i *ngIf="isDragEnable" class="fa-reorder fz-20 drag-enable-icon"></i>
        <div
          [class.ts-col-100]="!isDragEnable"
          [style]="{'padding-left': stepIndent+ 'px'}"
          class="d-flex align-items-center">
          <div
            class="align-items-center d-flex"
            [style]="{'min-width': stepSpacing ? '41px' : '29px'}"
            style="min-height: 22px;">
            <div
              [class.visibility-hidden]="testStep.isSelected"
              [class.step-number-hide]="!isChild && !isDragEnable"
              class="d-flex justify-content-end pt-10"
              style="padding-right: 7px">
              <div
                [class.pill-number]="isChild || testStep?.parentStep"
                class="result-status-8 list-number ng-binding x-sm-size text-white"
                [textContent]="testStep?.stepDisplayNumber || stepNumber"></div>
            </div>
            <div
              *ngIf="!isDragEnable && !isChild"
              [class.action-icons]="!testStep.isSelected"
              class="d-flex px-4 pr-8 step-action-btn">
              <mat-checkbox
                class="mat-checkbox"
                [checked]="testStep.isSelected"
                (change)="testStep.isSelected = !testStep.isSelected; toggleStepSelection(testStep)"></mat-checkbox>
            </div>
          </div>
          <div class="pl-5">
          <span
            *ngIf="testStep.isStepGroup && !testStep.isStepsExpanded"
            class="theme-t-secondary mr-5 text-nowrap fa-plus-square-solid"
            (click)="expandStepGroup(testStep)"
            [matTooltip]="'test_step.step_group.show_steps' | translate">
          </span>
            <span
              *ngIf="testStep.isStepGroup && testStep.isStepsExpanded"
              class="theme-t-secondary mr-5 text-nowrap fa-minus-square-solid"
              (click)="testStep.isStepsExpanded = false"
              [matTooltip]="'test_step.step_group.hide_steps' | translate">
            </span>
            <span
              class="text-uppercase mr-5 text-nowrap"
              *ngIf="canShowConditionalStepActions">
                <i *ngIf="testStep?.isConditionalWhileLoop" class="fa-while-loop mr-5 text-nowrap text-warning"></i>
                <i *ngIf="testStep?.isConditionalIf" class="fa-conditional-if mr-5 text-nowrap text-warning"></i>
                <span class="text-warning"
                      [translate]="testStep?.isConditionalWhileLoop ? '' : 'step.condition_type.'+testStep?.conditionType"></span>
          </span>
            <span
              *ngIf="testStep?.isForLoop">
            <i class="fa-power-loop mr-5 text-nowrap"></i>
            <span class="text-uppercase text-warning" [translate]="'test_step.for_loop.title'"></span>
            &nbsp;
            [
              <span class="text-link">{{testStep?.testData?.name}}</span>
            ]
            &nbsp;[&nbsp;{{testStep?.forLoopStartIndex}}
              ...{{testStep?.forLoopEndIndex == -1 ? ('test_step.for_loop.option_end' | translate) : testStep?.forLoopEndIndex}}&nbsp;]
          </span>
            <span
              [matTooltip]="'test_step.type.REST_STEP' | translate"
              *ngIf="testStep?.isRestStep" class="fa-rest-new text-warning mr-5 text-nowrap"></span>

            <span
              *ngIf="testStep?.isContinueLoop || testStep?.isBreakLoop"
              class="lh-1point4"
              [class.text-warning]="testStep?.isContinueLoop || testStep?.isBreakLoop"
              [class.text-uppercase]="testStep?.isContinueLoop || testStep?.isBreakLoop"
              [innerHTML]="testStep?.action"></span>
            <span
              class="lh-1point4"
              *ngIf="testStep?.template && !(testStep?.isContinueLoop || testStep?.isBreakLoop || testStep?.isAddonAction)"
              [innerHTML]="testStep?.parsedStep"></span>
            <span *ngIf="testStep?.isStepGroup" [textContent]="testStep.stepGroup?.name"></span>
            <span
              *ngIf="testStep?.isRestStep && (!testStep?.isContinueLoop && !testStep?.isBreakLoop && !testStep?.isAddonAction && !testStep.template)"
              [textContent]="testStep?.action"></span>
            <span *ngIf="testStep?.isAddonAction">
              <i class="fa-addon mr-5 text-nowrap"></i><span [innerHTML]="testStep.parsedAddonStep"></span>
            </span>
          </div>
          <div
            class="ml-auto d-flex fixed-right action-icons pl-5"
            *ngIf="testStep && !testStep.isSelected && !isDragEnable && !isChild"
            style="right: 0px;top: auto;bottom: 1px">
            <div class="d-inline-block">
              <a
                *ngIf="testStep.isConditionalIf || testStep.isConditionalElseIf"
                (click)="addElseIfStep(testStep)"
                [textContent]="'+' + ('step.condition_type.CONDITION_ELSE_IF' | translate)"
                class="action-icon py-10">
              </a>
              <a
                *ngIf="showElse(testStep)"
                (click)="addElseStep(testStep)"
                [textContent]="'+' + ('step.condition_type.CONDITION_ELSE' | translate)"
                class="action-icon py-10">
              </a>
              <!--              <a-->
              <!--                *ngIf="showWhileCondition(testStep)"-->
              <!--                (click)="addWhileConditionStep(testStep)"-->
              <!--                [textContent]="'+' + ('step.condition_type.LOOP_WHILE' | translate)"-->
              <!--                class="action-icon py-10">-->
              <!--              </a>-->
              <a class="action-icon fa-external-link-alt-solid" *ngIf="testStep.isStepGroup"
                 target="_blank"
                 [routerLink]="['/td', 'cases', testStep.stepGroupId, 'steps']"
                 [matTooltip]="'test_step.step_group.view_details' | translate"></a>
              <a
                *ngIf="!canShowConditionalStepActions && isBreakContinueLoopStep && !this.testStep?.isForLoop"
                (click)="isCloning ? '' : clone(testStep)"
                [class.not-allowed]="isCloning"
                [matTooltip]="'hint.message.common.clone' | translate"
                class="action-icon py-10 fa-copy-new">
              </a>
              <a
                *ngIf="!testStep.isConditionalElse && !testStep.isWhileLoop && isBreakContinueLoopStep"
                (click)="showDetails(testStep)"
                [matTooltip]="'hint.message.common.more' | translate"
                class="action-icon py-10 more-btn">
              </a>
              <a
                *ngIf="!testStep.isConditionalElse && !testStep.isWhileLoop && isBreakContinueLoopStep"
                (click)="editStep(testStep)"
                [matTooltip]="'hint.message.common.edit' | translate"
                class="action-icon py-10 fa-pencil-on-paper">
              </a>
              <a
                (click)="deleteStep(testStep)"
                href="javascript:void(0);"
                [matTooltip]="'hint.message.common.delete' | translate"
                class="action-icon py-10 fa-trash-thin">
              </a>
            </div>
          </div>
        </div>
      </a>
      <div class="add-after-container">
        <div
          class="action-add-after-placeholder"
          (click)="conditionChildStep? addAdjacentStep(testStep) : addAfterStep(testStep)"
          [style]="{'margin-left': conditionChildStep || showWhileCondition(testStep)?
          ( showWhileCondition(testStep)?'134px':'152px'): '0px'}"
          *ngIf="canShowBeforeAfterStep || (showWhileCondition(testStep)&&testStep.parentId)"
          [translate]="'testcase.details.steps.add_a_step'"></div>
        <div
          class="action-add-after-placeholder"
          (click)="addAfterStep(testStep)"
          *ngIf="canShowBeforeAfterStep && conditionChildStep"
          [translate]="'testcase.details.steps.add_a_step_inside'"></div>
        <div
          class="action-add-after-placeholder"
          style="margin-left: -96px"
          (click)="addBreakContinueStep(testStep, templates, true, true)"
          *ngIf="canShowBreakContinueStepAfter" [translate]="'testcase.details.steps.add_a_break'"></div>
        <div
          class="action-add-after-placeholder"
          style="margin-left: 90px"
          (click)="addBreakContinueStep(testStep, templates, true, false)"
          *ngIf="canShowBreakContinueStepAfter" [translate]="'testcase.details.steps.add_a_continue'"></div>
      </div>
      <div class="w-100"
           *ngIf="testStep.isStepGroup && testStep.isStepsExpanded">
        <span *ngFor='let childStep of testStep?.stepGroupSteps?.content; let childStepNumber = index; '>
          <app-action-test-step-list-item
            *ngIf="!childStep.isWhileLoop"
            [testStep]="childStep"
            [testSteps]="testSteps"
            [stepNumber]="testStep?.stepDisplayNumber+'.'+(childStepNumber+1)"
            [templates]="templates"
            [addonTemplates]="addonTemplates"
            [testCase]="testCase"
            [isChild]=true></app-action-test-step-list-item>
        </span>
      </div>
    </div>

    <app-test-step-form-container
      *ngIf="testStep.isEditing"
      [testCase]="testCase"
      [testSteps]="testSteps"
      [version]="version"
      [testStep]="testStep"
      [templates]="templates"
      [addonTemplates]="addonTemplates"
      (onSuccessfulStepSave)="onStepSave($event)"
      (onStepType)="onStepChangeAction($event)"
      (onCancel)="onFormCancel($event)"
      [stepRecorderView]="stepRecorderView"
      class="action-edit-container ts-col-100"></app-test-step-form-container>
    <app-test-step-form-container
      *ngIf="testStep.isAfter && testStep.siblingStep && !isAnyStepEditing"
      [testCase]="testCase"
      [testSteps]="testSteps"
      [version]="version"
      [testStep]="testStep.siblingStep"
      [templates]="templates"
      [addonTemplates]="addonTemplates"
      (onSuccessfulStepSave)="onStepSave($event)"
      (onStepType)="onStepChangeAction($event)"
      (onCancel)="onFormCancel($event)"
      [stepRecorderView]="stepRecorderView"
      [(stepForm)]="stepForm"
      class="action-after-container ts-col-100"></app-test-step-form-container>
    <div
      *ngIf="testStep.isAfter || testStep.isEditing"
      style="min-height: 300px"></div>
  `,
  styles: []
})
export class ActionTestStepListItemComponent extends TestStepListItemComponent implements OnInit {
  @Input('templates') templates: Page<NaturalTextActions>;
  @Input('addonTemplates') addonTemplates?: Page<AddonNaturalTextAction>;
  @Input() stepRecorderView?: boolean;
  @Input()public stepForm: FormGroup = new FormGroup({});

  get conditionChildStep() {
    return this.testStep.isForLoop && this.testStep.parentId || !this.testStep.isForLoop &&
      this.testStep.isConditionalType && !this.testStep.isConditionalWhileLoop && this.findParentIf(this.testStep)?.parentStep;
  };

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public testStepService: TestStepService,
    public naturalTestActionsService : NaturalTextActionsService,
    public matModal: MatDialog,
    public testDataService: TestDataService
  ) {
    super(authGuard, notificationsService, translate, toastrService, testStepService, naturalTestActionsService, matModal);
  }

  ngOnInit(): void {
  }

  showDetails(testStep: TestStep) {
    if(this.popupsAlreadyOpen(StepSummaryComponent)) return;
    testStep.isSelected = true;
    const dialogRef = this.matModal.open(StepSummaryComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      height: "100%",
      width: '29%',
      position: {top: '0px', right: '0px'},
      data: {
        testStep: testStep,
        version: this.version,
        testCase: this.testCase,
        steps: this.testSteps?.content,
        isStepRecordView: this.stepRecorderView
      },
      panelClass: ['mat-dialog', 'rds-none'],
      ...this.alterStyleIfStepRecorder()
    });
    if(this.stepRecorderView)
      this.resetPositionAndSize(dialogRef, StepSummaryComponent);
    else
      dialogRef.afterClosed().subscribe(() => testStep.isSelected = false);
  }

  postStepFetchProcessing(steps: Page<TestStep>) {
    this.assignTemplateForSteps(steps);
    this.assignTestDataForSteps(steps);
    super.postStepFetchProcessing(steps);
  }

  assignTemplateForSteps(testSteps: Page<TestStep>, childStep?:TestStep) {
    testSteps.content.forEach((testStep) => {
      if (testStep) {
        testStep.template = this.templates.content.find((template) => {
          return template.id == testStep.naturalTextActionId;
        });
        if(this.addonTemplates?.content?.length)
          testStep.addonTemplate = this.addonTemplates.content.find(template => template.id == testStep.addonActionId)
      }
      testStep.parentStep = testSteps.content.find(res => testStep.parentId == res.id);
      if(childStep)
        testStep.childStep = childStep;
    });
  }

  private assignTestDataForSteps(testSteps: Page<TestStep>) {
    let testDataIds = [];
    testSteps.content.forEach(step => {
      if (step.testDataId) {
        testDataIds.push(step.testDataId);
      }
    })
    if (testDataIds.length > 0)
      this.testDataService.findAll("id@" + testDataIds.join("#")).subscribe((testDataPage: Page<TestData>) => {
        testSteps.content.forEach((step) => {
          if (step.testDataId)
            step.testData = testDataPage.content.find(res => res.id == step.testDataId)
        })
      });
  }

  get leftIndentAllStep(){
    return (this.isChild ? !this.testStep?.parentId ?
        this.testStep?.childStep?.leftIndent :
        this.testStep?.childStep?.leftIndent + this.testStep.leftIndent :
      this.testStep.leftIndent) * 20
  }

  private alterStyleIfStepRecorder() {
    if(!this.stepRecorderView) return {};
    let mobileStepRecorderComponent:MobileStepRecorderComponent = this.matModal.openDialogs.find(dialog => dialog.componentInstance instanceof MobileStepRecorderComponent).componentInstance;
    let dialogContainer = mobileStepRecorderComponent.fullScreenMode ?
      mobileStepRecorderComponent.customDialogContainerH50.nativeElement: mobileStepRecorderComponent.customDialogContainerH100.nativeElement;
    let clients = {
      height: dialogContainer.clientHeight +'px',
      width: dialogContainer.clientWidth +'px',
      maxWidth: dialogContainer.clientWidth +'px',
      position: {
        top: dialogContainer.getBoundingClientRect().top + 'px',
        left: dialogContainer.getBoundingClientRect().left + 'px'
      },
      hasBackdrop: false,
      panelClass: ['mat-dialog', 'rds-none', 'modal-shadow-none', mobileStepRecorderComponent.fullScreenMode?'px-10':'']
    }
    return clients;
  }

  private popupsAlreadyOpen(currentPopup) {
    if(!Boolean(this.stepRecorderView)) return false;
    this?.matModal?.openDialogs?.forEach( dialog => {
      if((dialog.componentInstance instanceof ActionElementSuggestionComponent ||
          dialog.componentInstance instanceof ActionTestDataFunctionSuggestionComponent ||
          dialog.componentInstance instanceof ActionTestDataParameterSuggestionComponent ||
          dialog.componentInstance instanceof ActionTestDataEnvironmentSuggestionComponent ||
          dialog.componentInstance instanceof TestStepMoreActionFormComponent ||
          (dialog.componentInstance instanceof ActionElementSuggestionComponent &&
            !(currentPopup instanceof ElementFormComponent)))
        && !(dialog.componentInstance instanceof currentPopup)){
        dialog.close();
      }
    })
    return Boolean(this?.matModal?.openDialogs?.find( dialog => dialog.componentInstance instanceof currentPopup));
  }

  private resetPositionAndSize(matDialog: MatDialogRef<any>, dialogComponent: any) {
    setTimeout(() => {
      if (matDialog._containerInstance._config.height == '0px') {
        let alterStyleIfStepRecorder = this.alterStyleIfStepRecorder();
        matDialog.close();
        matDialog = this.matModal.open(dialogComponent, {
          ...matDialog._containerInstance._config,
          ...alterStyleIfStepRecorder
        });
      }
    }, 200)
  }

  get stepIndent() {
    return ((this.isChild ?  20: 0)+ this.leftIndentAllStep);
  }

  get stepSpacing() {
    return this.isChild || ((this.testStep?.parentStep?.isConditionalType ||
      this.testStep?.parentStep?.isConditionalWhileLoop) && this.testStep?.parentStep?.parentStep)
  }
}
