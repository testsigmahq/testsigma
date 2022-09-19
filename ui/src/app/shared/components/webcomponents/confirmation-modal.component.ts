import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {FormControl} from "@angular/forms";


@Component({
  selector: 'delete-dialog',
  template: `
                <div *ngIf="!modalData?.isPermanentDelete || modalData.disabled">
      <mat-dialog-content>
        <div
          class="confirm-message"
          [textContent]="modalData.description ? modalData.description: ('message.common.confirmation.default' | translate)"></div>
        <div
          class="confirm-note"
          [textContent]="modalData.confirmation ? modalData.confirmation : ('message.common.confirmation.note' | translate)"></div>
        <div
          *ngIf="modalData?.message"
          [innerHTML]="modalData.message"></div>
      </mat-dialog-content>
      <mat-dialog-actions class="confirm-actions" [style]="'padding-right: 0px !important'">
        <button class="theme-btn-primary"
                mat-dialog-close
                [translate]="'btn.common.cancel'"></button>
        <button class="border-0 p-8 pl-12 pr-12 btn btn-delete text-white theme-btn-clear-default"
                [disabled]="modalData.disabled"
                [translate]="modalData.yes ? modalData.yes:'btn.common.yes_delete'"
                [mat-dialog-close]="true"></button>
      </mat-dialog-actions>
    </div>
    <div *ngIf="modalData?.isPermanentDelete && !modalData.disabled">
      <div class="pb-25 pt-25">
        <div class="fz-20 pb-25 d-flex">
          <span [translate]="'message.common.confirmation.title_with_name'| translate: {Title: modalData?.title}"></span>
          <button
            class="close"
            type="button"
            [matTooltip]="'hint.message.common.close' | translate"
            mat-dialog-close>
          </button>
        </div>
        <div class="pb-15"
             [innerHTML]="'message.common.confirmation.name_note' | translate : {Name: modalData?.name}"></div>
        <div class="pb-15 lh-2point2" [translate]="modalData?.note"></div>
        <div class="rb-medium pb-25" [translate]="'message.common.confirmation.action_undone'"></div>
        <div class="ts-form">
          <input
            [formControl]="confirmText"
            type="text" class="form-control"
                 [placeholder]="'message.common.confirmation.placeholder' | translate"/>
        </div>
      </div>
      <div class="d-flex justify-content-end pb-25">
        <button
          mat-dialog-close
          class="theme-btn-clear-default"
          [translate]="'message.common.clear'"></button>
        <button
          [disabled]="!isValid"
          [mat-dialog-close]="true"
          class="border-0 p-8 pl-12 pr-12 btn btn-delete text-white theme-btn-clear-default"
                [translate]="'message.common.confirmation.btn_delete'| translate: {Item: modalData?.item}"></button>
      </div>
    </div>
  `,
})
export class ConfirmationModalComponent {
  public confirmText = new FormControl();
  public isValid : boolean = false;
  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
    this.confirmText.valueChanges.subscribe(() => this.isValid = this.confirmText.value.toLocaleLowerCase() == 'delete');

  }
}
