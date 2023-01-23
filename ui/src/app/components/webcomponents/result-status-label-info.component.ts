import {Component, Input, OnInit} from '@angular/core';
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {TestDeviceResult} from "../../models/test-device-result.model";

@Component({
  selector: 'app-result-status-label-info',
  template: `
    <app-re-run-icon class="fz-18 mt-2 position-absolute" [resultEntity]="resultEntity"></app-re-run-icon>
    <div class="mr-5 px-10 ml-25"
         [class.running]="resultEntity?.isRunning"
         [class.passed]="resultEntity?.isPassed"
         [class.stopped]="resultEntity?.isStopped"
         [class.aborted]="resultEntity?.isAborted"
         [class.not-executed]="resultEntity?.isNotExecuted"
         [class.queued]="resultEntity?.isQueued"
         [class.failed]="resultEntity?.isFailed">
      <span *ngIf="resultEntity?.isFailed"
            [textContent]="resultEntity.failedPercentage+'%'">
      </span>
      <span *ngIf="resultEntity?.isAborted"
            [textContent]="resultEntity.abortedPercentage+'%'">
      </span>
      <span *ngIf="resultEntity?.isNotExecuted"
            [textContent]="resultEntity.notExecutedPercentage+'%'">
      </span>
      <span *ngIf="resultEntity?.isStopped"
            [textContent]="resultEntity.stoppedPercentage+'%'">
      </span>
      <span *ngIf="resultEntity?.isPassed">
      </span>
      <span *ngIf="resultEntity?.isQueued && !resultEntity?.isRunning"
            [textContent]="resultEntity?.queuedPercentage+'%'">
      </span>
      <span class="ml-2" *ngIf="!resultEntity?.isRunning"
            [translate]="'execution.result.'+resultEntity?.result"></span>
      <span class="ml-2" *ngIf="resultEntity?.isRunning"
            [translate]="'execution.result.running'"></span>
    </div>
  `,
  styles: []
})
export class ResultStatusLabelInfoComponent implements OnInit {
  @Input('result') resultEntity: TestCaseResult | TestSuiteResult | TestDeviceResult;

  constructor() {
  }

  ngOnInit() {
  }

}
