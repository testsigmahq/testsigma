import {Component, Input, OnInit} from '@angular/core';
import {Page} from "../../models/page";
import {Attachment} from "../../../models/attachment.model";
import {AuthenticationGuard} from "../../guards/authentication.guard";
import {AttachmentService} from "../../../services/attachment.service";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {DomSanitizer} from '@angular/platform-browser';
import {BaseComponent} from "../base.component";

@Component({
  selector: 'app-list-attachments',
  templateUrl: './list-attachments.component.html',
  styles: [
  ]
})
export class ListAttachmentsComponent extends BaseComponent implements OnInit {
  @Input('attachmentRow') attachmentRow: string;
  @Input('attachmentRowId') attachmentRowId: number;
  public attachmentList: Page<Attachment>;
  public value: RegExp = new RegExp('\\.(jpeg|jpg|png|bmp|gif)$');
  public attachmentUrl: any;
  public selectedAttachment: Attachment;
  public isExpanded: boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private attachmentService: AttachmentService,
    private sanitizer: DomSanitizer) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  ngOnInit() {
  }

  ngOnChanges() {
    this.fetchAttachments();
  }

  fetchAttachments() {
    this.attachmentService.findAll('entity:' + this.attachmentRow + ',entityId:' + this.attachmentRowId).subscribe(res => {
      this.attachmentList = res;
    })
  }

  removeAttachment(id: number, collapse?: boolean) {
    this.attachmentService.remove(id).subscribe(() => {
      this.translate.get("message.common.deleted.success", {FieldName: 'Attachment'}).subscribe((res: string) => {
        this.showNotification(NotificationType.Success, res);
      });
      this.fetchAttachments();
      if(collapse){
        this.isExpanded = false;
      }
    })
  }

  uploadAttachment(event) {
    let file = event.target.files[0];
    if (file && file.size && file.size / 1024 / 1024 < 5) {
      let formData = new FormData();
      formData.append('entityId', this.attachmentRowId.toString());
      formData.append('entity', this.attachmentRow);
      formData.append('name', file.name);
      formData.append('fileContent', file);
      formData.append('type', '1');
      this.attachmentService.create(formData).subscribe(() => {
        this.translate.get("message.common.upload.success", {FieldName: 'Attachments'}).subscribe((res: string) => {
          this.showNotification(NotificationType.Success, res);
          this.fetchAttachments();
        });
      }, error => {
        this.translate.get("message.common.upload.failure", {FieldName: 'Attachments'}).subscribe((res: string) => {
          this.showAPIError(error, res);
          this.fetchAttachments();
        });
      })
    } else if (file.size / 1024 / 1024 >= 5) {

      this.translate.get("message.common.upload.limit_fail").subscribe((res: string) => {
        this.showNotification(NotificationType.Error, res);
        event.target.value = '';
      });
    }
  }

  expandAttachment(id: number) {
    this.toggleExpand(id)
    if (this.isExpanded)
      this.attachmentService.show(id).subscribe(res => {
        this.selectedAttachment = res;
        if(this.attachmentType() != 'Other') {
          this.attachmentUrl =  this.selectedAttachment.preSignedURL;
        } else {
          this.attachmentUrl = 'https://docs.google.com/gview?url=' + encodeURIComponent('' + this.selectedAttachment.preSignedURL) + "&embedded=true";
        }
        this.attachmentUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.attachmentUrl);
      })
  }

  toggleExpand(id: number) {
    if (this.selectedAttachment && this.selectedAttachment.id == id) {
      this.isExpanded = !this.isExpanded;
      this.selectedAttachment = null;
    } else {
      this.isExpanded = true;
    }
  }

  public attachmentType(): String {
    let testString = this.selectedAttachment.preSignedURL.toString(),
        imagesRegex = new RegExp('\.*(jpg|gif|png)'),
        videoRegex = new RegExp('\.*(webm|3gp|mp4|mov)');

    if (imagesRegex.test(testString)){
      return 'Image';
    } else if(videoRegex.test(testString)){
      return 'Video';
    } else {
      return 'Other';
    }
  }
}
