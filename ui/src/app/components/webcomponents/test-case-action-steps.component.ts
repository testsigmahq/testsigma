import {Component, Input, OnInit} from '@angular/core';
import {TestStepService} from "../../services/test-step.service";
import {Page} from "../../shared/models/page";
import {TestCaseService} from "../../services/test-case.service";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {TestDataService} from "../../services/test-data.service";
import {TestData} from "../../models/test-data.model";
import {TestCaseStepsListComponent} from "./test-case-steps-list.component";
import {TestStep} from "../../models/test-step.model";

import {AddonNaturalTextAction} from "../../models/addon-natural-text-action.model";
import {FormGroup} from "@angular/forms";
import {ChromeRecorderService} from "../../services/chrome-recoder.service";

@Component({
  selector: 'app-test-case-action-steps',
  templateUrl: './test-case-action-steps.component.html',
  styles: []
})
export class TestCaseActionStepsComponent extends TestCaseStepsListComponent implements OnInit {
  @Input('templates') templates: Page<NaturalTextActions>;
  @Input('kibbutzTemplates') kibbutzTemplates?: Page<AddonNaturalTextAction>;
  @Input('selectedTemplate') selectedTemplate: NaturalTextActions;
  @Input('stepRecorderView') stepRecorderView?: boolean; // TODO Check the usage in cloud side
  public navigateTemplate = [1044, 94, 10116, 10001]
  public stepForm: FormGroup = new FormGroup({});

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public testStepService: TestStepService,
    public testCaseService: TestCaseService,
    private testDataService: TestDataService,
    public chromeRecorderService : ChromeRecorderService,
  ) {
    super(testStepService, testCaseService, authGuard, notificationsService, translate, toastrService, chromeRecorderService);
  }

  protected postStepFetchProcessing() {
    this.assignTemplateForSteps();
    this.assignTestDataForSteps();
    super.postStepFetchProcessing();
  }

  private assignTemplateForSteps() {
    if (this.testCase && this.templates) {
      this.testSteps.content.forEach((testStep) => {
        if (testStep) {
          testStep.template = this.templates.content.find((template) => {
            return template.id == testStep.naturalTextActionId;
          });
          if(this.kibbutzTemplates?.content?.length)
          testStep.kibbutzTemplate = this.kibbutzTemplates.content.find(template => template.id == testStep.addonActionId)
        }
        if(this.navigateTemplate.includes(testStep?.template?.id))
          this.testCase.startUrl = testStep.testDataValue;
        testStep.parentStep = this.testSteps.content.find(res => testStep.parentId == res.id);
      });
    }
  }

  private assignTestDataForSteps() {
    let testDataIds = [];
    this.testSteps.content.forEach(step => {
      if (step.testDataId) {
        testDataIds.push(step.testDataId);
      }
    })
    if (testDataIds.length > 0)
      this.testDataService.findAll("id@" + testDataIds.join("#")).subscribe((testDataPage: Page<TestData>) => {
        this.testSteps.content.forEach((step) => {
          if (step.testDataId)
            step.testData = testDataPage.content.find(res => res.id == step.testDataId)
        })
      });
  }

  public setTest(steps: Page<TestStep>) {

  }
}
