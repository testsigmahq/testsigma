import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-edit-comment-modal',
  templateUrl: './edit-comment-modal.component.html',
})
export class EditCommentModalComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
  }
  public modelComment : String;
  ngOnInit(): void {
    this.modelComment = this.modalData.comment.comment;
  }
  ngOnDestroy() {
    this.modalData.comment.comment = this.modelComment ;
  }
}
