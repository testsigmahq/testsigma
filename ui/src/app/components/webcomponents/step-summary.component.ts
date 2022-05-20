import {Component, Inject, Input, OnInit, Optional} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {TestStep} from "../../models/test-step.model";
import {TestStepService} from "../../services/test-step.service";
import {ElementFormComponent} from "./element-form.component";
import {ResultConstant} from "../../enums/result-constant.enum";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCase} from "../../models/test-case.model";
import {Element} from "../../models/element.model";
import {ActionElementSuggestionComponent} from "./action-element-suggestion.component";
import {ActionTestDataFunctionSuggestionComponent} from "./action-test-data-function-suggestion.component";
import {ActionTestDataParameterSuggestionComponent} from "./action-test-data-parameter-suggestion.component";
import {ActionTestDataEnvironmentSuggestionComponent} from "./action-test-data-environment-suggestion.component";
import {TestStepMoreActionFormComponent} from "./test-step-more-action-form.component";
import {MobileStepRecorderComponent} from "../../agents/components/webcomponents/mobile-step-recorder.component";
import {AddonDetailsComponent} from "../../shared/components/webcomponents/addon-details.component";
import {MobileRecorderEventService} from "../../services/mobile-recorder-event.service";

@Component({
  selector: 'app-step-summary',
  templateUrl: './step-summary.component.html',
  styles: []
})
export class StepSummaryComponent implements OnInit {
  @Optional() @Input('stepDetails') stepDetails;
  public testStep: TestStep;
  public conditionIf: ResultConstant[] = [ResultConstant.SUCCESS, ResultConstant.FAILURE];
  public editedElement: String;
  private originalHeight: string;
  private originalWidth: string;

  constructor(
    @Inject(MAT_DIALOG_DATA) public options: {
      testStep: TestStep, version: WorkspaceVersion, testCase: TestCase,
      steps: TestStep[], isStepRecordView?: boolean},
    private testStepService: TestStepService,
    private matDialog: MatDialog,
    private matDialogRef: MatDialogRef<StepSummaryComponent>,
    private mobileRecorderEventService: MobileRecorderEventService
  ) {
    this.testStep = this.options.testStep;
  }

  ngOnInit(): void {
    if (this.stepDetails) {
      this.testStep = this.stepDetails.testStep;
      this.options = this.stepDetails;
    }
    if (this.testStep && this.testStep.preRequisiteStepId) {

      this.testStep.preRequisiteStep = this.options.steps.find(step => step.id == this.testStep.preRequisiteStepId);
      if (!this.testStep.preRequisiteStep)
        this.testStepService.findAll("id:" + this.testStep.preRequisiteStepId).subscribe(resonse => {
          this.testStep.preRequisiteStep = resonse.content[0];
        });
    }
  }

  getAddonTestDataAndElements(map: Map<String, any>) {
    let result = [];
    Object.keys(map).forEach(key => {
      result.push(map[key]);
    });
    return result;
  }

  canShowAddonDetails(map: Map<String, any>) {
    return map && Object.keys(map).length;
  }

  updateAddonElements(oldName, newName) {
    let testStepAddonElements = this.getAddonTestDataAndElements(this.testStep?.addonElements);
    for (let i = 0; i < testStepAddonElements.length; i++) {
      if (testStepAddonElements[i].name == oldName) {
        testStepAddonElements[i].name = newName;
        testStepAddonElements[i].isElementChanged = true;
      }
    }
    this.updateElementsInTestStep(testStepAddonElements);
  }

  updateElementsInTestStep(testStepElements) {
    let elements = this.testStep.addonElements;
    let index = 0;
    Object.keys(elements).forEach(key => {
      elements[key] = testStepElements[index++];
    });
    this.testStep.addonElements = elements;
  }

  openEditElement(name, isAddonElement?: boolean) {
    let sendDetails = {
      name: name,
      versionId: this.options?.version?.id,
      testCaseId: this.options?.testCase?.id,
      isStepRecordView: this.options?.isStepRecordView
    }
    if (this.options.isStepRecordView) {
      this.mobileRecorderEventService.suggestionContent.next(Object.assign(sendDetails, {
        content: this.mobileRecorderEventService.editElementPopup
      }));
      return
    }
    this.editedElement = name;
    const dialogRef = this.matDialog.open(ElementFormComponent, {
      height: "100vh",
      width: '60%',
      position: {top: '0px', right: '0px'},
      data: sendDetails,
      panelClass: ['mat-dialog', 'rds-none'],
    })
    let afterClose= (res) => {
      if(Boolean(this.options.isStepRecordView))
        this.matDialogRef.updateSize(this.originalWidth, this.originalHeight);
      if (res && res instanceof Element) {
        if (isAddonElement)
          this.updateAddonElements(name, res.name);
        else
          this.updateElement(res.name);
      }
    }
    dialogRef.afterClosed().subscribe((res) => afterClose(res));
  }

  updateElement(element) {
    if (this.editedElement == this.testStep.element)
      this.testStep.element = element;
  }

  openAddonDetails(id){
    const dialog = this.matDialog.open(AddonDetailsComponent, {
      height: "100vh",
      width: '30%',
      position: {top: '0', right: '0'},
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        functionId: id
      }
    });
    dialog.afterClosed().subscribe(data => {
    });
  }

  canShowCustomFunctionsParameters(args){
    return args ? Object.keys(args).length : 0;
  }

  closeSuggestion() {
    this.stepDetails ? this.mobileRecorderEventService.setEmptyAction() : this.matDialogRef.close();
  }

}
