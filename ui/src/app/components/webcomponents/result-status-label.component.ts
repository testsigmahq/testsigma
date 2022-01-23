import {Component, Input, OnInit} from '@angular/core';
import {ResultBase} from "../../models/result-base.model";

@Component({
  selector: 'app-result-status-label',
  template: `
    <div class="pt-4 d-flex">
      <div
        [class.fa-result-0]="resultEntity.isPassed"
        [class.fa-result-1]="resultEntity.isFailed"
        [class.fa-result-2]="resultEntity.isAborted"
        [class.fa-result-3]="resultEntity.isNotExecuted"
        [class.fa-result-5]="resultEntity.isQueued || resultEntity.isRunning"
        [class.fa-result-6]="resultEntity.isStopped"
        class="sample mr-4">
      </div>
      <div
        class="chart-status result-status-text-1 d-flex"
        *ngIf="resultEntity.isFailed">
        <div [textContent]="resultEntity.failedPercentage+'%'"></div>
        <div
          class="status-text ml-2"
          [translate]="'execution.result.FAILURE'"></div>
      </div>
      <div
        class="chart-status result-status-text-2  d-flex"
        *ngIf="resultEntity.isAborted">
        <div [textContent]="resultEntity.abortedPercentage+'%'"></div>
        <div
          class="status-text ml-2"
          [translate]="'execution.result.ABORTED'"></div>
      </div>
      <div
        class="chart-status result-status-text-3 d-flex"
        *ngIf="resultEntity.isNotExecuted">
        <div [textContent]="resultEntity.notExecutedPercentage+'%'"></div>
        <div
          class="status-text ml-2"
          [translate]="'execution.result.NOT_EXECUTED'"></div>
      </div>
      <div
        class="chart-status result-status-text-6 d-flex"
        *ngIf="resultEntity.isStopped">
        <div [textContent]="resultEntity.stoppedPercentage+'%'"></div>
        <div
          class="status-text ml-2"
          [translate]="'execution.result.STOPPED'"></div>
      </div>
      <div
        class="chart-status text-t-secondary d-flex"
        *ngIf="resultEntity.isPassed">
        <div [textContent]="resultEntity.passedPercentage+'%'"></div>
        <div
          class="status-text ml-2"
          [translate]="'execution.result.SUCCESS'"></div>
      </div>
      <div class="chart-status text-t-secondary d-flex"
           *ngIf="resultEntity.isQueued">
        <div [textContent]="resultEntity.queuedPercentage+'%'"></div>
        <div
          class="status-text ml-2"
          [translate]="'execution.result.QUEUED'"></div>
      </div>
    </div>
  `,
  styles: []
})
export class ResultStatusLabelComponent implements OnInit {
  @Input('resultEntity') resultEntity: ResultBase;

  constructor() {
  }

  ngOnInit(): void {
  }

}
