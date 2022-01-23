import {Component, Inject, Optional} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'session-expired-modal',
  template: `<h2 mat-dialog-title>
                <span class="modal-title" [translate]="'mobile_recorder.confirm.close.title'"></span>
                <button type="button" class="close"  mat-dialog-close data-placement="bottom"
                        [matTooltip]="'pagination.delete' | translate"></button>
              </h2>
              <mat-dialog-content class="mat-typography p-40 mx-auto">
                <div [textContent]="'mobile_recorder.confirm.close.message'| translate:{minutes: (data?.timeOut)/60 }"> </div>
              </mat-dialog-content>
              <mat-dialog-actions align="end" class="p-40">
                <button class="theme-btn-clear-default"
                        mat-dialog-close
                        [translate]="'btn.common.cancel'"></button>
                <button class="btn btn-delete"
                        [translate]="'btn.common.confirm'"
                        [mat-dialog-close]="true"
                        cdkFocusInitial></button>
              </mat-dialog-actions>`
})
export class SessionExpiredModalComponent {
  constructor(
    @Optional() @Inject(MAT_DIALOG_DATA) public data: {
    timeOut: number
    }
  ){}

}
