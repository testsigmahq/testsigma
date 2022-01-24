import {Component, OnInit} from '@angular/core';
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {Router} from "@angular/router";

@Component({
  selector: 'app-telemetry-notification',
  template: `
    <div class="p-20 h-100" style="background-color:#F0FFF0;overflow-x: hidden;">
    <mat-dialog-header>
      <div class="d-flex">
      <div class="ts-col-50 align-self-center" style="border: 0;">
        <span [translate]="'Telemetry'" class="modal-title"></span>
      </div>
         <div class="ts-col-10 align-self-center ml-auto">
         <a
          class="fa-close-alt mb-auto pb-auto action-hover-icon"
          (click)="closeAndRouteToDashboard()"
          [matTooltip]="'btn.common.close' | translate"></a>
         </div>
      </div>
    </mat-dialog-header>
    <mat-dialog-content>
      <div class="ts-col-100 pt-10 d-inline-flex mt-5">

        <div class="ts-col-100 d-inline">
          <div class="bd-highlight mb-3">
            <div class="rb-regular-i default-font" [translate]="'settings.telemetry.message1'"></div>
            <div class="p-2 pt-10 default-font bd-highlight" [innerHTML]="'settings.telemetry.message2'|translate"></div>
            <div class="p-2 pt-10 default-font bd-highlight">
              If you’d like to opt-out of sending usage stats, you can disable <a (click)="routeToTelemetry()" class="text-link">telemetry&nbsp;here</a>. For more information, check out our <a target="_blank" href="https://testsigma.com/privacy-policy">privacy policy.</a>
            </div>
          </div>
        </div>
      </div>
    </mat-dialog-content>
    </div>
  `,
  styles: []
})
export class TelemetryNotificationComponent implements OnInit {

  constructor(
    private matModal: MatDialog,
    private authGuard: AuthenticationGuard,
    private router: Router,
    private dialogRef: MatDialogRef<TelemetryNotificationComponent>
  ) {
  }


  ngOnInit() {
  }

  routeToTelemetry() {
    this.router.navigate(['settings', 'telemetry']);
    this.dialogRef.close();

  }

  closeAndRouteToDashboard() {
    this.router.navigate(['dashboard']);
    this.dialogRef.close();
  }
}
