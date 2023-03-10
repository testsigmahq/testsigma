import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TestStepResult} from "../../models/test-step-result.model";
import {TestCaseResult} from "../../models/test-case-result.model";

@Component({
  selector: 'app-re-run-test-step-result-item',
  template: `
    <div class="d-flex my-10 ts-col-100 flex-wrap"
         [class.action-re-run-active-group]="testStepResult.isStepGroup && activeStepGroup?.id == testStepResult.id">
      <a
        (click)="testStepResult.isStepGroup ? fetchStepGroupResults(testStepResult) : ''"
        [class.child-list-item]="isStepGroupChild"
        [class.bordered]="!isStepGroupChild && activeStepGroup?.id != testStepResult.id"
        [class.sm-pm]="!isStepGroupChild"
        [class.pt-15]="activeStepGroup?.id == testStepResult.id"
        class="pointer ts-col-100">
        <div
          [style]="{'padding-left': (testStepResult.leftIndent * 15) + 'px'}"
          class="d-flex flex-wrap ts-col-100">
          <div class="text-break ts-col-55 d-flex align-items-center">
          <span class="pr-5 pb-2 text-nowrap" [textContent]="stepNumber">
          </span>
            <span
              *ngIf="testStepResult.isStepGroup"
              class="theme-t-secondary mr-5 text-nowrap"
              [class.fa-plus-square-solid]="testStepResult.id != activeStepGroup?.id"
              [class.fa-minus-square-solid]="testStepResult.id == activeStepGroup?.id">
            </span>
            <span
              class="text-warning text-uppercase mr-5 text-nowrap"
              *ngIf="testStepResult.isConditionalIf || testStepResult.isConditionalElse || testStepResult.isConditionalElseIf"
              [translate]="'step.condition_type.'+testStepResult?.stepDetail?.conditionType">
          </span>
            <span
              *ngIf="testStepResult.isForLoop">
            <i class="fa-power-loop mr-5 text-nowrap"></i>
            <span [translate]="'step.condition_type.'+testStepResult?.stepDetail?.conditionType"></span>
            #{{testStepResult.metadata.forLoop.index}} :: {{testStepResult.metadata.forLoop.testDataName}}
              - {{testStepResult.metadata.forLoop.iteration}}
          </span>
            <span
              [matTooltip]="'test_step.type.REST_STEP' | translate"
              *ngIf="testStepResult.isRestStep" class="fa-rest-new text-warning mr-5 text-nowrap line-height-none fz-16"></span>
            <span *ngIf="testStepResult.template" [innerHTML]="testStepResult.parsedStep"></span>
            <span *ngIf="testStepResult.isStepGroup" [textContent]="testStepResult.stepGroup?.name"></span>
            <span
              *ngIf="isKubbutzStep()">
              <i class="fa-addon mr-5 text-nowrap"></i><span
              [innerHTML]="testStepResult.parsedAddonStep(testStepResult?.addonTestData, testStepResult?.addonElements, testStepResult?.stepDetail?.action)"></span>
            </span>
          </div>

          <div class="text-wrap ts-col-45 d-flex">
            <div
              *ngIf="testStepResult?.childResult?.id"
              class="ts-col-50 d-flex pr-20 justify-content-center">
              <div class="text-t-secondary fz-12 d-flex align-items-end ts-col-100">
                <div>
                  <div
                    class="text-nowrap pb-4"
                    [translate]="'re_run.re_run_result'"></div>
                  <span
                    class="ml-auto text-nowrap">
                  <i
                    [class.fa-result-0]="testStepResult?.childResult?.isPassed"
                    [class.fa-result-1]="testStepResult?.childResult.isFailed"
                    [class.fa-result-2]="testStepResult?.childResult?.isAborted"
                    [class.fa-result-3]="testStepResult?.childResult?.isNotExecuted"
                    [class.fa-result-5]="testStepResult?.childResult?.isQueued"
                    [class.fa-result-6]="testStepResult?.childResult?.isStopped"
                    class="pr-6"></i>
                  <span
                    [translate]="'execution.result.'+testStepResult?.childResult?.result"
                    [class.result-status-text-0]="testStepResult?.childResult?.isPassed"
                    [class.result-status-text-1]="testStepResult?.childResult?.isFailed"
                    [class.result-status-text-2]="testStepResult?.childResult?.isAborted"
                    [class.result-status-text-3]="testStepResult?.childResult?.isNotExecuted"
                    [class.result-status-text-5]="testStepResult?.childResult?.isQueued"
                    [class.result-status-text-6]="testStepResult?.childResult?.isStopped"></span>
                </span>
                </div>
                <app-duration-format
                  class="text-nowrap ml-auto"
                  [duration]="testStepResult?.childResult?.duration"></app-duration-format>
              </div>
            </div>
            <div
              class="fz-12 text-t-secondary d-flex ts-col-50 border-separator-l-1 pl-20 ml-auto">
              <div class="ts-col-50">
                <div
                  class="text-nowrap pb-4"
                  [translate]="'re_run.previous_result'"></div>
                <span
                  class="text-nowrap">
                <i
                  [class.fa-result-0]="testStepResult.isPassed"
                  [class.fa-result-1]="testStepResult.isFailed"
                  [class.fa-result-2]="testStepResult.isAborted"
                  [class.fa-result-3]="testStepResult.isNotExecuted"
                  [class.fa-result-5]="testStepResult.isQueued"
                  [class.fa-result-6]="testStepResult.isStopped"
                  class="pr-6"></i>
                <span
                  [translate]="'execution.result.'+testStepResult.result"
                  [class.result-status-text-0]="testStepResult.isPassed"
                  [class.result-status-text-1]="testStepResult.isFailed"
                  [class.result-status-text-2]="testStepResult.isAborted"
                  [class.result-status-text-3]="testStepResult.isNotExecuted"
                  [class.result-status-text-5]="testStepResult.isQueued"
                  [class.result-status-text-6]="testStepResult.isStopped"></span>
              </span>
              </div>
              <app-duration-format
                class="ml-auto text-nowrap pb-4 mt-auto"
                [duration]="testStepResult.duration"></app-duration-format>
            </div>
          </div>
        </div>
      </a>
      <div
        class="ts-col-100"
        *ngIf="activeStepGroup?.id == testStepResult.id">
        <app-re-run-test-step-result-item
          *ngFor='let stepGroupResult of activeStepGroup.stepGroupResults?.content; let childStepNumber = index; '
          [stepNumber]="stepNumber+'.'+(childStepNumber+1)"
          [isStepGroupChild]=true
          [testcaseResult]="testcaseResult"
          [testStepResults]="stepGroupResult"></app-re-run-test-step-result-item>
      </div>
    </div>
  `,
  styles: [],
  host: {class: 'ts-col-100'}
})
export class ReRunTestStepResultItemComponent implements OnInit {
  @Input('activeStepGroup') activeStepGroup: TestStepResult;
  @Input('testcaseResult') testcaseResult: TestCaseResult;
  @Input('testStepResults') testStepResult: TestStepResult;
  @Input('stepNumber') stepNumber: number;
  @Output('activeStepGroupAction') activeStepGroupAction = new EventEmitter<TestStepResult>();
  @Input('isStepGroupChild') isStepGroupChild: Boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  fetchStepGroupResults(testStepResult: TestStepResult) {
    if(this.activeStepGroup?.id == testStepResult?.id)
      this.activeStepGroup = null;
    else {
      this.activeStepGroup = this.testStepResult;
      this.activeStepGroupAction.emit(testStepResult);
    }
  }

  isKubbutzStep(){
    return (this.testStepResult?.addonTestData || this.testStepResult?.addonElements) || (this.testStepResult.testStep?.isAddonAction && this.testStepResult?.testStep?.addonTemplate) && !this.testStepResult.template
  }

}
