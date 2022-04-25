import { Component, OnInit, Input } from '@angular/core';
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestSuiteResult} from "../../models/test-suite-result.model";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {TestPlanResult} from "../../models/test-plan-result.model";
import { MatDialog } from '@angular/material/dialog';
import {ReRunDetailsComponent} from "./re-run-details.component";

@Component({
  selector: 'app-re-run-icon',
  template: `<i [matTooltip]="'runs.details.hint.re_run' | translate" (click)="openDetails();$event.stopImmediatePropagation();$event.stopPropagation();$event.preventDefault()"
                class="fa-re-run mr-5 text-t-secondary pointer" *ngIf="resultEntity?.lastRun?.id != resultEntity.id"></i>`
})
export class ReRunIconComponent implements OnInit {
  @Input('resultEntity') resultEntity: TestCaseResult | TestSuiteResult | TestDeviceResult | TestPlanResult;

  constructor(private matModal: MatDialog) { }

  ngOnInit(): void {
  }

  openDetails() {
    this.matModal.open(ReRunDetailsComponent, {
      width: '70%',
      height: '100vh',
      position: {top: '0', right: '0', bottom: '0'},
      data: {
        testCaseResult: this.testCaseResult,
        testSuiteResult: this.testSuiteResult,
        environmentResult: this.environmentResult,
        testPlanResult: this.testPlanResult},
      panelClass: ['mat-overlay']
    })
  }

  get testPlanResult() {
    if(this.resultEntity instanceof TestPlanResult)
      return this.resultEntity;
    else if(this.resultEntity instanceof TestSuiteResult)
      return this.resultEntity?.testDeviceResult?.testPlanResult;
    else if(this.resultEntity instanceof TestDeviceResult)
      return this.resultEntity?.testPlanResult;
    else if(this.resultEntity instanceof TestCaseResult)
      return this.resultEntity?.testDeviceResult?.testPlanResult;
  }

  get testCaseResult() {
    if(this.resultEntity instanceof TestCaseResult)
      return this.resultEntity;
  }

  get environmentResult() {
    if(this.resultEntity instanceof TestDeviceResult)
      return this.resultEntity;
  }

  get testSuiteResult() {
    if(this.resultEntity instanceof TestSuiteResult)
      return this.resultEntity;
  }

}
