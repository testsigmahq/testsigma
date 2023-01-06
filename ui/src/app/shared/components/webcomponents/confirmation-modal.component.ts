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
        <div
          *ngIf="modalData?.note"
          [class.note]="modalData?.titleText"
          [innerHTML]="modalData.note"
          class="text-t-secondary pb-15 lh-2 pt-15 pl-15"></div>
      </mat-dialog-content>
      <mat-dialog-actions class="confirm-actions">
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
      <div *ngIf="modalData?.linkedEntityList?.cachedItems" class="pb-25">
        <div class="fz-20 pb-25 d-flex">
          <span *ngIf="modalData?.linkedEntityList?.cachedItems?.length > 0" class="fz-15" [innerHTML]="'step_group.delete.impacted.message' | translate : {Testcases:modalData?.linkedEntityList?.cachedItems?.length}"></span>
          <span *ngIf="modalData?.linkedEntityList?.cachedItems?.length == 0" class="fz-15" [innerHTML]="'step_group.delete.zero_impacted.message' | translate"></span>
          <button
            class="close"
            type="button"
            [matTooltip]="'hint.message.common.close' | translate"
            mat-dialog-close>
          </button>
        </div>
        <div *ngIf="modalData?.linkedEntityList?.cachedItems.length > 0" >
          <mat-dialog-content class="m-0 p-0 w-100">
            <div class="list-content overflow-x-hidden  theme-only-items-scroll delete-confirm-popup">
              <cdk-virtual-scroll-viewport
                class="list-container virtual-scroll-viewport border-rds-4 border-separator-1 mb-25"
                itemSize="27"
                style="max-height:261px;height:261px;">
                <div
                  *ngFor="let linkedEntity of modalData?.linkedEntityList?.cachedItems ; let index=index; let first=first"
                  [class.border-separator-t-1]="first"
                  class="list-view md-pm green-highlight ">
                  <div *ngIf="linkedEntity?.createdAt != linkedEntity?.updatedAt" [innerHTML]="'step_group.testcase.listing' | translate : {name : linkedEntity?.name, isEdited:'(Edited)'}" class="ts-col-80 pr-10">
                  </div>
                  <div *ngIf="linkedEntity?.createdAt == linkedEntity?.updatedAt" [innerHTML]="'step_group.testcase.listing' | translate : {name : linkedEntity?.name, isEdited:''}" class="ts-col-80 pr-10">
                  </div>

                  <div  (click)="openLinkedEntity(linkedEntity)" class="ts-col-15 pointer d-flex" target="_blank">
                    <span [textContent]="linkedEntity?.createdBy?.name" ></span>
                    <i class="fa-right-arrow-thin pl-5 ml-auto" style="color: #647488"></i>
                  </div>
                </div>
              </cdk-virtual-scroll-viewport>
            </div>
          </mat-dialog-content>
        </div>
        <div class="ts-form">
          <input
            [formControl]="confirmText"
            type="text" class="form-control"
            [placeholder]="'message.common.confirmation.placeholder' | translate"/>
        </div>

      </div>
        <div *ngIf="!modalData?.linkedEntityList?.cachedItems" class="pb-25">
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
          <div class="rb-medium pb-25" [innerHTML]="(modalData?.confirmation ? modalData.confirmation : 'message.common.confirmation.action_undone') | translate"></div>
        <div class="ts-form">
          <input
            [formControl]="confirmText"
            type="text" class="form-control"
                 [placeholder]="'message.common.confirmation.placeholder' | translate"/>
        </div>
      </div>
      <div class="d-flex justify-content-end">
        <button
          mat-dialog-close
          class="theme-btn-clear-default border-rds-4"
          [translate]="'btn.common.cancel'"></button>
        <button
          [disabled]="!isValid"
          [mat-dialog-close]="true"
          class="border-0 p-8 pl-12 pr-12 btn btn-delete text-white theme-btn-clear-default border-rds-4"
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
  ngOnInit(){
  }



  openLinkedEntity(linkedEntity) {
    let entityUrl;
    if(this.modalData?.entityUrl)
      entityUrl = this.formatEntityUrlIfExists(this.modalData?.entityUrl, linkedEntity);
    if(!entityUrl) {
      entityUrl = "/ui/td/cases/" + linkedEntity?.id + "/steps";
    }
    window.open(window.location.origin + entityUrl+"?stepGroupId="+this.modalData?.testCaseId, "_blank");
  }

  formatEntityUrlIfExists(url,linkedEntity){
    return this.replaceParamValues(url, {entityId: linkedEntity?.id, versionId: linkedEntity?.workspaceVersionId})
  }

  replaceParamValues(url:string,data:object){
    url.match(/{{.*?}}/g)?.forEach(placeholder => {
      url = url.replace(placeholder, data[placeholder?.slice(2,-2)]);
    })
    return url;
  }
  getAvatarName(name:string){
    const splitedName = name.split(" ");
    return `${splitedName[0]?.charAt(0).toUpperCase()}${splitedName[1]?.charAt(0).toUpperCase()}`;
  }
}
