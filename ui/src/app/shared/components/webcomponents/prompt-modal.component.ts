import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';

@Component({
  selector: 'prompt-dialog',
  template: `
    <div class="px-25">
      <div>
        <h2
          class="confirm-message"
          [textContent]="modalData.description"></h2>
      </div>
      <div mat-dialog-actions class="confirm-actions">

        <div class="my-5">
          <h3
            style="font-weight: 400"
            *ngIf="modalData?.message"
            [innerHTML]="modalData.message"></h3>
        </div>
        <button class="theme-btn-primary my-5"
                mat-dialog-close
                [translate]="'btn.common.ok'"></button>
      </div>
    </div>`,
})
export class PromptModalComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
  }
}
