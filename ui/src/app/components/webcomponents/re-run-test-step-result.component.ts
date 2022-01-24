import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {TestCaseResult} from "../../models/test-case-result.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestCaseDataDrivenResultService} from "../../services/test-case-data-driven-result.service";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {TestStepResultService} from "../../services/test-step-result.service";
import {Page} from "../../shared/models/page";
import {TestStepResult} from "../../models/test-step-result.model";
import {WorkspaceType} from "../../enums/workspace-type.enum";
import {NaturalTextActionsService} from "../../services/natural-text-actions.service";
import {TestStepService} from "../../services/test-step.service";
import {TestStep} from "../../models/test-step.model";
import {NaturalTextActions} from "../../models/natural-text-actions.model";
import {TestCase} from "../../models/test-case.model";
import {TestCaseService} from "../../services/test-case.service";

@Component({
  selector: 'app-re-run-test-step-result',
  templateUrl: './re-run-test-step-result.component.html',
  styles: [
  ]
})
export class ReRunTestStepResultComponent implements OnInit {
  @Input('testCaseResult') testCaseResult: TestCaseResult;
  @Input('executionResult') executionResult: TestPlanResult;

  public testCaseDataDrivenResults: InfiniteScrollableDataSource;
  public testStepResults: Page<TestStepResult>;
  private testSteps: Page<TestStep>;
  private templates: Page<NaturalTextActions>;
  public activeStepGroup: TestStepResult;

  constructor(
    private testCaseResultService: TestCaseResultService,
    private testCaseDataDrivenResultService: TestCaseDataDrivenResultService,
    private testStepResultService: TestStepResultService,
    private naturalTextActionsService: NaturalTextActionsService,
    private testStepService: TestStepService,
    private testCaseService: TestCaseService) {
  }

  ngOnInit(): void {
    this.fetchSteps()
  }
  fetchSteps(query?: string) {
    query += ",groupResultId:null,testCaseResultId:" +this.testCaseResult.id;
    this.testStepResultService.findAll(query).subscribe(oldResult => {
      this.testStepResults = oldResult;
      this.testStepResultService.findAll(",groupResultId:null,testCaseResultId:" +this.testCaseResult?.childResult?.id).subscribe(res => {
        res.content.forEach(childStep => {
          this.testStepResults.content.forEach(step => {
            if (step.stepId === childStep.stepId && step?.stepDetails?.order_id === childStep?.stepDetails?.order_id)
              step.childResult = childStep
          })
        })
      });
      this.fetchTestSteps(this.testCaseResult.testCase.id);
    });
    query += ",groupResultId:null,testCaseResultId:" +this.testCaseResult?.childResult?.id;
    this.testStepResultService.findAll(query).subscribe(res => {
      this.testStepResults = res;
      this.fetchTestSteps(this.testCaseResult.testCase.id);
    });
  }

  setCaseTemplateDetails() {
    this.fetchNLActions(this.testStepResults);
    this.fetchResultStepGroups();
  }

  setGroupTemplateDetails(stepResults) {

    this.fetchNLActions(stepResults);
  }

  fetchTestSteps(id, isTestGroup?) {
    let query = "testCaseId:" + id;
    this.testStepService.findAll(query, 'position', this.testStepResults.pageable).subscribe(res => {
      this.testSteps = res;
      if (!isTestGroup) {
        this.setCaseTemplateDetails()
      } else if (isTestGroup) {
        this.setGroupTemplateDetails(isTestGroup)
      }
    })
  }
  fetchNLActions(testStepResults: Page<TestStepResult>) {
    let workspaceType: WorkspaceType = this.executionResult.testPlan.workspaceVersion.workspace.workspaceType;
    this.naturalTextActionsService.findAll("workspaceType:" + workspaceType).subscribe(res => {
      this.templates = res;
      testStepResults.content.forEach((testStepResult) => {
        testStepResult.testStep = this.testSteps.content.find(step => step.id == testStepResult.stepId);
        if (testStepResult.stepDetail) {
          testStepResult.template = res.content.find((template) => {
            return template.id == testStepResult.stepDetail.natural_text_action_id;
          });
          if (testStepResult.testStep)
            testStepResult.testStep.template = testStepResult.template;
        }
        testStepResult.parentResult = testStepResults.content.find(stepResults => testStepResult.parentResultId == stepResults.id);
      });
    });
  }
  trackByIdx(i, item) {
    return item.id;
  }

  fetchStepGroupResults(testStepResult: TestStepResult) {
    if (!testStepResult || !testStepResult.isStepGroup) {
      return;
    }
    this.testStepResultService.findAll("groupResultId:" + testStepResult.id).subscribe(stepResults => {
      testStepResult.stepGroupResults = stepResults;
      this.testStepResultService.findAll(",groupResultId:" +testStepResult?.childResult?.id).subscribe(res => {
        res.content.forEach(childStep => {
          testStepResult.stepGroupResults.content.forEach(step => {
            if (step.stepId === childStep.stepId && step?.stepDetails?.order_id === childStep?.stepDetails?.order_id)
              step.childResult = childStep
          })
        })
      });
      this.fetchTestSteps(testStepResult.id, stepResults);
    });
    this.activeStepGroup = testStepResult;
  }
  fetchResultStepGroups() {
    let componentIds = [];
    this.testStepResults.content.forEach((stepResult) => {
      if (stepResult.isStepGroup)
        componentIds.push(stepResult.stepGroupId);
    });
    if (componentIds.length > 0)
      this.testCaseService.findAll("id@" + componentIds.join("#")).subscribe((testCases: Page<TestCase>) => {
        this.testStepResults.content.forEach((stepResult) => {
          if (stepResult.stepGroupId)
            stepResult.stepGroup = testCases.content.find(testCase => testCase.id == stepResult.stepGroupId)
        })
      });
  }
}
