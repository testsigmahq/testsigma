import {Component, ElementRef, EventEmitter, Inject, Input, NgZone, OnInit, Output, ViewChild} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestStep} from "../../models/test-step.model";
import {NotificationsService} from "angular2-notifications";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestCase} from "../../models/test-case.model";
import {Page} from "../../shared/models/page";
import {FormControl, FormGroup} from "@angular/forms";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {TestStepMoreActionFormComponent} from "./test-step-more-action-form.component";
import {CdkTextareaAutosize} from "@angular/cdk/text-field";
import {take} from "rxjs/operators";
import {TestStepType} from "../../enums/test-step-type.enum";
import {TestStepPriority} from "../../enums/test-step-priority.enum";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {PageScrollService} from 'ngx-page-scroll-core';
import {DOCUMENT} from '@angular/common';
import {TestStepConditionType} from "../../enums/test-step-condition-type.enum";

import {TestStepService} from "../../services/test-step.service";
import {ResultConstant} from "../../enums/result-constant.enum";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";
import {AddonNaturalTextAction} from "../../models/addon-natural-text-action.model";
import {BaseComponent} from "../../shared/components/base.component";
import {StepActionType} from "../../enums/step-action-type.enum";
import {ActionElementSuggestionComponent} from "./action-element-suggestion.component";
import {ActionTestDataFunctionSuggestionComponent} from "./action-test-data-function-suggestion.component";
import {ActionTestDataParameterSuggestionComponent} from "./action-test-data-parameter-suggestion.component";
import {ActionTestDataEnvironmentSuggestionComponent} from "./action-test-data-environment-suggestion.component";
import {ElementFormComponent} from "./element-form.component";
import {MobileStepRecorderComponent} from "../../agents/components/webcomponents/mobile-step-recorder.component";

@Component({
  selector: 'app-test-step-form-container',
  templateUrl: './test-step-form-container.component.html',
  styles: []
})
export class TestStepFormContainerComponent extends BaseComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  @Input('testCase') testCase: TestCase;
  @Input('testSteps') testSteps: Page<TestStep>;
  @Input('testStep') testStep: TestStep;
  @Input('templates') templates?: Page<NaturalTextActions>;
  @Input('addonTemplates') addonTemplates?: Page<AddonNaturalTextAction>;
  @Input('selectedTemplate') selectedTemplate: NaturalTextActions = undefined;
  @Input('testCaseResultId') testCaseResultId: number;
  @Input('isDryRun') isDryRun: boolean;
  @Output('onSuccessfulStepSave') onSuccessfulStepSave = new EventEmitter<TestStep>();
  @Output('onCancel') onCancel = new EventEmitter<TestStep>();
  @Output('onStepType') onStepType = new EventEmitter<string>();


  @ViewChild('autosize') autosize: CdkTextareaAutosize;
  @ViewChild('changeStep', {static: false}) public changeStepRef: ElementRef;
  @ViewChild('naturalStep', {static: false}) public navigateRef: ElementRef;
  @ViewChild('naturalStep', {static: false}) public naturalRef: ElementRef;

  public stepType: string;
  public showForm: Boolean = false;
  public position: number;
  @Input()public stepForm: FormGroup = new FormGroup({});
  @Output() stepFormChange = new EventEmitter<FormGroup>();
  public formSubmitted: boolean = false;
  @Input() stepRecorderView?: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private matDialog: MatDialog,
    private pageScrollService: PageScrollService,
    @Inject(DOCUMENT) private document: any,
    private _ngZone: NgZone,
    private testStepService: TestStepService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get isActionText() {
    return this.stepType == TestStepType.ACTION_TEXT;
  }

  get isRest() {
    return this.stepType == TestStepType.REST_STEP;
  }

  get isStepGroup() {
    return this.stepType == TestStepType.STEP_GROUP;
  }

  get isForLoop() {
    return this.stepType == TestStepType.FOR_LOOP;
  }

  get isWhileParentORCondition() {
    return this.getIsParentLoop(this.testStep, false) || this.testStep?.isConditionalWhileLoop;
  }

  getIsParentLoop(testStep: TestStep, isForLoop){
    if(testStep.parentStep){
      if(isForLoop ? testStep.parentStep?.isForLoop : testStep.parentStep?.isConditionalWhileLoop){
        return true;
      } else{
        return this.getIsParentLoop(testStep.parentStep, isForLoop);
      }
    } else {
      return false;
    }
  }

  get isParentForLoop() {
    return this.getIsParentLoop(this.testStep, true);
  }

  ngOnInit(): void {
    this.position = -1;
    if (!this.testStep && this.testSteps.content) {
      this.testSteps.content.forEach(step => {
        if (step.position >= this.position)
          this.position = step.position
      })
    }
    this.position = this.testStep?.siblingStep ? this.testStep?.position : this.position + 1;
    if (!this.testStep) {
      this.createTestStep();
      this.stepType = TestStepType.ACTION_TEXT;
      if (this.version.workspace.isRest)
        this.stepType = TestStepType.REST_STEP
    } else {
      this.stepType = this.testStep.type;
      this.showForm = true;
    }
    this.stepForm.addControl('action', new FormControl(this.testStep.action, []));
  }

  openFirstChangeTypeWithScroll() {
    this.scrollToDomCenter(this.changeStepRef?.nativeElement, this.openFirstChangeType)
  }

  openFirstChangeType(elm, _this) {
    _this.canShowChangeStep = true;
  }

  scrollToDomCenter(elm, callBack) {
    if (elm) {
      setTimeout(()=> {
        let parentElement = this.getScrollParent(this.changeStepRef.nativeElement);
        this.setScrollPosition(parentElement, callBack, elm)
      }, 500);
    } else
      setTimeout(()=> callBack(elm), 200);
  }

  setScrollPosition(parentElement: HTMLElement, callBack, elem){
    if(parentElement && this.changeStepRef?.nativeElement?.getClientRects()[0]?.top > 360) {
      parentElement.scrollTop =  this.changeStepRef?.nativeElement?.getClientRects()[0]?.top < 500 ?
        parentElement.scrollTop + (this.changeStepRef?.nativeElement?.getClientRects()[0]?.top - 360) : parentElement?.scrollHeight + 500;
      setTimeout(() => {
        this.setScrollPosition(parentElement, callBack, elem)
      }, 250);
    } else {
      callBack(elem, this)
    }
  }

  openFirstNaturalStepWithScroll() {
    this.scrollToDomCenter(this.naturalRef?.nativeElement, this.openFirstNaturalStep)
  }

  openFirstNaturalStep(elm, _this) {
    _this.canShowNaturalStep = true;
  }

  showWhileStep() {
    delete this.testStep.conditionType;
    delete this.testStep.naturalTextActionId;
    delete this.testStep.template;
    delete this.testStep.stepGroupId;
    delete this.testStep.stepGroup;
    delete this.testStep.restStep;
    delete this.testStep.testData;
    delete this.testStep.forLoopTestDataId;
    delete this.testStep.forLoopStartIndex;
    delete this.testStep.forLoopEndIndex;
    this.testStep.type = TestStepType.ACTION_TEXT;
    this.testStep.conditionType = TestStepConditionType.LOOP_WHILE;
    this.testStep.conditionIf = [ResultConstant.SUCCESS];
    this.testStep.priority = TestStepPriority.MINOR;
  }

  changeStepType(stepType: string, isWhile?: boolean) {
    if (this.testStep.isConditionalWhileLoop) {
      delete this.testStep.conditionType;
    }
    if (this.testStep.isConditionalIf) {
      this.testStep.priority = TestStepPriority.MINOR;
    }
    this.stepType = stepType;
    this.showForm = true;
    this.onStepType.emit(stepType);
    if (!this.testStep.id) {
      if (isWhile) {
        this.showWhileStep();
      } else {
        this.createTestStep(this.testStep.stepDisplayNumber);
      }
    }
  }

  createTestStep(stepDisplayNumber?) {
    this.testStep = this.testStep ? this.testStep : new TestStep();
    let commonData = this.stepForm.getRawValue();
    this.testStep.conditionIf = [];
    this.testStep.testCaseId = this.testCase.id;
    this.testStep.position = this.position;
    this.testStep.waitTime = 30;
    this.testStep.priority = TestStepPriority.MAJOR;
    if (commonData['preRequisiteStepId']) {
      this.testStep.preRequisiteStepId = commonData['preRequisiteStepId']
    }
    if(!this.isForLoop && (this.testStep.conditionType=="LOOP_FOR" || commonData['conditionType']=="LOOP_FOR"))
      delete this.testStep.conditionType;
    if (commonData['conditionType'] && !this.isForLoop) {
      this.testStep.conditionType = commonData['conditionType'];
    }
    if (!this.isActionText) {
      delete this.testStep.naturalTextActionId;
      delete this.testStep.template
    }
    if (!this.isStepGroup) {
      delete this.testStep.stepGroupId
      delete this.testStep.stepGroup
    }
    if (this.testStep.isConditionalType) {
      this.testStep.priority = TestStepPriority.MINOR;
      this.testStep.conditionIf = [ResultConstant.SUCCESS];
    }
    if (!this.isForLoop) {
      delete this.testStep.forLoopEndIndex;
      delete this.testStep.forLoopStartIndex;
      delete this.testStep.forLoopTestDataId;
    }
    if(!this.isRest)
      delete this.testStep.restStep;
    this.stepForm.removeControl('waitTime');
    this.stepForm.removeControl('priority');
    this.stepForm.removeControl('preRequisiteStepId');
    this.stepForm.removeControl('conditionType');
    this.testStep.stepDisplayNumber = (stepDisplayNumber == 0 ? stepDisplayNumber + 1 : stepDisplayNumber) || this.testSteps.content.filter(step => step.stepDisplayNumber.toString().indexOf(".") == -1 && !step.isWhileLoop)?.length+1;
  }

  hideForm() {
    this.showForm = false;
    if(!this.testStep?.id) {
      delete this.testStep.conditionType;
      this.stepForm.removeControl('conditionType');
      this.stepType = this.version.workspace.isRest ? TestStepType.REST_STEP : TestStepType.ACTION_TEXT;
      this.changeStepType(this.stepType);
    }
    this.onCancel.emit(this.testStep);
    //this.onStepType.emit(this.stepType)
  }

  afterSaveStep(testStep: TestStep, templates?) {
    this.stepForm.reset();
    this.templates = templates;
    this.showForm = false;
    if (this.testStep.id) {
      this.testStep.isEditing = false;
      testStep.isEditing = true;
    }
    this.selectedTemplate = undefined;
    delete this.selectedTemplate
    testStep.stepDisplayNumber = this.testStep.stepDisplayNumber;
    this.onSuccessfulStepSave.emit(testStep);
    this.updateChildStepsDisabledProperty(testStep);
  }

  showMoreOption(testStep: TestStep) {
    if(this.popupsAlreadyOpen(TestStepMoreActionFormComponent)) return;
    testStep.stepDisplayNumber = this.indexPosition(false);
    if(testStep.preRequisiteStepId) {
      this.testSteps.content.find(testStep => testStep.id == this.testStep.preRequisiteStepId)
        .stepDisplayNumber = this.indexPosition(true);
    }
    let moreOption = this.matDialog.open(TestStepMoreActionFormComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      height: "100%",
      width: '29%',
      position: {top: '0px', right: '0px'},
      data: {
        testStep: testStep,
        form: this.stepForm,
        formSubmitted: true,
        steps: this.testSteps.content,
        parentDisabled: testStep.parentStep?.disabled,
        isStepRecordView: this.stepRecorderView
      },
      panelClass: ['mat-dialog', 'rds-none'],
      ...this.alterStyleIfStepRecorder()
    })
    moreOption.disableClose = true;
    if(this.stepRecorderView) this.resetPositionAndSize(moreOption, TestStepMoreActionFormComponent);
    moreOption.backdropClick().subscribe((event) => {
      let maxWait = this.stepForm?.controls['waitTime']?.value;
      if(maxWait > 120 || maxWait < 1) {
        moreOption.disableClose = true;
      } else {
        moreOption.close();
      }
    });
    if(this.popupsAlreadyOpen(TestStepMoreActionFormComponent)) return;
    testStep.stepDisplayNumber = this.indexPosition(false);
    if(testStep.preRequisiteStepId) {
      this.testSteps.content.find(testStep => testStep.id == this.testStep.preRequisiteStepId)
        .stepDisplayNumber = this.indexPosition(true);
    }
  }

  triggerResize() {
    this._ngZone.onStable.pipe(take(1))
      .subscribe(() => this.autosize.resizeToFitContent(true));
  }

  get testStepType() {
    return TestStepType;
  }

  indexPosition(getPreRequisiteIndex?: boolean): number {
    let testStep;
    if(Boolean(getPreRequisiteIndex)){
      testStep = this.testSteps.content.find(testStep=> testStep.id == this.testStep.preRequisiteStepId);
    } else {
      testStep = this.testStep;
    }
    if(testStep.stepDisplayNumber)
      return testStep.stepDisplayNumber;
    if (testStep?.id) {
        return this.testSteps?.content?.findIndex(step => step.id == testStep.id) + 1;
    } else if (this.testSteps?.content?.length && testStep?.position < this.testSteps?.content?.length + 1) {
        return testStep?.position + 1;
    }
    return this.testSteps?.content?.filter(step => !step.isWhileLoop)?.length + 1;
  }

  get stepConditionType() {
    let value = this.stepForm?.controls['conditionType']?.value == TestStepConditionType.CONDITION_IF ||
    this.stepForm?.controls['conditionType']?.value == TestStepConditionType.CONDITION_ELSE_IF  ||
    this.stepForm?.controls['conditionType']?.value == TestStepConditionType.LOOP_WHILE || this.testStep.isConditionalWhileLoop ? this.stepForm?.controls['conditionType']?.value : false;
    return this.testStep?.isConditionalType ? this.testStep.conditionType : value;
  }

  setFormSubmitted() {
    this.formSubmitted = true;
  }

  get isElseIfType() {
    return this.stepConditionType == TestStepConditionType.CONDITION_ELSE_IF;
  }

  get isIfType() {
    return this.stepConditionType == TestStepConditionType.CONDITION_IF;
  }

  get isConditionalWhileType() {
    return this.stepConditionType == TestStepConditionType.LOOP_WHILE;
  }

  get canShowActionRequired() {
    return (this?.isRest|| !this?.isStepGroup)
      && !this?.testStep?.action?.length && this?.formSubmitted
  }

  createConditionalIFStep(isSetCondition: boolean) {
    if (isSetCondition && this.testStep.type == TestStepType.FOR_LOOP) {
      this.changeStepType(TestStepType.ACTION_TEXT)
    }
    if (isSetCondition) {
      this.testStep.conditionType = TestStepConditionType.CONDITION_IF;
      this.testStep.priority = TestStepPriority.MINOR;
      this.testStep.conditionIf = [ResultConstant.SUCCESS];
    } else {
      this.testStep.priority = TestStepPriority.MAJOR;
      this.testStep.conditionType = undefined;
      this.testStep.conditionIf = undefined;

    }
  }

  addWhileConditionStep(step: TestStep) {
    let testStep = new TestStep();
    testStep.position = step.position + 1;
    testStep.conditionIf = [ResultConstant.SUCCESS];
    testStep.testCaseId = step.testCaseId;
    testStep.waitTime = 30;
    testStep.conditionType = TestStepConditionType.LOOP_WHILE;
    testStep.priority = TestStepPriority.MINOR;
    testStep.parentId = step.id;
    testStep.parentStep = step;
    testStep.siblingStep = step;
    testStep.preRequisiteStepId = null;
    testStep.type = TestStepType.ACTION_TEXT;
    testStep.stepDisplayNumber = step.stepDisplayNumber+".1";
    if (this.version.workspace.isRest)
      testStep.type = TestStepType.REST_STEP;
    step.siblingStep = testStep;
    step.isAfter = true;
    this.templates.content = this.templates.content.filter(template => template.stepActionType == StepActionType.WHILE_LOOP);
  }


  private updateChildStepsDisabledProperty(parentStep: TestStep) {
    this.testSteps.content.forEach(step => {
      if(step.parentId == parentStep.id){
        step.disabled = parentStep.disabled;
        this.updateChildStepsDisabledProperty(step);
      }
    });
  }

  private popupsAlreadyOpen(currentPopup) {
    if(!Boolean(this.stepRecorderView)) return false;
    this?.matDialog?.openDialogs?.forEach( dialog => {
      if((dialog.componentInstance instanceof ActionElementSuggestionComponent ||
          dialog.componentInstance instanceof ActionTestDataFunctionSuggestionComponent ||
          dialog.componentInstance instanceof ActionTestDataParameterSuggestionComponent ||
          dialog.componentInstance instanceof ActionTestDataEnvironmentSuggestionComponent||
          dialog.componentInstance instanceof TestStepMoreActionFormComponent ||
          (dialog.componentInstance instanceof ActionElementSuggestionComponent &&
            !(currentPopup instanceof ElementFormComponent)))
        && !(dialog.componentInstance instanceof currentPopup)){
        dialog.close();
      }
    })
    return Boolean(this?.matDialog?.openDialogs?.find( dialog => dialog.componentInstance instanceof currentPopup));
  }

  alterStyleIfStepRecorder() {
    if(!this.stepRecorderView) return {};
    let mobileStepRecorderComponent:MobileStepRecorderComponent = this.matDialog.openDialogs.find(dialog => dialog.componentInstance instanceof MobileStepRecorderComponent).componentInstance;
    let dialogContainer = mobileStepRecorderComponent.fullScreenMode ?
      mobileStepRecorderComponent.customDialogContainerH50.nativeElement: mobileStepRecorderComponent.customDialogContainerH100.nativeElement;
    let clients = {
      height: dialogContainer.clientHeight +'px',
      width: dialogContainer.clientWidth +'px',
      position: {
        top: dialogContainer.getBoundingClientRect().top + 'px',
        left: dialogContainer.getBoundingClientRect().left + 'px'
      },
      hasBackdrop: false,
      panelClass: ['mat-dialog', 'rds-none', 'modal-shadow-none', mobileStepRecorderComponent.fullScreenMode?'px-10':'']
    }
    return clients;
  }

  private resetPositionAndSize(matDialog: MatDialogRef<any>, dialogComponent: any) {
    setTimeout(() => {
      if (matDialog._containerInstance._config.height == '0px') {
        let alterStyleIfStepRecorder = this.alterStyleIfStepRecorder();
        matDialog.close();
        matDialog = this.matDialog.open(dialogComponent, {
          ...matDialog._containerInstance._config,
          ...alterStyleIfStepRecorder
        });
        matDialog.afterClosed().subscribe(res => {
          this.stepFormChange.emit(this.stepForm);
          this.testStep.conditionType = this.stepForm.get("conditionType").value;
          //this.testStep.dataMap = this.stepForm.get("dataMap").value;
        })
      } else {
        matDialog.afterClosed().subscribe(res => {
          this.stepFormChange.emit(this.stepForm);
          this.testStep.conditionType = this.stepForm.get("conditionType").value;
          //this.testStep.dataMap = this.stepForm.get("dataMap").value;
        })
      }
    }, 200)
  }
}
