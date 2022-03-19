import {Component, EventEmitter, Inject, Input, OnInit, Output} from '@angular/core';
import {Upload} from "../../models/upload.model";
import {UploadService} from "../../services/upload.service";
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {UploadType} from "../../enums/upload-type.enum";
import {collapse, fade} from "../../animations/animations";
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {BaseComponent} from "../base.component";
import {AuthenticationGuard} from "../../guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {PlatformType} from "../../enums/platform-type.enum";
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {UploadStatus} from "../../enums/upload-status.enum";
import {WorkspaceType} from "../../../enums/workspace-type.enum";
import {TestCaseResultExternalMapping} from "../../../models/test-case-result-external-mapping.model";

@Component({
  selector: 'app-uploads-form',
  animations: [collapse, fade],
  templateUrl: './uploads-form.component.html'
})
export class UploadsFormComponent extends BaseComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  public uploadedFileObject;
  @Input('upload') upload: Upload;
  @Output('onUpload') uploadCallBack = new EventEmitter<any>();
  public uploadTypes = [UploadType.Attachment];
  public uploadForm: FormGroup;
  public uploading: boolean;
  public attachmentTypes= ".xlsx,.xls,image/*,.doc, .docx,.ppt, .pptx,.txt,.pdf,.mp4,.3gp";

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { version: WorkspaceVersion, upload: Upload, isInspection: boolean  },
    public dialogRef: MatDialogRef<UploadsFormComponent>,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private uploadService: UploadService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    if(!this.upload)
      this.upload = new Upload();
    this.upload.type = UploadType.Attachment;
    if(this.version){
      this.data.version = this.version;
    }
    if(this.data.upload) {
      this.upload = this.data.upload;
    }
    if (this.data.version.workspace.isAndroidNative) {
      this.uploadTypes.push(UploadType.APK);
      this.upload.type = UploadType.APK;
    } else if (this.data.version.workspace.isIosNative) {
      this.uploadTypes.push(UploadType.IPA);
      this.upload.type = UploadType.IPA;
    }
    this.initiateForm();
  }

  private initiateForm(): void {
    this.uploadForm = new FormGroup({
      name: new FormControl(this.upload.name, [Validators.required, Validators.minLength(4)]),
      type: new FormControl(this.upload.type)
    });
  }

  private get formData(): FormData {
    let formData = new FormData(),
        rawData = this.getRawValue()
    formData.append("fileContent", this.uploadedFileObject || new File([""], ""));
    formData.append("name", rawData.name);
    formData.append("uploadType", rawData.type );
    formData.append("workspaceId", this.data.version.workspace.id.toString())
    formData.append("platformType", PlatformType.TestsigmaLab)
    return formData
  }

  public update(): void {
    this.uploading = true;
    let updateDetailsOnly = !this.uploadedFileObject?.name;
    this.uploadService.save(this.upload.id, this.formData).subscribe(
      (upload) => {
        if(upload.isInProgress||updateDetailsOnly) {
          this.fetchUpload(upload.id);
        } else if(upload.isCompleted) {
          this.uploadCallBack.emit(upload);
          if(!this.version){
            this.uploading = false;
            this.translate.get("message.common.upload.success", {FieldName: "File"})
              .subscribe(key => this.showNotification(NotificationType.Success, key))
            this.dialogRef.close(upload);
          }
        } else {
          this.uploadCallBack.emit(this.upload);
          if(!this.version){
            this.uploading = false;
            this.dialogRef.close(true);
            this.translate.get("message.common.upload.failure", {FieldName: "File"})
              .subscribe(key => this.showAPIError('UnExpected Error', key));
          }
        }
      },
      err => {
        this.uploading = false;
        this.translate.get(updateDetailsOnly ? "message.common.update.failure" : "message.common.upload.failure", {FieldName: "File"})
          .subscribe(key => this.showAPIError(NotificationType.Error, key,'Upload'))
      }
    );
  }

  public create(): void {
    this.uploading = true;
    this.uploadService.create(this.formData).subscribe(
      (upload) => {
        if(upload.isInProgress) {
          this.fetchUpload(upload.id);
        } else if(upload.isCompleted) {
          this.uploadCallBack.emit(upload);
          if(!this.version){
            this.uploading = false;
            this.translate.get("message.common.upload.success", {FieldName: "File"})
              .subscribe(key => this.showNotification(NotificationType.Success, key))
            this.dialogRef.close(upload);
          }
        } else {
          this.uploadCallBack.emit(this.upload);
          if(!this.version){
            this.uploading = false;
            this.dialogRef.close(true);
            this.translate.get("message.common.upload.failure", {FieldName: "File"})
              .subscribe(key => this.showAPIError('UnExpected Error', key));
          }
        }
      },
      err => {
        this.uploading = false;
        this.translate.get("message.common.upload.failure", {FieldName: "File"})
          .subscribe(key => this.showAPIError(err, key,'Upload'))
      }
    );
  }

  fetchUpload(id: number){
    this.uploadService.find(id).subscribe(upload => {
      if(upload.isCompleted) {
        this.uploadCallBack.emit(upload);
        if(!this.version){
          this.uploading = false;
          this.translate.get("message.common.upload.success", {FieldName: "File"})
            .subscribe(key => this.showNotification(NotificationType.Success, key))
          this.dialogRef.close(upload);
        }
      }else if(upload.isInProgress){
        setTimeout(() => {
          this.fetchUpload(upload.id);
        }, 1000);
      }else{
        this.uploadCallBack.emit(this.upload);
        if(!this.version){
          this.uploading = false;
          this.dialogRef.close(true);
          this.translate.get("message.common.upload.failure", {FieldName: "File"})
            .subscribe(key => this.showAPIError('UnExpected Error', key));
        }
      }
    });
  }

  public uploadedFile(event): void {
    this.uploadedFileObject = event.target.files ? event.target.files[0] : null;
    this.upload.fileSize = this.uploadedFileObject.size;
    this.upload.fileName = this.uploadedFileObject.name;
    if(!this.upload.id) {
      this.upload.name = this.upload.fileName;
      this.uploadForm.controls.name.setValue(this.upload.fileName);
    }
  }

  public getRawValue = () => this.uploadForm.getRawValue();

  public removeUpload() {
    this.uploadedFileObject = null;
    this.upload.fileName = '';
    if(!this.upload.id)
      this.uploadForm.controls.name.setValue('');
  }

  public onCancel(){
    this.uploadCallBack.emit(null);
  }

  get maxSizeError() {
   return this.uploadedFileObject?.size > (1024 * 1024 * (this.getRawValue().type == this.uploadTypes[0] ? 75 : 500));
  }

}
