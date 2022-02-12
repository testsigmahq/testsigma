import { Component, OnInit, Input } from '@angular/core';
import {TestPlan} from "../../models/test-plan.model";

@Component({
  selector: 'app-reports-button',
  template: `
    <a
      *ngIf="testPlan.lastRun"
      [matTooltip]="displayText ? '' : 'hint.message.common.reports' | translate"
      [routerLink]="['/td/runs', testPlan.lastRun.id]"
      (click)="$event.preventDefault();$event.stopImmediatePropagation();"
      class="btn text-t-secondary text-nowrap"
      [class.icon-btn]="!displayText"
      [class.theme-btn-clear-default]="displayText">
      <i class="fa-pi-chart"></i>
      <span class="pl-10"
            *ngIf="displayText" [translate]="displayText"></span>
    </a>
    <div
      *ngIf="!testPlan.lastRun"
      class="text-t-secondary"
      [matTooltip]="'result.hint.not_run' | translate">
      <button
        class="btn text-t-secondary text-nowrap"
        [class.icon-btn]="!displayText"
        [class.theme-btn-clear-default]="displayText" [disabled]="true">
        <i class="fa-pi-chart"></i>
        <span class="pl-10"
              *ngIf="displayText" [translate]="displayText"></span>
      </button>
    </div>
  `,
  styles: [
  ]
})
export class ReportsButtonComponent implements OnInit {
  @Input('testPlan') testPlan: TestPlan;
  @Input('displayText') displayText: string;

  constructor() { }

  ngOnInit(): void {
  }

}
