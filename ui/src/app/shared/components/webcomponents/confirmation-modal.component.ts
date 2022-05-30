import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'delete-dialog',
  template: `
              <mat-dialog-content>
                <div
                  class="confirm-message"
                  [textContent]="modalData.description ? modalData.description: ('message.common.confirmation.default' | translate)"></div>
                <div
                  class="confirm-note pr-15"
                  [translate]="'message.common.confirmation.note'"></div>
                <div
                  *ngIf="modalData?.message"
                  [innerHTML]="modalData.message"></div>
              </mat-dialog-content>
              <mat-dialog-actions class="confirm-actions">
                  <button class="theme-btn-primary"
                          mat-dialog-close
                          [translate]="'btn.common.cancel'"></button>
                  <button class="border-0 p-8 pl-12 pr-12 btn btn-delete text-white theme-btn-clear-default"
                          [disabled]="modalData.disabled"
                          [translate]="'btn.common.yes_delete'"
                          [mat-dialog-close]="true"></button>
              </mat-dialog-actions>`,
})
export class ConfirmationModalComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
  }
}
