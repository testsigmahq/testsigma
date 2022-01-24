import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {ListAttachmentsComponent} from "../../shared/components/webcomponents/list-attachments.component";

@Component({
  selector: 'app-result-attachment',
  templateUrl: './result-attachment.component.html',
  styles: []
})
export class ResultAttachmentComponent implements OnInit {
  @Input('attachmentRow') attachmentRow: string;
  @Input('attachmentRowId') attachmentRowId: number;
  @Input('smallWidget') smallWidget: boolean;
  @ViewChild(ListAttachmentsComponent) public attachmentsComponent: ListAttachmentsComponent;

  constructor() {
  }

  ngOnInit() {
  }
}
