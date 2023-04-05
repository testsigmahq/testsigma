import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../../../shared/components/base.component";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TestDevice} from "../../../models/test-device.model";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {DevicesService} from "../../../agents/services/devices.service";

@Component({
  selector: 'app-map-test-machine-form',
  template: `
    <div class="theme-overlay-container">
      <div class="theme-overlay-header px-15">
        <div class="theme-overlay-title flex-grow-1">
          <i
            [matTooltip]="'hint.message.common.back' | translate"
            class="fa-back-arrow pr-7 pointer" mat-dialog-close></i>
          <span translate="test_plan.suite.add_machine.map.title"></span></div>
      </div>
      <div class="theme-overlay-content px-14">
        <!-- Machine list -->
        <app-test-plan-machines-list-item
          [executionEnvironment]="environment"
          [applicationVersionsMap]="data.applicationVersionsMap"
          [isSelectOnly]="true"
          (click)="selectMachine(environment)"
          *ngFor="let environment of data.executionEnvironments"
        ></app-test-plan-machines-list-item>
      </div>
    </div>
  `,
  styles: [
  ]
})
export class MapTestMachineFormComponent extends BaseComponent {
  @ViewChild('trigger') trigger;
  @ViewChild('settingsDialog') settingsDialog: CdkConnectedOverlay;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { executionEnvironments: TestDevice[], applicationVersionsMap: Object },
    public dialogRef: MatDialogRef<MapTestMachineFormComponent>,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public devicesService: DevicesService) {
    super(authGuard, notificationsService, translate);
  }

  selectMachine(machine) {
    this.dialogRef.close(machine);
  }

  close() {
    this.dialogRef.close();
  }
}
