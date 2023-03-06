import {Component, EventEmitter, Input, OnInit, Optional, Output} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TestStepService} from "../../services/test-step.service";
import {TestStep} from "../../models/test-step.model";
import {Page} from "../../shared/models/page";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MatDialog} from '@angular/material/dialog';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCase} from "../../models/test-case.model";
import {TestStepPriority} from "../../enums/test-step-priority.enum";
import {TestStepConditionType} from "../../enums/test-step-condition-type.enum";
import {TestStepType} from "../../enums/test-step-type.enum";
import {StepDetailsDataMap} from "../../models/step-details-data-map.model";
import {ResultConstant} from "../../enums/result-constant.enum";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {Pageable} from "../../shared/models/pageable";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {WorkspaceType} from "../../enums/workspace-type.enum";
import {AddonNaturalTextAction} from "../../models/addon-natural-text-action.model";
import {MobileStepRecorderComponent} from "../../agents/components/webcomponents/mobile-step-recorder.component";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {StepsListComponent} from "../cases/steps-list.component";
import {SharedService} from "../../services/shared.service";
import {TestDataService} from "../../services/test-data.service";
import {TestData} from "../../models/test-data.model";
import {ForLoopData} from "../../models/for-loop-data.model";

@Component({
  selector: 'app-test-step-list-item',
  template: `
  `,
  styles: []
})
export abstract class TestStepListItemComponent extends BaseComponent implements OnInit {
  @Input('testStep') testStep: TestStep;
  @Input('isChild') isChild: boolean;
  @Input('stepNumber') stepNumber: number;
  @Input('isDragEnable') isDragEnable?: boolean;
  @Input('version') version?: WorkspaceVersion;
  @Input('testCase') testCase?: TestCase;
  @Input('testSteps') testSteps: Page<TestStep>;
  @Input('isSearchEnabled') isSearchEnabled: any;
  @Input('templates') templates: Page<NaturalTextActions>;
  @Input('addonTemplates') addonTemplates?: Page<AddonNaturalTextAction>;
  @Output('onStepSelection') stepSelectionEvent = new EventEmitter<TestStep>();
  @Output('onStepDeselection') stepDeselectionEvent = new EventEmitter<TestStep>();
  @Output('onStepDestroy') stepDestroyEvent = new EventEmitter<TestStep>();
  @Output('onStepClone') stepCloneEvent = new EventEmitter<TestStep>();
  @Output('onStepBulkDestroy') onStepBulkDestroy = new EventEmitter<TestStep>();
  @Output('onSuccessfulStepSave') onSuccessfulStepSave = new EventEmitter<TestStep>();
  @Output('OnElseStep') elseStepEvent = new EventEmitter<TestStep>();
  @Output('onSelectedStepType') public onSelectedStepType = new EventEmitter<any>();
  public isCloning: boolean;


  get mobileStepRecorder():MobileStepRecorderComponent {
    return this.matModal.openDialogs.find(dialog => dialog.componentInstance instanceof MobileStepRecorderComponent)?.componentInstance;
  }

  protected constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public testStepService: TestStepService,
    public naturalTestActionService: NaturalTextActionsService,
    public matModal: MatDialog,
    public sharedService: SharedService,
    @Optional() public testDataService?: TestDataService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
  }

  deleteStep(testStep: TestStep) {
    this.translate.get("message.common.confirmation.message", {FieldName: 'Test Step'}).subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          if (testStep.isConditionalIf || testStep.isForLoop || testStep.isConditionalElseIf || testStep.isConditionalElse || testStep.isConditionalWhileLoop || testStep.isWhileLoop) {
            this.bulkDestroyStep(testStep);
          } else {
            this.destroyStep(testStep)
          }
        }
      });
    })
  }

  indexTestStepsHavingPrerequisiteSteps(testStep:TestStep) {
    let testSteps:TestStep[];
    this.testStepService.findAll("preRequisiteStepId:"+this.testStep.id).subscribe( res => {
      if(!res.empty){
        this.sharedService.openLinkedTestStepsDialog(this.testSteps.content,res.content);
      } else {
        this.deleteStep(testStep);
      }
      }
    );
    // waitTillRequestResponds();
    // let _this = this;
    //
    // function waitTillRequestResponds() {
    //   if (testSteps.isFetching)
    //     setTimeout(() => waitTillRequestResponds(), 100);
    //   else {
    //     if (testSteps.isEmpty)
    //       _this.deleteStep(testStep);
    //     else
    //       _this.sharedService.openLinkedTestStepsDialog(testSteps);
    //   }
    // }
  }
  // private openLinkedTestStepsDialog(list) {
  //   this.translate.get("step_is_prerequisite_to_another_step").subscribe((res) => {
  //     this.matModal.open(LinkedEntitiesModalComponent, {
  //       width: '568px',
  //       height: 'auto',
  //       data: {
  //         description: res,
  //         linkedEntityList: list,
  //       },
  //       panelClass: ['mat-dialog', 'rds-none']
  //     });
  //   });
  // }

  destroyStep(testStep: TestStep) {
    this.testStepService.destroy(testStep.id).subscribe(() => {
      this.stepDestroyEvent.emit(testStep);
    }, error => {
      this.translate.get('message.common.deleted.failure', {FieldName: 'Test Step'}).subscribe((res) => {
        this.showAPIError(error, res);
      })
    })
  }

  bulkDestroyStep(testStep: TestStep) {
    this.onStepBulkDestroy.emit(testStep);
  }

  expandStepGroup(step: TestStep) {
    step.isStepsExpanded = true;
    //this.activeStepGroupAction.emit(step);
    this.testStepService.findAll("testCaseId:" + step.stepGroupId, 'position').subscribe(steps => {
      steps.content[0].setStepDisplayNumber(steps.content, step.stepDisplayNumber);
      this.assignTemplateForSteps(steps, step)
      step.stepGroupSteps = steps;
      this.assignTestDataForSteps(steps);

    });
  }

  assignTemplateForSteps(testSteps: Page<TestStep>, childStep?:TestStep) {
    testSteps.content.forEach((testStep) => {
      if(testStep.parentStep)
      testStep.parentStep = testSteps.content.find(res => testStep.parentId == res.id);
      if(childStep)
      testStep.childStep = childStep;
    });
  }

  public assignTestDataForSteps(testSteps: Page<TestStep>) {
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
    this.testStepService.findAllForLoopData().subscribe((loopData: Page<ForLoopData>) => {
      let testDataProfileIds = [];
      testSteps.content.forEach((step) => {
        if (step.isForLoop && loopData?.content?.find(res => res.testStepId == step.id)) {
          step.forLoopData = loopData.content.find(res => res.testStepId == step.id)
          if(!testDataProfileIds.includes(step?.forLoopData?.testDataProfileId) && step?.forLoopData?.testDataProfileId) {
            testDataProfileIds.push(step.forLoopData.testDataProfileId);
          }
        }
      })
      this.testDataService.findAll("id@" + testDataProfileIds.join("#")).subscribe((testDataPage: Page<TestData>) => {
        if(!testDataPage.empty) {
          testSteps?.content?.forEach(step => {
            if (step?.forLoopData && testDataPage.content.find(data => data.id == step?.forLoopData?.testDataProfileId)) {
              step.forLoopData.testDataProfileData = testDataPage.content.find(data => data.id == step?.forLoopData?.testDataProfileId)
            }
          })
        }
      }, error => {
      })
    }, error => {
    });

  }

  // postStepFetchProcessing(steps: Page<TestStep>) {
  //
  // }

  toggleStepSelection(step: TestStep) {
    if (step.isSelected)
      this.stepSelectionEvent.emit(step);
    else
      this.stepDeselectionEvent.emit(step);
  }

  clone(testStep: TestStep) {
    this.isCloning = true;
    let testStepNew: TestStep = new TestStep().deserialize(testStep.serialize());
    testStepNew.id = undefined;
    testStepNew.position = this.testSteps.totalElements;
    testStepNew.copiedFrom = testStep.id;
    delete testStepNew.parentId;
    this.testStepService.create(testStepNew).subscribe((step) => {
      step.template = testStep.template;
      step.testData = testStep.testData;
      step.stepGroup = testStep.stepGroup;
      step.stepDisplayNumber = this.testSteps.totalElements+1;//TODO need change method
      step.addonTemplate = testStep.addonTemplate;
      this.stepCloneEvent.emit(step);
      this.isCloning = false;
    }, error => {
      this.translate.get('test_step.clone.failure').subscribe((res) => {
        this.showAPIError(error, res);
        this.isCloning = false;
      })
    })
  }

  get canShowConditionalStepActions() {
    return (this.testStep?.isConditionalIf || this.testStep?.isConditionalElse || this.testStep?.isConditionalElseIf || this.testStep?.isWhileLoop || this.testStep?.isConditionalWhileLoop)
  }

  get isBreakContinueLoopStep() {
    return !(this.testStep.isContinueLoop || this.testStep.isBreakLoop)
  }

  resetAllStepActions() {
    this.testSteps.content.filter(step => {
      step.isEditing = false
      step.isBefore = false;
      step.isAfter = false;
      delete step.siblingStep;
    })
  }

  editStep(testStep: TestStep) {
    if(this.mobileStepRecorder) this.mobileStepRecorder.isElseIfStep = false;
    this.resetAllStepActions();
    testStep.isEditing = true;
    this.onStepChangeAction(testStep.type)
  }


  get isAnyStepEditing(): boolean {
    return !!this.testSteps.content.find(step => step.isEditing);
  }

  onStepSave(testStep: TestStep) {
    this.onSuccessfulStepSave.emit(testStep);
  }

  showElse(step: TestStep): Boolean {
    if (step && step.isConditionalElseIf || step.isConditionalIf) {
      let isShow = true;
      this.testSteps.content.filter(childStep => {
        if (childStep.isConditionalElseIf && childStep.parentId == step.id ||
          childStep.isConditionalElse && childStep.parentId == step.id) {
          isShow = false;
        }
      });
      return isShow;
    }
  }

  showWhileCondition(step: TestStep): Boolean {
    if (step && step.isWhileLoop) {
      let isShow = true;
      this.testSteps?.content?.filter(childStep => {
        if (childStep.isConditionalWhileLoop && childStep.parentId == step.id) {
          isShow = false;
        }
      });
      return isShow;
    }
  }


  addBreakContinueStep(testStep: TestStep, templates:Page<NaturalTextActions>, isAfter, isBreak) {
    let breakContinueStep = this.initNewTestStep((testStep.position + <number>(isAfter ? 1 : -1)), testStep.testCaseId);
    breakContinueStep.type = isBreak ? TestStepType.BREAK_LOOP : TestStepType.CONTINUE_LOOP;
    breakContinueStep.parentId = testStep.isConditionalType ? testStep.id : testStep.parentId;
    breakContinueStep.preRequisiteStepId = null;
    if(testStep.isConditionalType)
      breakContinueStep.stepDisplayNumber = testStep.stepDisplayNumber+".1";
    else if(isAfter) {
      let stepNumberArray = testStep.stepDisplayNumber.split(".");
      let lastNumber = parseInt(stepNumberArray[stepNumberArray.length-1]);
      breakContinueStep.stepDisplayNumber = stepNumberArray.slice(0, stepNumberArray.length-1).join(".")+"."+(lastNumber+1);
    } else {
      let stepNumberArray = testStep.stepDisplayNumber.split(".");
      let lastNumber = parseInt(stepNumberArray[stepNumberArray.length-1]);
      breakContinueStep.stepDisplayNumber = stepNumberArray.slice(0, stepNumberArray.length-1).join(".")+"."+(lastNumber);
    }
    templates.content.forEach((template:NaturalTextActions) => {
      if((isBreak && template.displayName == 'breakLoop') || (!isBreak && template.displayName == 'continueLoop')) {
        breakContinueStep.template = template
        breakContinueStep.naturalTextActionId = template.id
        breakContinueStep.action = template.naturalText
      }
    })
    if(breakContinueStep.template || this.version?.workspace?.isRest) {
      if(this.version?.workspace?.isRest) {
        breakContinueStep.action = isBreak ? 'break loop' : 'continue loop';
      }
      this.testStepService.create(breakContinueStep).subscribe((step) => {
        if(testStep.isConditionalType) {
          step.parentStep = testStep;
        } else {
          step.parentStep = testStep.parentStep;
        }
        step.stepDisplayNumber = breakContinueStep.stepDisplayNumber;//TODO need change method
        this.onSuccessfulStepSave.emit(step);
      })
    } else {
      this.translate.get('test_step.template_not_have').subscribe((res) => {
        this.showNotification(NotificationType.Error, res)
      })
    }
  }

  get canShowBreakContinueStep() {
    return !this.isDragEnable && !this.testStep.isSelected && !this.isChild && !this.isSearchEnabled &&
      this.testStep.getParentLoopId(this.testStep) && !this.testStep.isBreakLoop && !this.testStep.isContinueLoop &&
      this.testStep.parentStep.isConditionalType && !this.testStep.parentStep.isForLoop && !this.testStep.parentStep.isConditionalWhileLoop;
  }

  get canShowBreakContinueStepAfter() {
    return !this.isDragEnable && !this.testStep.isSelected && !this.isChild && !this.isSearchEnabled &&
      this.testStep.getParentLoopId(this.testStep) && !this.testStep.isBreakLoop && !this.testStep.isContinueLoop &&
      this.testStep.parentStep.isConditionalType && ((!this.testStep.parentStep.isForLoop && !this.testStep.parentStep.isConditionalWhileLoop) || this.testStep.isConditionalIf);
  }

  get canShowBeforeAfterStep() {
    return !this.isDragEnable && !this.testStep.isSelected && !this.isChild && !this.isSearchEnabled && !this.testStep.isBreakLoop && !this.testStep.isContinueLoop
  }

  addElseStep(testStep: TestStep) {
    let childSteps: TestStep[] = this.fetchChildren(testStep);
    let lastChild = childSteps[childSteps.length - 1];
    let elseStep = this.initNewTestStep(lastChild.position + 1, testStep.testCaseId);
    elseStep.priority = TestStepPriority.MINOR;
    elseStep.conditionType = TestStepConditionType.CONDITION_ELSE;
    elseStep.parentId = testStep.id;
    this.testStepService.create(elseStep).subscribe((step) => {
      step.parentStep = testStep;
      step.stepDisplayNumber = step.incrementParentStepDisplayNumberLastDigit();
      this.onSuccessfulStepSave.emit(step);
    })
  }

  addElseIfStep(step: TestStep) {
    if(this.mobileStepRecorder) {
      this.mobileStepRecorder.isElseIfStep = true;
      this.mobileStepRecorder.editedStep = step;
    }
    let childSteps: TestStep[] = this.fetchChildren(step);
    childSteps.sort((a, b) => a.position - b.position);
    let lastChild = childSteps[childSteps.length - 1];
    let afterStep = this.initNewTestStep(lastChild.isConditionalType && lastChild != step && !lastChild.parentStep ? lastChild.position - 1 : lastChild.position + 1, step.testCaseId);
    afterStep.conditionType = TestStepConditionType.CONDITION_ELSE_IF;
    afterStep.dataMap.conditionIf = [ResultConstant.SUCCESS];
    afterStep.priority = TestStepPriority.MINOR;
    afterStep.parentId = step.id;
    afterStep.parentStep = step;
    afterStep.stepDisplayNumber = afterStep.incrementParentStepDisplayNumberLastDigit();
    lastChild.isAfter = true;
    lastChild.siblingStep = afterStep;
    afterStep.siblingStep = lastChild;
    this.fetchNextConditionalChild(step)
  }

  fetchNextConditionalChild(parentStep: TestStep) {
    let isFound = false
    this.testSteps.content.forEach(step => {
      if (step.parentId == parentStep.id && (step.isConditionalElse || step.isConditionalElseIf) && !isFound) {
        step.isNeedToUpdate = true;
        isFound = false
      }
    })
  }

  fetchChildren(step: TestStep) {
    let childSteps: TestStep[] = [];
    childSteps.push(step);
    this.iterateChildren(step, childSteps, step);
    return childSteps;
  }

  iterateChildren(parentStep: TestStep, children, staticParent?: TestStep) {
    this.testSteps.content.forEach(step => {
      step.isAfter = false;
      if (step.parentId == parentStep.id && !(staticParent && step.parentId == staticParent.id && (step.isConditionalElse || step.isConditionalElseIf))) {
        children.push(step);
        if (step.isConditionalIf || step.isForLoop || step.isConditionalElse || step.isConditionalElseIf || step.isConditionalWhileLoop || step.isWhileLoop) {
          this.iterateChildren(step, children);
        }
      }
    })
  }

  addBeforeStep(step: TestStep) {
    if (step.isConditionalWhileLoop) {
      step = step.parentStep;
    }
    if(this.mobileStepRecorder) this.mobileStepRecorder.isElseIfStep = false;
    this.resetAllStepActions();
    let testStep = this.initNewTestStep(step.position, step.testCaseId);
    testStep.siblingStep = step;
    step.siblingStep = testStep;
    testStep.parentId = step.parentId;
    testStep.parentStep = step.parentStep;
    step.isBefore = true;
    if(step.isConditionalType) {
      testStep.stepDisplayNumber = step.stepDisplayNumber;
    } else if(step.parentStep?.isConditionalType){
      if(step.stepDisplayNumber?.toString()?.indexOf(".")>-1) {
        let array = step.stepDisplayNumber.split(".");
        let lastDigit = parseInt(array[array.length - 1]);
        array = array.slice(0, array.length - 1);
        testStep.stepDisplayNumber = array.join(".") + "." + (lastDigit -1);
      }
    } else if(step.parentStep) {
      testStep.stepDisplayNumber = testStep.decrementParentStepDisplayNumberLastDigit();
    } else {
      testStep.stepDisplayNumber = step.stepDisplayNumber-1;
    }
  }

  addAfterStep(step: TestStep) {
    if(this.mobileStepRecorder) this.mobileStepRecorder.isElseIfStep = false;
    this.resetAllStepActions();
    let testStep = this.initNewTestStep(step.position + 1, step.testCaseId);
    testStep.parentId = step.parentId;
    testStep.parentStep = step.parentStep;
    if (step.isConditionalType) {
      testStep.parentId = step.id;
      testStep.parentStep = step;
    }
    testStep.siblingStep = step;
    step.siblingStep = testStep;
    step.isAfter = true;
    if(step.isConditionalType) {
      testStep.stepDisplayNumber = step.stepDisplayNumber + ".1";
    } else if(step.parentStep?.isConditionalType){
      if(step.stepDisplayNumber?.toString()?.indexOf(".")>-1) {
        let array = step.stepDisplayNumber.split(".");
        let lastDigit = parseInt(array[array.length - 1]);
        array = array.slice(0, array.length - 1);
        testStep.stepDisplayNumber = array.join(".") + "." + (lastDigit + 1);
      }
    } else if(step.parentStep) {
      testStep.stepDisplayNumber = testStep.incrementParentStepDisplayNumberLastDigit();
    } else {
      testStep.stepDisplayNumber = step.stepDisplayNumber+1;
    }
  }

  addAdjacentStep(step: TestStep) {
    this.resetAllStepActions();
    let lastChildStep = this.findAscendingSiblingStep(step);
    let testStep = this.initNewTestStep((lastChildStep ? lastChildStep.position : step.position) + 1, step.testCaseId);
    testStep.parentId = step.parentId;
    testStep.parentStep = step.parentStep;
    testStep.siblingStep = step;
    if((step.isConditionalElse||step.isConditionalElseIf)){
      testStep.parentId = lastChildStep.parentId;
      testStep.parentStep = lastChildStep.parentStep;
    }
    if(lastChildStep){
      let originalStep= this.testSteps.content.find(testStep => step.id == testStep.id);
      lastChildStep.stepDisplayNumber = originalStep.stepDisplayNumber;
    }
    step.siblingStep = testStep;
    step.isAfter = true;
    testStep.stepDisplayNumber = testStep.incrementParentStepDisplayNumberLastDigit(true, lastChildStep||step);
  }

  initNewTestStep(position: number, testCaseId): TestStep {
    let testStep = new TestStep();
    testStep.position = position;
    testStep.dataMap = new StepDetailsDataMap();
    testStep.conditionIf = [];
    testStep.testCaseId = testCaseId;
    testStep.waitTime = 30;
    testStep.priority = TestStepPriority.MAJOR;
    testStep.type = TestStepType.ACTION_TEXT;
    if (this.version.workspace.isRest)
      testStep.type = TestStepType.REST_STEP;
    return testStep
  }

  onFormCancel(step: TestStep) {
    this.resetAllStepActions();
    this.onStepChangeAction(TestStepType.ACTION_TEXT)
  }

  onStepChangeAction(type) {
    this.onSelectedStepType.emit(type);
  }

  private findAscendingSiblingStep(stepObject: TestStep, skipNullCheck?:boolean): TestStep {
    if(Boolean(skipNullCheck)||stepObject.isConditionalIf || stepObject.isConditionalElseIf || stepObject.isConditionalElse) {
      let step = new TestStep().deserialize(stepObject.serialize());
      let lastConditionalChildStep =
        [...this.testSteps.content].reverse().filter(r => r.isConditionalElse || r.isConditionalElseIf)
          .find(stepsBelow => stepsBelow.parentId == step.id)
      ;
      if (!lastConditionalChildStep) {
        if (!step.isConditionalIf) {
          step.parentStep = this.findParentIf(step).parentStep;
          step.parentId = step.parentStep.id;
        }
        const lastNormalStep = [...this.testSteps.content].reverse().find(stepsBelow => stepsBelow.parentId == step.id);
        step.position = lastNormalStep ? lastNormalStep.position : step.position;
        return step;
      } else {
        return this.findAscendingSiblingStep(lastConditionalChildStep, true);
      }
    } else if(stepObject.isForLoop||stepObject.isWhileLoop){
      const lastNormalStep = [...this.testSteps.content].reverse().find(stepsBelow => stepsBelow.parentId == stepObject.id)
      stepObject.position = lastNormalStep ? lastNormalStep.position : stepObject.position;
    }
  }

  public findParentIf(parentStep: TestStep) {
    return parentStep.isConditionalIf ? parentStep : this.findParentIf(parentStep.parentStep);
  }
}
