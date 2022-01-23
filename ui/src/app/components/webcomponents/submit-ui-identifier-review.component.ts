import {Component, Inject, OnInit} from '@angular/core';
import {Page} from "../../shared/models/page";
import {FormControl, FormGroup} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../shared/components/base.component";
import {ElementService} from "../../shared/services/element.service";

@Component({
  selector: 'app-submit-ui-identifier-review',
  template: `
    <div class="mat-dialog-header border-0">
      <div
        class="ts-col-90 d-flex fz-15 rb-medium"
        [translate]="isInReview ? 'ui_identifier.review.in_review_title' : 'testcase.review.submit_title'">
      </div>
      <button
        class="close"
        [matTooltip]="'hint.message.common.close' | translate"
        mat-dialog-close>
      </button>
    </div>

    <form
      class="ts-form px-30 pt-20 rb-regular">
<!--      <div-->
<!--        *ngIf="!isInReview"-->
<!--        class="form-group border-rds-6 ">-->
<!--        <app-users-auto-complete-->
<!--          *ngIf="uiIdentifierForm && projectId"-->
<!--          [formGroup]="uiIdentifierForm"-->
<!--        ></app-users-auto-complete>-->
<!--        <label class="control-label" [translate]="'testcase.summary.reviewed_by'"></label>-->
<!--      </div>-->
      <div class="form-group">
          <textarea
            class="theme-border border-rds-4 w-100 px-10 py-15"
            [ngModelOptions]="{standalone: true}"
            [(ngModel)]="testerComments" #ctrl="ngModel"
            cdkTextareaAutosize
            (ngModelChange)="setModelValue(this.ctrl.value)"
            #autosize="cdkTextareaAutosize"
            cdkAutosizeMinRows="4"
            style="max-height: 150px"
            cdkAutosizeMaxRows="4"></textarea>

        <label class="control-label" [translate]="'testcase.review.comments'"></label>
      </div>
      <div
        class="form-group"
        *ngIf="isInReview">
        <mat-checkbox
          [ngModelOptions]="{standalone: true}"
          class="mat-checkbox"
          [(ngModel)]="sendMailNotification" #ctrl="ngModel"
          [checked]="sendMailNotification"
          (change)="updateSendMailNotification(ctrl.value)">
          <span [translate]="'testcase.review.mail'" class="rb-medium"></span></mat-checkbox>
      </div>
    </form>
    <div *ngIf="!isInReview" class="text-right px-25 ">
      <button
        class="theme-btn-clear-default"
        mat-dialog-close
        [translate]="'btn.common.cancel'"></button>
      <button
        (click)="updateUiIdentifier(false)"
        [isLoading]="isSaving"
        [message]="'message.common.submitting'"
        appAsyncBtn
        [translate]="isInReview ? 'btn.common.review' : 'btn.common.submit'"
        class="theme-btn-primary"></button>
    </div>

    <div *ngIf="isInReview" class="text-right px-25 mt-20">
      <div class="mr-5 d-flex justify-content-end">
        <div class="mt-4">
          <i class="mt-10 fa-info text-danger fz-20 mr-5 mt-10 pointer"
             [matTooltip]="'ui_identifier.hint.rework'|translate"></i>
        </div>
        <button
          class="btn btn-delete"
          [isLoading]="isSaving"
          [message]="'message.common.submitting'"
          appAsyncBtn
          (click)="updateUiIdentifier(true)"
          [translate]="'btn.common.rework'"></button>
        <button
          [isLoading]="isSaving"
          [message]="'message.common.submitting'"
          appAsyncBtn
          (click)="updateUiIdentifier(false)"
          [translate]="'btn.common.review'"
          class="theme-btn-primary ml-10"></button>
      </div>
    </div>
  `,
})
export class SubmitUiIdentifierReviewComponent extends BaseComponent implements OnInit {
  public uiIdentifier: Element;
  public uiIdentifiers: any[];
  public uiIdentifierForm: FormGroup;
  public isInReview: Boolean = false;
  public testerComments: String;
  public sendMailNotification: Boolean;
  public isSaving: Boolean = false;
  public projectId: number;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private uiIdentifierService: ElementService,
    @Inject(MAT_DIALOG_DATA) public option: { uiIdentifiers: Element[]|undefined, uiIdentifier: Element|undefined,  projectId: number , isInReview: boolean|undefined},
    public dialogRef: MatDialogRef<SubmitUiIdentifierReviewComponent>) {
    super(authGuard, notificationsService, translate, toastrService);
    this.uiIdentifier = Object.assign(new Element(), this.option.uiIdentifier||new Element());
    this.uiIdentifiers = this.option.uiIdentifiers;
    this.projectId = this.option?.projectId;
  }

  ngOnInit(): void {
    // this.addValidations();
  }

  updateUiIdentifier(val){
    console.log(val);
  }

  // addValidations() {
  //   this.uiIdentifierForm = new FormGroup({
  //     reviewedBy: new FormControl(this.uiIdentifier.reviewedBy, []),
  //   })
  // }
  // //TODO: Move each Status to separate APIs - Shabarish
  // updateUiIdentifier(isRework) {
  //   this.isSaving = true;
  //   if (this.uiIdentifiers) {
  //     if (isRework) {
  //       this.uiIdentifier.status = UiIdentifierStatus.REWORK;
  //     } else if(this.isInReview){
  //       this.uiIdentifier.status = UiIdentifierStatus.READY;
  //     } else
  //       this.uiIdentifier.status = UiIdentifierStatus.IN_REVIEW;
  //     this.bulkUpdate(this.uiIdentifiers);
  //     return;
  //   }
  //   if (this.isInReview)
  //     this.uiIdentifier.reviewSubmittedById = this.authGuard.user.id;
  //   else
  //     this.uiIdentifier.reviewedById = this.authGuard.user.id;
  //   this.uiIdentifier.status = this.isInReview|| this.uiIdentifiers? UiIdentifierStatus.READY : UiIdentifierStatus.IN_REVIEW;
  //   this.uiIdentifier.comments = this.testerComments;
  //   if (isRework) {
  //     this.uiIdentifier.status = UiIdentifierStatus.REWORK;
  //   }
  //
  //   if (this.sendMailNotification) {
  //     this.uiIdentifier.sendMailNotification = this.sendMailNotification;
  //   } else {
  //     isRework = false;
  //   }
  //
  //   this.uiIdentifierService.update(this.uiIdentifier.id, this.uiIdentifier, window.location.href).subscribe(
  //     (res) => {
  //       this.setComments(this.testerComments, isRework);
  //       res.reviewedBy = this.uiIdentifier.reviewedBy;
  //       this.sendMailNotification = false;
  //       this.isSaving = false;
  //       this.dialogRef.close(res);
  //     });
  // }

  bulkUpdate(ids: number[]) {
    // this.uiIdentifierService.bulkUpdateStatus(ids,
    //   Boolean(this.sendMailNotification),
    //   this.testerComments)
    //   .subscribe(res =>{
    //     this.isSaving = false;
    //     this.dialogRef.close(true);
    //   });
  }

  getCurrentItem(items: Page<any>, id: number) {
    let selectedItems = null;
    items.content.filter(item => {
      if (item.id == id) {
        selectedItems = item;
      }
    })
    return selectedItems;
  }

  // setReViewedBy(reviewer) {
  //   if (reviewer) {
  //     this.uiIdentifier.reviewedById = reviewer.id;
  //     this.uiIdentifier.reviewedBy = reviewer;
  //   }
  // }

  setModelValue(value) {
    this.testerComments = value;
  }

  // setComments(comments, rework) {
  //
  //   if (comments) {
  //     const comment = new Comment();
  //     comment.comment = comments.replace("\n", "<br>");
  //     comment.entity = 'FIELD_DEFINITION';
  //     comment.row = this.uiIdentifier.id;
  //     comment.isRework = rework;
  //     this.commentService.create(comment).subscribe((res: Comment) => {
  //       res.createdBy = this.authGuard.session.user;
  //     });
  //   }
  // }

  updateSendMailNotification(sendMailNotification) {
    this.sendMailNotification = sendMailNotification;
  }
}
