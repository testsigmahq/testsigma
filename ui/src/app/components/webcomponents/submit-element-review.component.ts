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
  selector: 'app-submit-element-review',
  template: `
    <div class="mat-dialog-header border-0">
      <div
        class="ts-col-90 d-flex fz-15 rb-medium"
        [translate]="isInReview ? 'element.review.in_review_title' : 'testcase.review.submit_title'">
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
      <!--          *ngIf="elementForm && projectId"-->
      <!--          [formGroup]="elementForm"-->
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
        (click)="updateElement(false)"
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
             [matTooltip]="'element.hint.rework'|translate"></i>
        </div>
        <button
          class="btn btn-delete"
          [isLoading]="isSaving"
          [message]="'message.common.submitting'"
          appAsyncBtn
          (click)="updateElement(true)"
          [translate]="'btn.common.rework'"></button>
        <button
          [isLoading]="isSaving"
          [message]="'message.common.submitting'"
          appAsyncBtn
          (click)="updateElement(false)"
          [translate]="'btn.common.review'"
          class="theme-btn-primary ml-10"></button>
      </div>
    </div>
  `,
})
export class SubmitElementReviewComponent extends BaseComponent implements OnInit {
  public element: Element;
  public elements: any[];
  public elementForm: FormGroup;
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
    private elementService: ElementService,
    @Inject(MAT_DIALOG_DATA) public option: { elements: Element[]|undefined, element: Element|undefined,  projectId: number , isInReview: boolean|undefined},
    public dialogRef: MatDialogRef<SubmitElementReviewComponent>) {
    super(authGuard, notificationsService, translate, toastrService);
    this.element = Object.assign(new Element(), this.option.element||new Element());
    this.elements = this.option.elements;
    this.projectId = this.option?.projectId;
  }

  ngOnInit(): void {
    // this.addValidations();
  }

  updateElement(val){
    console.log(val);
  }

  // addValidations() {
  //   this.elementForm = new FormGroup({
  //     reviewedBy: new FormControl(this.element.reviewedBy, []),
  //   })
  // }
  // //TODO: Move each Status to separate APIs - Shabarish
  // ElementStatus(isRework) {
  //   this.isSaving = true;
  //   if (this.elements) {
  //     if (isRework) {
  //       this.element.status = ElementStatus.REWORK;
  //     } else if(this.isInReview){
  //       this.element.status = ElementStatus.READY;
  //     } else
  //       this.element.status = ElementStatus.IN_REVIEW;
  //     this.bulkUpdate(this.elements);
  //     return;
  //   }
  //   if (this.isInReview)
  //     this.element.reviewSubmittedById = this.authGuard.user.id;
  //   else
  //     this.element.reviewedById = this.authGuard.user.id;
  //   this.element.status = this.isInReview|| this.elements? ElementStatus.READY : ElementStatus.IN_REVIEW;
  //   this.element.comments = this.testerComments;
  //   if (isRework) {
  //     this.element.status = ElementStatus.REWORK;
  //   }
  //
  //   if (this.sendMailNotification) {
  //     this.element.sendMailNotification = this.sendMailNotification;
  //   } else {
  //     isRework = false;
  //   }
  //
  //   this.elementService.update(this.element.id, this.element, window.location.href).subscribe(
  //     (res) => {
  //       this.setComments(this.testerComments, isRework);
  //       res.reviewedBy = this.element.reviewedBy;
  //       this.sendMailNotification = false;
  //       this.isSaving = false;
  //       this.dialogRef.close(res);
  //     });
  // }

  bulkUpdate(ids: number[]) {
    // this.elementService.bulkUpdateStatus(ids,
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
  //     this.element.reviewedById = reviewer.id;
  //     this.element.reviewedBy = reviewer;
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
  //     comment.entity = 'ELEMENT';
  //     comment.row = this.element.id;
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
