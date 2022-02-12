import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {JsonPipe} from "@angular/common";
import {TestCase} from "../../../models/test-case.model";
import {Page} from "../../../shared/models/page";
import {MobileRecordingComponent} from "./mobile-recording.component";
import {MirroringContainerComponent} from "./mirroring-container.component";
import {TestStep} from "../../../models/test-step.model";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {StepBulkUpdateFormComponent} from "../../../components/webcomponents/step-bulk-update-form.component";
import {TestStepType} from "../../../enums/test-step-type.enum";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MirroringData} from "../../models/mirroring-data.model";
import {DevicesService} from "../../services/devices.service";
import {ElementService} from 'app/shared/services/element.service';
import {TestCaseService} from "../../../services/test-case.service";
import {TestStepService} from "../../../services/test-step.service";
import {ActivatedRoute, Params} from "@angular/router";
import {MobileRecorderEventService} from "../../../services/mobile-recorder-event.service";
import {TestStepMoreActionFormComponent} from "../../../components/webcomponents/test-step-more-action-form.component";
import {StepSummaryComponent} from "../../../components/webcomponents/step-summary.component";
import {WorkspaceVersionService} from "../../../shared/services/workspace-version.service";
import {ActionElementSuggestionComponent} from "../../../components/webcomponents/action-element-suggestion.component";
import {ActionTestDataFunctionSuggestionComponent} from "../../../components/webcomponents/action-test-data-function-suggestion.component";
import {ActionTestDataParameterSuggestionComponent} from "../../../components/webcomponents/action-test-data-parameter-suggestion.component";
import {ActionTestDataEnvironmentSuggestionComponent} from "../../../components/webcomponents/action-test-data-environment-suggestion.component";
import {ElementFormComponent} from "../../../components/webcomponents/element-form.component";
import {RecorderActionTemplateConstant} from "./recorder-action-template-constant";
import {MobileElement} from "../../models/mobile-element.model";
import {Position} from "../../models/position.model";
import {SendKeysRequest} from "../../models/send-keys-request.model";
import {WorkspaceType} from "../../../enums/workspace-type.enum";
import {Element} from "../../../models/element.model";
import {Observable} from "rxjs";
import {ElementLocatorType} from "../../../enums/element-locator-type.enum";
import {ElementCreateType} from "../../../enums/element-create-type.enum";
import {TestStepConditionType} from "../../../enums/test-step-condition-type.enum";
import {StepDetailsDataMap} from "../../../models/step-details-data-map.model";
import {TestDataType} from "../../../enums/test-data-type.enum";
import {TestStepPriority} from "../../../enums/test-step-priority.enum";
import {ConfirmationModalComponent} from "../../../shared/components/webcomponents/confirmation-modal.component";
import {NaturalTextActionsService} from "../../../services/natural-text-actions.service";
import {NaturalTextActions} from "../../../models/natural-text-actions.model";
import {TestCaseActionStepsComponent} from "../../../components/webcomponents/test-case-action-steps.component";

@Component({
    selector: 'app-mobile-step-recorder',
    templateUrl: './mobile-step-recorder.component.html',
    providers: [JsonPipe]
  })
export class MobileStepRecorderComponent extends MobileRecordingComponent implements OnInit {
  public testCase: TestCase;
  public templates: Page<any>;
  public searchTerm: string;
  public version: any;
  public addonAction: Page<any>;
  public selectedTemplate: any;
  public canDrag: boolean = false;
  public isCheckHelpPreference: boolean;
  public inputValue: any;
  @ViewChild('customDialogContainerH50') customDialogContainerH50: ElementRef
  @ViewChild('customDialogContainerH100') customDialogContainerH100: ElementRef
  @ViewChild('mirroringContainerComponent') mirroringContainerComponent: MirroringContainerComponent;
  @ViewChild('stepList') stepList: TestCaseActionStepsComponent;
  // TODO
  // public isRibbonShowed: boolean = true;
  private locatorTypes = {
    accessibility_id: {variableName: "accessibilityId"},
    id_value: {variableName: "id"},
    xpath: {variableName: "xpath"},
    class_name: {variableName: "type"},
    name: {variableName: "name"}
  };
  private selectedStepsList: TestStep[]=[];
  private bulkStepUpdateDialogRef: MatDialogRef<StepBulkUpdateFormComponent>;
  public draggedSteps: TestStep[];
  public currentStepType: string = TestStepType.ACTION_TEXT;
  public pauseRecord: boolean = false;
  private viewAfterLastAction: string = 'NATIVE_APP';
  public actionStep: TestStep;
  public addActionStepAfterSwitch: boolean = false;
  public isElseIfStep: boolean = false;
  private isNeedToUpdateId: number;
  public editedStep: TestStep;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    @Inject(MAT_DIALOG_DATA) public data: MirroringData,
    public JsonPipe: JsonPipe,
    public localDeviceService: DevicesService,
    //public cloudDeviceService: CloudDevicesService,
    public elementService: ElementService,
    public dialogRef: MatDialogRef<MobileRecordingComponent>,
    public dialog: MatDialog,
    public testCaseService: TestCaseService,
    public testStepService: TestStepService,
    public naturalTextActionsService: NaturalTextActionsService,
    public applicationVersionService: WorkspaceVersionService,
    private route: ActivatedRoute,
    private mobileRecorderEventService: MobileRecorderEventService
  ) {
    super(
      authGuard, notificationsService, translate, toastrService,
      data,
      JsonPipe,
      localDeviceService,
      //cloudDeviceService,
      elementService,
      dialogRef,
      dialog)
  }

  get halfHeightDialogsOpen(): boolean {
    return Boolean(
      this?.dialog?.openDialogs?.find(dialog => {
        return dialog.componentInstance instanceof ActionElementSuggestionComponent||
          dialog.componentInstance instanceof ActionTestDataFunctionSuggestionComponent ||
          dialog.componentInstance instanceof ActionTestDataParameterSuggestionComponent ||
          dialog.componentInstance instanceof ActionTestDataEnvironmentSuggestionComponent ||
          dialog.componentInstance instanceof ElementFormComponent ;
      })
    );
  };

  get fullHeightDialogsOpen(): boolean {
    return Boolean(
      this?.dialog?.openDialogs?.find(dialog =>
        dialog.componentInstance instanceof TestStepMoreActionFormComponent ||
        dialog.componentInstance instanceof StepSummaryComponent)
    );
  }

  get canShowBulkActions() {
    return this.selectedStepsList && this.selectedStepsList.length>1;
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.route.params.subscribe((params: Params) => {
      this.fetchTestCase(this.route.snapshot.queryParamMap['params'].testCaseId);
    });
    super.mobileRecorderComponentInstance = this;
  }

  ngOnChanges() {
    if (Boolean(this.data.testCaseId))
      this.fetchTestCase(this.data.testCaseId);
  }

  selectedSteps(steps: TestStep[]) {
    this.selectedStepsList = steps;
  }

  onPositionChange(steps: TestStep[]) {
    this.draggedSteps = steps;
  }

  onStepType(type: string) {
    this.currentStepType = type;
  }

  private saveLaunchStep() {
    let templateId = RecorderActionTemplateConstant.launch_app[this.platform];
    this.saveElementAndCreateStep(templateId);
  }

  public saveTapStep(mobileElement: MobileElement) {
    let templateId = RecorderActionTemplateConstant.tap[this.platform];
    let elementName = mobileElement.text && mobileElement.text.length < 250 ? mobileElement.text : mobileElement.type;
    this.saveElementAndCreateStep(templateId, elementName, undefined, mobileElement);
  }

  public saveDeviceTapStep(tapPoint: Position) {
    tapPoint.y = (Number(tapPoint.y) / this.mirroringContainerComponent.screenOriginalHeight) * 100;
    tapPoint.x = (Number(tapPoint.x) / this.mirroringContainerComponent.screenOriginalWidth) * 100;
    let templateId = RecorderActionTemplateConstant.tapByRelativeCoordinates[this.platform];
    this.saveElementAndCreateStep(templateId, null, tapPoint.x + "," + tapPoint.y);
  }

  public saveNavigateBackStep() {
    let templateId = RecorderActionTemplateConstant.navigateBack[this.platform];
    this.saveElementAndCreateStep(templateId);
  }

  public saveChangeOrientationStep(isLandscape) {
    let templateId = isLandscape ?
      RecorderActionTemplateConstant.setOrientationAsLandscape[this.platform] :
      RecorderActionTemplateConstant.setOrientationAsPortrait[this.platform];
    this.saveElementAndCreateStep(templateId);
  }

  public saveEnterStep(sendKeysRequest: SendKeysRequest) {
    let mobileElement = sendKeysRequest.mobileElement;
    let templateId = RecorderActionTemplateConstant.enter[this.platform];
    let elementName = mobileElement.text && mobileElement.text.length < 250 ? mobileElement.text : mobileElement.type;
    this.saveElementAndCreateStep(templateId, elementName, sendKeysRequest.keys, sendKeysRequest.mobileElement);
  }

  public saveClearStep(mobileElement: MobileElement) {
    let templateId = RecorderActionTemplateConstant.clear[this.platform];
    let elementName = mobileElement.text && mobileElement.text.length < 250 ? mobileElement.text : mobileElement.type;
    this.saveElementAndCreateStep(templateId, elementName, undefined, mobileElement);
  }

  private fetchTestCase(testCaseId: number) {
    this.testCaseService.show(testCaseId).subscribe(testCase => {
      this.testCase = testCase
      this.applicationVersionService.show(this.testCase.workspaceVersionId).subscribe(res => {
        this.version = res;
        this.fetchActionTemplates();
      })
    })
  }

  private fetchActionTemplates() {
    let workspaceType: WorkspaceType = this.version.workspace.workspaceType;
    this.naturalTextActionsService.findAll("workspaceType:" + workspaceType).subscribe(res => {
      this.templates = res
      this.addLaunchStep();
    });
  }

  private saveElementAndCreateStep(templateId: number, elementName?: String, testData?: String,
                                        mobileElement?: MobileElement, fromTestData?: String, toTestData?: String) {
    if(this.canDrag|| this.pauseRecord) return;
    this.checkViewAndAddActionStep(templateId, elementName, testData, mobileElement, fromTestData, toTestData);
  }

  private saveElement(elementName, mobileElement?: MobileElement): Observable<Element[]> {
    let element = new Element();
    element.name = elementName.replace(/[{}().+*?^$%`'/\\|]/g,'_');
    element.locatorType = (mobileElement.accessibilityId) ? ElementLocatorType.accessibility_id :
      (mobileElement.id ? ElementLocatorType.id_value : ElementLocatorType.xpath);
    element.locatorValue = mobileElement[this.locatorTypes[element.locatorType].variableName];
    element.createdType = ElementCreateType.MOBILE_INSPECTOR;
    let elements: Element[] = [element];
    elements.forEach(element => element.workspaceVersionId = this.testCase.workspaceVersionId);
    return this.elementService.bulkCreate(elements)
  }

  private createStepAfterRefresh(currentStep: TestStep) {
    setTimeout(() => {
      if (this.stepList.isStepFetchComplete) {
        if(this.isNeedToUpdateId > 0) {
          this.stepList.testSteps.content[this.isNeedToUpdateId].isNeedToUpdate = true;
          this.isNeedToUpdateId = 0;
        }
        this.selectedIndex = this.fullScreenMode? 0:1;
        this.mobileRecorderEventService.emitStepRecord(currentStep);
        this.stepList.isStepFetchCompletedTmp = false;
      } else {
        this.createStepAfterRefresh(currentStep);
      }
    }, 500);
  }

  private addLaunchStep() {
    setTimeout(() => {
      if (this.stepList.isStepFetchComplete) {
        if(this.stepList?.testSteps?.content?.length == 0 ){
          this.saveLaunchStep();
          this.stepList.isStepFetchCompletedTmp = false;
        }
      } else {
        this.addLaunchStep();
      }
    }, 500);
  }

  public createStep(currentStep: TestStep, isSwitchStep?:boolean) {
    this.dialog.openDialogs?.find( dialog => dialog.componentInstance instanceof TestStepMoreActionFormComponent)?.close();
    currentStep.action = currentStep.template.actionText(currentStep?.element?.toString(), currentStep?.testDataVal?.toString())
    if (this.stepList.isAnyStepEditing) {
      if (this.stepList.editedStep.isConditionalType||this.isElseIfStep) {
        currentStep.parentId = this.stepList.editedStep.id;
        if(this.isElseIfStep){
          currentStep.parentId = this.stepList.editedStep.id;
          currentStep.conditionType = TestStepConditionType.CONDITION_ELSE_IF;
          if(!this.stepList.editedStep.isConditionalType){
            currentStep.parentId = this.findConditionalParent(this.stepList.editedStep).id;
            currentStep.siblingStep = this.stepList.testSteps.content.slice().reverse().find(step => (step.isConditionalElse || step.isConditionalElseIf)  && step.parentId == this.findConditionalParent(this.stepList.editedStep).id);
            if(Boolean(currentStep.siblingStep)){
              currentStep.position = currentStep.siblingStep.position + 1;
              this.isNeedToUpdateId = this.stepList.testSteps.content.indexOf(this.stepList.testSteps.content.find(step => step.id == currentStep.siblingStep.id));
            } else {
              this.isNeedToUpdateId = -1;
            }
          } else {
            currentStep.parentId = this.editedStep.id;
          }
          this.editedStep = null;
        } else {
          currentStep.conditionType = null;
        }
        if(this.stepList.editedStep.isForLoop && this.stepList.editedStep.siblingStep?.isConditionalType){
          currentStep.conditionType = this.stepList.editedStep.siblingStep.conditionType;
          //currentStep.dataMap = new StepDetailsDataMap().deserialize({...{...currentStep.dataMap},...{...this.stepList.editedStep.siblingStep.dataMap}});
        }
        if (this.stepList.editedStep.isConditionalWhileLoop && this.stepList.editedStep.siblingStep?.isConditionalType){
          currentStep.conditionType = this.stepList.editedStep.siblingStep.conditionType;
        }
      } else if (this.stepList.editedStep.isWhileLoop){
        currentStep.parentId = this.stepList.editedStep.id;
        currentStep.conditionType = TestStepConditionType.LOOP_WHILE
      } else
        currentStep.parentId = this.stepList.editedStep.parentId;
      if(this.addActionStepAfterSwitch && !Boolean(isSwitchStep)) {
        this.actionStep = currentStep;
        return;
      }
      this.stepList.fetchSteps();
      this.createStepAfterRefresh(currentStep);
    } else{
      if(this.addActionStepAfterSwitch && !Boolean(isSwitchStep)) {
        this.actionStep = currentStep;
        return;
      }
      this.mobileRecorderEventService.emitStepRecord(currentStep);
      this.selectedIndex = this.fullScreenMode? 0:1;
    }
  }

  updateRecordedElement() {
    if(this.canDrag) return;
    let elementFormComponent:ElementFormComponent= this.dialog.openDialogs?.find(dialog => dialog.componentInstance instanceof ElementFormComponent)?.componentInstance;
    if(!Boolean(elementFormComponent)) return;
    let inspectedElement  = this.mirroringContainerComponent.inspectedElement.mobileElement;
    let locatorType = (inspectedElement.accessibilityId) ?
      ElementLocatorType.accessibility_id : (inspectedElement.id ?
        ElementLocatorType.id_value : ElementLocatorType.xpath);
    let definition = inspectedElement[this.locatorTypes[locatorType].variableName];
    elementFormComponent.element.locatorType = locatorType;
    elementFormComponent.element.locatorValue = definition;
  }

  openBulkUpdate() {
    this.bulkStepUpdateDialogRef = this.dialog.open(StepBulkUpdateFormComponent, {
      width: '450px',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        steps: this.selectedStepsList
      }
    })
    this.bulkStepUpdateDialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.selectedStepsList = [];
        this.testCase = undefined;
        this.fetchTestCase(this.route.snapshot.queryParamMap['params'].testCaseId);
      }
    })
  }

  bulkDeleteConfirm() {
    const dialogRef = this.dialog.open(ConfirmationModalComponent, {
      width: '450px',
      data: {
        description: this.translate.instant("message.common.confirmation.message", {FieldName: 'Test Steps'})
      },
      panelClass: ['matDialog', 'delete-confirm']
    });
    dialogRef.afterClosed().subscribe(result => {if (result) this.bulkDestroy();});
  }

  private bulkDestroy() {
    this.testStepService.bulkDestroy(this.selectedStepsList).subscribe({
      next: () => {
        this.fetchTestCase(this.route.snapshot.queryParamMap['params'].testCaseId);
        this.showNotification(NotificationType.Success, this.translate.instant("message.common.deleted.success", {FieldName: 'Test Steps'}));
        this.selectedStepsList = [];
      },
      error: (error) => {
        if (error.status == "400") {
          this.showNotification(NotificationType.Error, error.error);
        } else {
          this.showNotification(NotificationType.Error, this.translate.instant("message.common.deleted.failure", {FieldName: 'Test Steps'}));
        }
      }
    })
  }

  cancelDragging() {
    this.selectedStepsList =[]
    this.draggedSteps = [];
    this.canDrag = false;
    this.fetchTestCase(this.route.parent.snapshot.params.testCaseId);
  }

  updateSorting() {
    this.testStepService.bulkUpdate(this.draggedSteps).subscribe(() => {
      this.showNotification(NotificationType.Success, this.translate.instant('testcase.details.steps.re-order.success'));
      this.cancelDragging();
    }, error => this.showAPIError(error, this.translate.instant('testcase.details.steps.re-order.failure')) );
  }

  private populateAttributesFromDetails(template: NaturalTextActions): TestStep {
    let currentStep = new TestStep();
    currentStep.template = template;
    currentStep.naturalTextActionId = currentStep.template.id;
    //currentStep.dataMap = new StepDetailsDataMap();
    currentStep.type = TestStepType.ACTION_TEXT;
    let commonData = this.stepList.stepForm
    if(commonData.get('waitTime')){
      currentStep.waitTime = commonData.get('waitTime').value;
      currentStep.priority = commonData.get('priority').value;
      currentStep.preRequisiteStepId = commonData.get('preRequisiteStepId').value;
      currentStep.conditionType = commonData.get('conditionType').value;
      currentStep.disabled = commonData.get('disabled').value;
      currentStep.conditionIf = commonData.get('conditionIf')?.value;
    } else {
      currentStep.waitTime = 30;
      currentStep.priority = TestStepPriority.MAJOR;
      currentStep.disabled = false;
    }
    return currentStep;
  }

  private checkViewAndAddActionStep(templateId: number, elementName?: String, testData?: String,
                                    mobileElement?: MobileElement, fromTestData?: String, toTestData?: String) {
    if(this.viewAfterLastAction != this.getMirroringContainerComponent().viewType){
      const switchViewTemplateId = this.getMirroringContainerComponent().viewType == 'NATIVE_APP'?
        RecorderActionTemplateConstant.switchToNativeAppContext[this.platform] : RecorderActionTemplateConstant.switchToWebviewContext[this.platform];
      this.naturalTextActionsService.findAll("id:" + switchViewTemplateId ).subscribe(templates => {
        this.createSwitchStepAndActionActionStep(this.populateAttributesFromDetails(templates.content[0]), templateId, elementName,
          testData, mobileElement, fromTestData, toTestData);
      });
    } else {
      this.addActionStepAfterSwitch = false;
      this.addActionStep(templateId, elementName, testData, mobileElement, fromTestData, toTestData)
    }
  }

  private addActionStep(templateId: number, elementName?: String, testData?: String, mobileElement?: MobileElement,
                        fromTestData?: String, toTestData?: String){
    this.naturalTextActionsService.findAll("id:" + templateId).subscribe(templates => {
      let currentStep: TestStep = this.populateAttributesFromDetails(templates.content[0]);
      if (Boolean(testData)) {
        currentStep.testDataVal = testData;
        currentStep.testDataType = TestDataType.raw;
      }
      if (Boolean(elementName)) {
        this.saveElement(elementName, mobileElement).subscribe(res => {
          currentStep.element = res[0].name;
          this.createStep(currentStep)
        })
      } else {
        this.createStep(currentStep)
      }
    })
  }

  private createSwitchStepAndActionActionStep(SwitchStep: TestStep, templateId: number, elementName?: String,
                                              testData?: String, mobileElement?: MobileElement,
                                              fromTestData?: String, toTestData?: String) {
    this.viewAfterLastAction = this.getMirroringContainerComponent().viewType;
    this.populateActionStep(templateId, elementName, testData, mobileElement, fromTestData, toTestData);
    this.createStep(SwitchStep, true);
  }

  private populateActionStep(templateId: number, elementName: String, testData: String,
                             mobileElement: MobileElement, fromTestData?: String, toTestData?: String) {
    this.addActionStepAfterSwitch = true;
    this.addActionStep(templateId, elementName, testData, mobileElement, fromTestData, toTestData);
  }

  public findConditionalParent(parentStep: TestStep) {
    return parentStep.isConditionalType ? parentStep : this.findConditionalParent(parentStep.parentStep);
  }

  public findConditionalIfParent(parentStep: TestStep) {
    return parentStep.isConditionalIf ? parentStep : this.findConditionalParent(parentStep.parentStep);
  }
}
