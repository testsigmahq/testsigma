import {Component, EventEmitter, Inject, Input, OnInit, Optional, Output} from '@angular/core';
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
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {UploadVersion} from "../../models/upload-version.model";
import {UploadVersionService} from "../../services/upload-version.service";

@Component({
  selector: 'app-uploads-form',
  animations: [collapse, fade],
  templateUrl: './uploads-form.component.html'
})
export class UploadsFormComponent extends BaseComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  public uploadedFileObject;
  @Input('upload') upload: Upload;
  @Optional() @Input('inline') inline?: Boolean;
  @Output('onUpload') uploadCallBack = new EventEmitter<any>();
  public uploadTypes = [UploadType.Attachment];
  public uploadForm: FormGroup;
  public uploading: boolean;
  public attachmentTypes = ".xlsx,.xls,image/*,.doc, .docx,.ppt, .pptx,.txt,.pdf,.mp4,.3gp";
  public apkTypes = ".apk";
  public ipaTypes = ".ipa";
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { version: WorkspaceVersion, upload: Upload, isInspection: boolean  },
    public dialogRef: MatDialogRef<UploadsFormComponent>,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private uploadService: UploadService,
    public uploadVersionService: UploadVersionService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    if(!this.upload)
      this.upload = new Upload();
    if(this.version){
      this.data.version = this.version;
    }
    if(this.data.upload) {
      this.upload = this.data.upload;
    }
    this.initiateForm();
  }

  private initiateForm(): void {
    this.uploadForm = new FormGroup({
      name: new FormControl(this.upload.name, [Validators.required, Validators.minLength(4), this.noWhitespaceValidator]),
      version: new FormControl(undefined, [Validators.required])    });
  }

  private get formData(): FormData {
    let version = this.version || this.data.version;
    let formData = new FormData(),
      rawData = this.getRawValue()
    formData.append("fileContent", this.uploadedFileObject || new File([""], ""));
    formData.append("name", rawData.name);
    formData.append("workspaceId", this.data.version.workspace.id.toString());
    formData.append("version", rawData.version);
    formData.append("uploadType", version.workspace.isAndroidNative ? UploadType.APK : version.workspace.isIosNative ? UploadType.IPA : UploadType.Attachment);
    return formData;
  }

  public update(): void {
    this.uploading = true;
    let updateDetailsOnly = !this.uploadedFileObject?.name;
    this.uploadService.save(this.upload.id, this.formData).subscribe(
      (upload) => {
        this.fetchUpload(upload.id);
      },
      err => {
        this.uploading = false;
        this.translate.get(updateDetailsOnly ? "message.common.update.failure" : "message.common.upload.failure", {FieldName: "File"})
          .subscribe(key => this.showAPIError(err, key,'Upload'))
      }
    );
  }

  public create(): void {
    this.uploading = true;
    this.uploadService.create(this.formData).subscribe(
      (upload) => {
        this.fetchUpload(upload.id);
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
      this.uploadVersionService.find(upload.latestVersionId).subscribe((version)=> {
        upload.latestVersion = version;
        if (upload.isCompleted || !this.data.version.workspace.isMobileNative) {
          this.uploadCallBack.emit(upload);
          if (!this.version) {
            this.uploading = false;
            this.translate.get("message.common.upload.success", {FieldName: "File"})
              .subscribe(key => this.showNotification(NotificationType.Success, key))
            this.dialogRef.close(upload);
          }
        } else if (upload.isInProgress) {
          setTimeout(() => {
            this.fetchUpload(upload.id);
          }, 5000);
        } else {
          this.uploadCallBack.emit(this.upload);
          if (!this.version) {
            this.uploading = false;
            this.dialogRef.close(true);
            this.translate.get("message.common.upload.failure", {FieldName: "File"}).subscribe(key => this.showAPIError('UnExpected Error', key));
          }
        }
    });
  });
  }

  public uploadedFile(event): void {
    this.uploadedFileObject = event.target.files ? event.target.files[0] : null;
    if (!this.upload.id) {
      this.upload.name = this.uploadedFileObject.name;
      this.uploadForm.controls.name.setValue(this.uploadedFileObject.name);
      this.uploadForm.controls.version.setValue(this.uploadedFileObject.name);
      this.upload.latestVersion = this.upload.latestVersion || new UploadVersion();
      this.upload.latestVersion.fileSize = this.uploadedFileObject.size;
    }
    if (this.upload.id){
      this.upload.latestVersion = this.upload.latestVersion || new UploadVersion();
        this.upload.latestVersion.fileSize = this.uploadedFileObject.size;
    }
  }

  public getRawValue = () => this.uploadForm.getRawValue();

  public removeUpload() {
    this.uploadedFileObject = null;
    if (!this.upload.id) {
      this.uploadForm.controls.name.setValue('');
      this.uploadForm.controls.version.setValue('');
    }
  }

  public onCancel(){
    this.uploadCallBack.emit(null);
  }

  get maxSizeError() {
    return this.uploadedFileObject?.size > (1024 * 1024 * (500));
  }

  get fileTypes() {
    let version = this.version || this.data.version;
    return version?.workspace?.isAndroidNative ? this.apkTypes : version?.workspace?.isIosNative ? this.ipaTypes : this.attachmentTypes;
  }

}
