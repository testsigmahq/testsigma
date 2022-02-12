import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {TestPlan} from "../../models/test-plan.model";
import {SchedulePlanFormComponent} from "./schedule-plan-form.component";
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-schedule-form-button',
  template: `
    <button
      (click)="openSchedule();$event.preventDefault();$event.stopImmediatePropagation();"
      class="btn text-t-secondary text-nowrap"
      [class.icon-btn]="!displayText"
      [class.theme-btn-clear-default]="displayText"
      [class.schedule-align]="displayText"
      [disabled]="testPlan?.lastRun?.isExecuting"
      [matTooltip]="displayText ? '' : 'runs.list_view.run_later' | translate">
      <i class="fa-schedule pt-1"></i>
      <span class="pl-8"
        *ngIf="displayText" [translate]="displayText"></span>
    </button>
  `,
  styles: [
  ]
})
export class ScheduleFormButtonComponent implements OnInit {
  @Input('displayText') displayText: string;
  @Input('testPlan') testPlan: TestPlan;
  @Output('onClose') onClose = new EventEmitter<void>();

  constructor(private matDialog: MatDialog) { }

  ngOnInit(): void {
  }

  openSchedule() {
    this.matDialog.open(SchedulePlanFormComponent, {
      height: "100vh",
      width: '600px',
      position: {top: '0px', right: '0px'},
      data: {testPlan: this.testPlan, scheduledPlan: null},
      panelClass: ['mat-dialog', 'rds-none']
    }).afterClosed().subscribe(res=> {
      if(res)
        this.onClose.emit();
    })
  }
}
