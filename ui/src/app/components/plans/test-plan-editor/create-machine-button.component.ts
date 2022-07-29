import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {TestDevice} from "../../../models/test-device.model";
import {TestSuite} from "../../../models/test-suite.model";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";
import {MatDialog} from "@angular/material/dialog";
import {MapTestMachineFormComponent} from "./map-test-machine-form.component";

@Component({
  selector: 'app-create-machine-button',
  template: `
    <button
      *ngIf="!isIconOnly"
      #trigger="cdkOverlayOrigin" cdkOverlayOrigin
      [ngClass]="{'disabled': isDisabled}"
      [matTooltip]="tooltip"
      class="theme-btn-primary ml-15"
      (click)="toggleDropdown()"
    >
      <span [translate]="'test_plan.add.machine'"></span>
      <span *ngIf="executionEnvironments.length" class="fa fa-caret-down ml-10"></span>
    </button>
    <i
      *ngIf="isIconOnly"
      #trigger="cdkOverlayOrigin" cdkOverlayOrigin
      [ngClass]="{'disabled': isDisabled}"
      [matTooltip]="tooltip"
      (click)="toggleDropdown()"
      class="fa-add-to-queue pointer ml-10 machine-add-icon-plan"
    ></i>
    <ng-template
      #envTagsDialog="cdkConnectedOverlay"
      cdkConnectedOverlay
      [cdkConnectedOverlayOrigin]="trigger"
      [cdkConnectedOverlayOpen]="isOpen">
      <div class="bg-white create-machine-options mt-5 overflow-hidden shadow-all2-b4">
        <ul class="m-0 p-10">
          <li (click)="onCreateMachine.emit()" class="p-10 pointer" [translate]="'test_plan.suite.add_machine.create'"></li>
          <li
            (click)="mapMachine()"
            [matTooltip]="(supportedMachines.length? '' : (executionEnvironments.length? 'test_plan.suite.add_machine.map.no_machine_appid' : 'test_plan.suite.add_machine.map.no_machine')) | translate"
            class="p-10 pointer" [ngClass]="{'disabled': !supportedMachines.length}" [translate]="'test_plan.suite.add_machine.map'"></li>
        </ul>
      </div>
    </ng-template>
  `,
  styleUrls: ['./create-machine-button.component.scss']
})
export class CreateMachineButtonComponent {
  @Input('isDisabled') isDisabled: boolean;
  @Input('tooltip') tooltip: string;
  @Input('executionEnvironments') executionEnvironments: TestDevice[];
  @Input('testSuiteList') testSuiteList: TestSuite[];
  @Input('applicationVersionsMap') applicationVersionsMap: Object;
  @Input('selectedTestSuites') selectedTestSuites: TestSuite[];

  @Output('onCreateMachine') onCreateMachine: EventEmitter<any> = new EventEmitter();
  @Output('onMapMachine') onMapMachine: EventEmitter<TestDevice> = new EventEmitter();

  @ViewChild('envTagsDialog') envTagsDialog: CdkConnectedOverlay;
  @ViewChild('trigger') trigger;
  @Input('isIconOnly') isIconOnly: boolean;

  isOpen = false;

  constructor(private matDialog: MatDialog) {}

  toggleDropdown() {
    if(!this.executionEnvironments.length) this.onCreateMachine.emit();
    if(this.isDisabled || !this.executionEnvironments.length) return;

    this.isOpen = true;
    setTimeout(() => {
      this.envTagsDialog.overlayRef._outsidePointerEvents.subscribe(res => this.closeDropdown() );
    }, 200);
  }

  closeDropdown() {
    this.envTagsDialog.overlayRef.detach();
    this.isOpen = false;
  }

  mapMachine() {
    this.closeDropdown();
    this.matDialog.open(MapTestMachineFormComponent, {
      width: '360px',
      height: '100vh',
      data: {
        executionEnvironments: this.supportedMachines,
        applicationVersionsMap: this.applicationVersionsMap
      },
      position: {top: '0px', right: '0px'},
      panelClass: ['mat-dialog', 'rds-none']
    }).afterClosed().subscribe((machine) => {
      if(machine) this.onMapMachine.emit(machine);
    })
  }

  get supportedMachines() {
    return this.selectedTestSuites.length? this.executionEnvironments.filter((env)=> (env.workspaceVersionId == this.selectedTestSuites[0].workspaceVersionId)) : [];
  }
}
