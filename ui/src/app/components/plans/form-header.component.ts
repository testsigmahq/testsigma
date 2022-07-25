import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {Router} from "@angular/router";

@Component({
  selector: 'plans-form-header',
  template: `
    <div class="align-items-center d-flex h-60p px-16 theme-border-b w-100">

      <div class="flex-grow-1 d-flex">
        <span class="go-back-icon" (click)="handleBack()"></span>
        <span [translate]="isEdit? 'test_plans.form.edit.title' : 'test_plans.form.create.title'" class="fz-18 ml-10 rb-medium"></span>

        <div class="border-rds-4 d-inline-flex flex-wrap ml-30">
          <mat-slide-toggle aria-labelledby="example-radio-group-label" [checked]="!isNewUI" (change)="handleUISwitch($event)">
            <span [translate]="'btn.common.switch_to_classic'"></span>
          </mat-slide-toggle>
        </div>
      </div>

      <div class="ml-30">
        <button
          *ngFor="let btn of headerBtns"
          (click)="btn.clickHandler()"
          [disabled]="btn.isDisabled"
          [class]="btn.className">{{ btn.content }}</button>
      </div>
    </div>`,
  styles: [
  ]
})
export class FormHeaderComponent {
  @Input('headerBtns') headerBtns: any[];
  @Input('isNewUI') isNewUI: boolean;
  @Input('isEdit') isEdit: boolean;
  @Input('version') version: WorkspaceVersion;

  @Output('onSwitchUI') onSwitchUI: EventEmitter<Boolean> = new EventEmitter<Boolean>();

  constructor(
    public router: Router
  ) {}

  handleUISwitch(event) {
    this.onSwitchUI.emit(event.checked);
  }

  handleBack() {
    this.router.navigate(['/td', this.version?.id, 'plans']);
  }
}
