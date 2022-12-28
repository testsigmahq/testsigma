import {Component, EventEmitter, Input, OnInit, Optional, Output, SimpleChanges, ViewChild} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestStepService} from "../../services/test-step.service";
import {TestCaseService} from "../../services/test-case.service";
import {TestCase} from "../../models/test-case.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {Page} from "../../shared/models/page";
import {TestStep} from "../../models/test-step.model";
import {Pageable} from "../../shared/models/pageable";
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {TestStepType} from "../../enums/test-step-type.enum";
import {ChromeRecorderService} from "../../services/chrome-recoder.service";
import {CdkVirtualScrollViewport} from "@angular/cdk/scrolling";


@Component({
  template: ``
})
export abstract class TestCaseStepsListComponent extends BaseComponent implements OnInit {
  @Input('testCase') public testCase: TestCase;
  @Input('version') public workspaceVersion: WorkspaceVersion;
  @Input('searchTerm') public searchTerm?: string;
  @Input('isDragEnabled') public isDragEnabled: boolean = false;
  @Output('onStepsFetch') public onStepsFetch = new EventEmitter<number>();
  @Optional() @Output('emitTestSteps') public emitTestSteps = new EventEmitter<TestStep[]>();
  @Output('onStepSelection') public onStepSelection = new EventEmitter<TestStep[]>();
  @Output('onStepDrag') public onStepDrag = new EventEmitter<TestStep[]>();
  @Output('onSelectedStepType') public onSelectedStepType = new EventEmitter<string>();
  @Input('isCheckHelpPreference') isCheckHelpPreference: boolean;
  @ViewChild('stepsVirtualScrollViewNonReorderDisabled') virtualScroll: CdkVirtualScrollViewport;
  public isStepFetchCompletedTmp: Boolean = false;

  public testSteps: Page<TestStep>;
  public isStepFetchComplete: Boolean = false;
  public selectedSteps: TestStep[];
  public hideFormContainer: boolean;
  public refreshedView: boolean = true;

  protected constructor(
    public testStepService: TestStepService,
    public testCaseService: TestCaseService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public chromeRecorderService : ChromeRecorderService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    this.check();
    if(!(Object.keys(changes).length && Object.keys(changes)[0] === "selectedTemplate") &&
      (!(Object.keys(changes).length && Object.keys(changes)[0] === "templates") || changes?.templates?.currentValue?.content?.length == changes?.templates?.previousValue?.length) &&
      (!(Object.keys(changes).length && Object.keys(changes)[0] === "addonTemplates")))
      this.fetchSteps();
    this.selectedSteps=[];
    this.invokeScrollToStep();
  }

  trackByIdx(i, item: TestStep) {
    return item.id;
  }

  onStepDestroy(testStep: TestStep) {
    const index = this.testSteps.content.findIndex(step => step.id == testStep.id);
    if (index > -1) {
      this.testSteps.content.splice(index, 1);
      this.testSteps.content.forEach(step => {
        if(step.position> testStep.position)
          step.position -=1;
      })
    }
    testStep.removeFromDom = true;
    this.testSteps.totalElements-=1
    this.testSteps.numberOfElements-=1;
    this.hideFormContainer = !this.hideFormContainer;
    if(this.testSteps.totalElements== 0) {
      this.refreshedView = !this.refreshedView;
      setTimeout(() => this.refreshedView = !this.refreshedView, 200);
    }
    setTimeout(() => this.testSteps.content = [...this.testSteps.content], 1000);
    setTimeout(() => this.hideFormContainer = !this.hideFormContainer, 200);
    this.testSteps.content[0]?.setStepDisplayNumber(this.testSteps.content)
    this.EmitTestStepCount(this.testSteps);
  }

  onStepBulkDestroy(testStep: TestStep) {
    let selectedSteps = [];
    if (testStep?.isConditionalWhileLoop) {
      let parentWhileStep = new TestStep();
      parentWhileStep.id = <number>testStep.parentId;
      selectedSteps.push(parentWhileStep);
    }
    selectedSteps.push(testStep)
    this.stepSelection(testStep, selectedSteps);
    this.onBulkDestroy(selectedSteps);
  }

  stepSelection(parentStep: TestStep, selectedSteps) {
    if (parentStep.isConditionalElse || parentStep.isConditionalElseIf || parentStep.isConditionalIf || parentStep.isForLoop || parentStep.isWhileLoop || parentStep.isConditionalWhileLoop) {
      this.testSteps.content.forEach(step => {
        if (step.parentId == parentStep.id) {
          selectedSteps.push(step);
          this.stepSelection(step, selectedSteps);
        }
      })
    }
  }

  onBulkDestroy(selectedStepsList) {
    this.testStepService.bulkDestroy(selectedStepsList).subscribe({
      next: () => {
        this.fetchSteps();
        this.translate.get("message.common.deleted.success", {FieldName: 'Test Steps'}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
        });
      },
      error: (error) => {
        if (error.status == "400") {
          this.showNotification(NotificationType.Error, error.error);
        } else {
          this.translate.get("message.common.deleted.failure", {FieldName: 'Test Steps'}).subscribe((res: string) => {
            this.showNotification(NotificationType.Error, res);
          });
        }
      }
    })
  }

  onStepClone(testStep: TestStep) {
    this.testSteps.content.splice(testStep.position, 0, testStep);
    this.testSteps.content.forEach(step => {
      if(step.position>= testStep.position && testStep.id != step.id)
        step.position +=1;
    })
    testStep.highlight = true;
    testStep.isAfter = true
    this.testSteps.totalElements+=1
    this.testSteps.numberOfElements+=1
    this.testSteps.content = [...this.testSteps.content];
    setTimeout(() => {testStep.highlight = false; testStep.isAfter = false}, 1500);
    this.testSteps.content[0].setStepDisplayNumber(this.testSteps.content)
    this.EmitTestStepCount(this.testSteps);
  }

  onSuccessfulStepSave(testStep: TestStep) {
    testStep.highlight = true;
    if(testStep.isEditing) {
      let index = this.testSteps.content.findIndex(step => step.id == testStep.id);
      this.testSteps.content.splice(index, 1, testStep);
    } else if(testStep.siblingStep) {
      if (testStep.isConditionalWhileLoop) {
        this.testSteps.content.splice(testStep.parentStep.position, 0, testStep.parentStep);
        this.testSteps.content.splice(testStep.position, 0, testStep);
      } else {
        this.testSteps.content.splice(testStep.position, 0, testStep);
      }
      this.testSteps.content.forEach(step => {
        if(step.position>= testStep.position && testStep.id != step.id)
          step.position +=1;
      })
      this.testSteps.content.forEach(step => {
        if(step.isNeedToUpdate){
          step.parentId = testStep.id;
          step.isNeedToUpdate = false;
          this.testStepService.update(step).subscribe(()=>{})
        }
      })
      this.testSteps.totalElements+=1
      this.testSteps.numberOfElements+=1;
      //let position = testStep.siblingStep.isBefore ? testStep.position-1 : testStep.position+1
      this.hideFormContainer=true;
      testStep.siblingStep.isBefore = false;
      testStep.siblingStep.isAfter = false;
    } else {
      this.testSteps.totalElements+=1
      this.testSteps.numberOfElements+=1
      this.testSteps.content.splice(testStep.position, 0, testStep);
      this.testSteps.content.forEach(step => {
        if(step.position>= testStep.position && testStep.id != step.id)
          step.position +=1;
      })
      this.hideFormContainer=true;
    }
    this.resetAllStepActions();
    testStep.isEditing = false;
    this.testSteps.content = [...this.testSteps.content];
    setTimeout(() => this.hideFormContainer = false, 200);
    setTimeout(() => testStep.highlight = false, 1500);
    this.testSteps.content[0].setStepDisplayNumber(this.testSteps.content)
    this.EmitTestStepCount(this.testSteps);
    // this.testSteps.content = this.testSteps?.content.filter(step => step.type != TestStepType.WHILE_LOOP)
    this.onStepChangeAction(TestStepType.ACTION_TEXT);
  }

  resetAllStepActions() {
    this.testSteps.content.filter(step => {
      step.isEditing = false
      step.isBefore = false;
      step.isAfter = false;
      delete step.siblingStep;
    })
  }

  selectStep(testStep: TestStep) {
    this.selectedSteps = this.selectedSteps || [];
    if (testStep?.isConditionalWhileLoop) {
      let parentWhileStep = new TestStep();
      parentWhileStep.id = <number>testStep.parentId;
      this.selectedSteps.push(parentWhileStep);
    }
    this.selectedSteps.push(testStep);
    this.selectChildStep(testStep, true);

    this.onStepSelection.emit(this.selectedSteps);
  }

  selectChildStep(parentStep: TestStep, isSelect) {

    if (parentStep.isConditionalElse || parentStep.isConditionalElseIf || parentStep.isConditionalIf || parentStep.isForLoop || parentStep.isWhileLoop || parentStep.isConditionalWhileLoop) {
      this.testSteps.content.forEach(step => {
        if (step.parentId == parentStep.id) {
          if (isSelect) {
            step.isSelected = true;
            this.selectedSteps.push(step);
          } else if (!isSelect) {
            this.selectedSteps = this.selectedSteps.filter((step) => step.parentId != parentStep.id);
            step.isSelected = false;
          }
          this.selectChildStep(step, isSelect);
        }
      })
    }
  }

  deselectStep(testStep: TestStep) {
    if (testStep.isConditionalWhileLoop) {
      this.selectedSteps = this.selectedSteps.filter((step) => step.id != testStep.parentId);
    }
    this.selectedSteps = this.selectedSteps.filter((step) => step.id != testStep.id);
    this.selectChildStep(testStep, false)
    this.onStepSelection.emit(this.selectedSteps);
  }

  drop(event: CdkDragDrop<TestStep[]>) {
    if (event.previousIndex != event.currentIndex) {
      if(this.testSteps.content[event.currentIndex].isConditionalWhileLoop && event.previousIndex > event.currentIndex){
        --event.currentIndex
      }
      moveItemInArray(this.testSteps.content, event.previousIndex, event.currentIndex);
      this.testSteps.content = [...this.testSteps.content];
      let startIndex = event.previousIndex,
        endIndex = event.currentIndex;
      if (event.previousIndex > event.currentIndex) {
        startIndex = event.currentIndex;
        endIndex = event.previousIndex;
      }
      this.testSteps.content.slice(startIndex, endIndex + 1).forEach((step: TestStep, index: number) => {
        step.position = startIndex + index;
        step.isDirty = true;
      });
      let previousStep = this.testSteps.content[event.currentIndex - 1];
      console.log(previousStep, event.currentIndex - 1);
      if (previousStep) {
        if (previousStep.isForLoop || previousStep.isConditionalIf || previousStep.isConditionalElseIf || previousStep.isConditionalElse || previousStep.isConditionalWhileLoop) {
          event.item.data.parentStep = previousStep;
          event.item.data.parentId = previousStep.id;
          if(previousStep.disabled) {
            event.item.data.isAutoDisabled = event.item.data.isAutoDisabled ? event.item.data.isAutoDisabled
              : event.item.data.disabled ? false : true;
            event.item.data.disabled = previousStep.disabled;
          } else if(event.item.data.isAutoDisabled) {
            event.item.data.disabled = false;
          }
        } else if (previousStep.parentStep) {
          event.item.data.parentStep = previousStep.parentStep;
          event.item.data.parentId = previousStep.parentStep.id;
          if(previousStep.parentStep.disabled) {
            event.item.data.isAutoDisabled = event.item.data.isAutoDisabled ? event.item.data.isAutoDisabled
              : event.item.data.disabled ? false : true;
            event.item.data.disabled = previousStep.disabled;
          } else if(event.item.data.isAutoDisabled) {
            event.item.data.disabled = false;
          }
        } else {
          event.item.data.parentStep = undefined;
          event.item.data.parentId = undefined;
          if(event.item.data.isAutoDisabled) {
            event.item.data.disabled = false;
          }
        }
      } else {
        event.item.data.parentStep = undefined;
        event.item.data.parentId = undefined;
        if(event.item.data.isAutoDisabled) {
          event.item.data.disabled = false;
        }
      }
      this.onStepDrag.emit(this.testSteps.content.filter(step => step.isDirty));
    }
  }

  public EmitTestStepCount(testSteps: Page<TestStep>) {
    let tempTestSteps = [...this.testSteps.content];
    tempTestSteps = tempTestSteps.filter(testStep => {
      console.log(testStep)
      console.log(testStep.isConditionalType)
      return !testStep.isConditionalType;
    })
    this.onStepsFetch.emit(tempTestSteps.length);
    this.emitTestSteps.emit(testSteps.content)
  }

  public fetchSteps() {
    let query = "testCaseId:" + this.testCase.id;
    if (this.searchTerm)
      query += ",action:*" + this.searchTerm + "*";
    this.testStepService.findAll(query, 'position').subscribe(res => {
      if(!this.searchTerm){
        this.refreshedView = false;
        setTimeout( () => this.refreshedView = true, 200)
      }
      this.testSteps = res;
      // this.testSteps.content = this.testSteps?.content.filter(step => step.type != TestStepType.WHILE_LOOP)
      this.EmitTestStepCount(this.testSteps);
      if (this.testSteps.content.length > 0) {
        this.testSteps.content = [...res.content];
        this.testSteps.content[0].setStepDisplayNumber(this.testSteps.content)
        this.fetchStepGroups();
      }
      this.postStepFetchProcessing();
    })
  }

  onStepChangeAction(obj) {
    this.onSelectedStepType.emit(obj);
  }

  protected postStepFetchProcessing() {
    this.isStepFetchComplete = true;
    this.isStepFetchCompletedTmp = true;
  }

  private fetchStepGroups() {
    let stepGroupIds = [];
    this.testSteps.content.forEach((step) => {
      if (step.isStepGroup)
        stepGroupIds.push(step.stepGroupId);
    });
    if (stepGroupIds.length > 0) {
      let pageable =  new Pageable();
      pageable.pageSize = stepGroupIds.length;
      this.testCaseService.findAll("id@" + stepGroupIds.join("#"),null,  pageable).subscribe((testCases: Page<TestCase>) => {
        this.testSteps.content.forEach((step) => {
          if (step.stepGroupId)
            step.stepGroup = testCases.content.find(testCase => testCase.id == step.stepGroupId)
        })
      });
    }
  }

  get isAnyStepEditing() : boolean{
    return !!this.testSteps.content.find(step => step.isEditing || step.isBefore || step.isAfter);
  }

  get editedStep(): TestStep{
    return this.testSteps.content.find(step => step.isEditing || step.isBefore || step.isAfter);
  }

  get isAnyStepAfter() : boolean{
    return !!this.testSteps.content.find(step => step.isAfter);
  }

  OnElseStep(step: TestStep) {
    this.testSteps.content.splice(step.position, 0, step);
    step.highlight = true;
    this.testSteps.content = [...this.testSteps.content];
    setTimeout(() => step.highlight = false, 1500);
  }

  private check() {
    if(this.chromeRecorderService.isInstalled){
      this.chromeRecorderService.stepListCallBackContext = this;
    }
    this.chromeRecorderService.stepListCallBack = this.callBack;
  }
  private callBack(steps: Page<TestStep>) {
    this.fetchSteps()
  }

  invokeScrollToStep() {
    let position = window.location.hash.replace('#position:', '');

    if(position) {
      window.location.hash = '';
      this.scrollToStepPosition(position);
    }
  }

  scrollToStepPosition(position) {
    const testSteps = this.testSteps?.content;
    if (this.virtualScroll && testSteps?.length)
      this.virtualScroll.scrollToIndex(position - 5,'smooth');
    else {
      setTimeout(() => {
        this.scrollToStepPosition(position);
      }, 200)
    }
  }

}
