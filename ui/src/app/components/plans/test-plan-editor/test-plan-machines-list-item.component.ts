import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {TestPlan} from 'app/models/test-plan.model';
import {TestDevice} from "../../../models/test-device.model";

@Component({
  selector: 'app-test-plan-machines-list-item',
  template: `<div
    [attr.module]="'plan-machines-list-item'"
    class="mt-15 position-relative pointer"
    [ngClass]="{'item-selected': isSelected}"
  >
    <!--tooltipIfTruncated-->
    <div class="text-truncate w-100" [matTooltip]="executionEnvironment.title">{{ executionEnvironment.title }}</div>
    <div class="fz-10 light-text-color mt-5 text-lowercase mb-15">
      {{ (executionEnvironment.testSuites?.length || executionEnvironment.suiteIds?.length ) || 0}} <span [translate]="'test_plans.suites.title'"></span>
    </div>
    <app-test-plan-machine-info [executionEnvironment]="executionEnvironment"></app-test-plan-machine-info>
    <div class="machine-actions position-absolute d-inline-block" *ngIf="!isSelectOnly">
      <i class="fa-pencil-on-paper pointer hover-only" (click)="$event.stopPropagation(); editMachine()" [matTooltip]="'btn.common.edit' | translate"></i>
      <i class="fa-trash-thin pointer ml-10 hover-only" (click)="$event.stopPropagation(); removeMachine()" [matTooltip]="'btn.common.delete' | translate"></i>
    </div>
  </div>`,
  styleUrls: ['./test-plan-machines-list-item.component.scss']
})
export class TestPlanMachinesListItemComponent {
  @Input('executionEnvironment') executionEnvironment: TestDevice;
  @Input('execution') execution: TestPlan;
  @Input('applicationVersionsMap') applicationVersionsMap: Object;
  @Input('isSelected') isSelected: boolean;
  @Input('isSelectOnly') isSelectOnly: boolean;

  @Output('onDelete') onDelete: EventEmitter<TestDevice> = new EventEmitter<TestDevice>();
  @Output('onEdit') onEdit: EventEmitter<TestDevice> = new EventEmitter<TestDevice>();
  @Output('onMachineSettingsUpdate') onMachineSettingsUpdate: EventEmitter<{ executionEnvironment: TestDevice, key: string, value: any }> = new EventEmitter<{executionEnvironment: TestDevice, key: string, value: any}>();

  @ViewChild('trigger') trigger;

  public isSettingsOpen: Boolean;

  constructor() {}

  editMachine() {
    this.onEdit.emit(this.executionEnvironment);
  }

  removeMachine(){
    this.onDelete.emit(this.executionEnvironment);
  }

  openSettingsPopup() {
    this.isSettingsOpen = true;
  }

  handleMachineSettings(action, value) {
    switch (action) {
      case 'runTestSuitesInParallel':
      case 'createSessionAtCaseLevel':
        this.updateMachineSettings(action, value);
        break;
      case 'runTestCasesInParallel':
        this.updateMachineSettings('createSessionAtCaseLevel', value);
        this.updateMachineSettings('runTestCasesInParallel', value);
        this.updateMachineSettings('runTestCasesInSequenceSuiteIds', value? [] : this.executionEnvironment.suiteIds);
        break;
    }
  }

  updateMachineSettings(key, value) {
    this.onMachineSettingsUpdate.emit({ executionEnvironment: this.executionEnvironment, key, value });
  }

  get isHybrid() {
    return this.executionEnvironment.isHybrid;
  }

  get currentApplicationVersion() {
    return this.applicationVersionsMap?.[this.executionEnvironment?.testSuites?.[0]?.workspaceVersionId];
  }

  get isMobile() {
    return this.currentApplicationVersion?.application?.isMobile;
  }

  get isWeb() {
    return this.currentApplicationVersion?.application?.isWeb;
  }
}
