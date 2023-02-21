import {Component, Input, OnInit, EventEmitter, Output, Injectable} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {Upload} from "../../models/upload.model";
import {Page} from "../../models/page";
import {WorkspaceType} from "../../../enums/workspace-type.enum";
import {UploadType} from "../../enums/upload-type.enum";
import {UploadService} from "../../services/upload.service";
import {UploadVersion} from "../../models/upload-version.model";
import {UploadVersionService} from "../../services/upload-version.service";
import {TestPlanLabType} from '../../../enums/test-plan-lab-type.enum';
import {SupportedDeviceType} from "../../../agents/enums/supported-device-type";

@Component({
  selector: 'app-uploads-auto-complete',
  template: `
    <div class="d-flex ts-col-100 flex-wrap">
      <app-auto-complete
        [class.d-none]=""
        class="d-block"
        [class.ts-col-100]="!(isMultiVersion && showVersions)"
        [class.ts-col-65]="isMultiVersion && showVersions"
        id="appUploadId"
        [formGroup]="uploadForm"
        [formCtrlName]="formControl"
        [testPlanLabType]="testPlanLabType"
        [items]="uploads"
        [value]="value"
        (onSearch)="fetchUploads($event)"
        (onValueChange)="setUpload($event)"
      ></app-auto-complete>
      <app-auto-complete *ngIf="isMultiVersion && showVersions"
                         [class.d-none]=""
                         class="d-block ml-10"
                         [class.ts-col-30]="isMultiVersion"
                         id="appUploadVersionId"
                         [formGroup]="uploadForm"
                         [formCtrlName]="versionFormControl"
                         [testPlanLabType]="testPlanLabType"
                         [items]="uploadVersions"
                         [value]="uploadVersion"
                         (onSearch)="fetchUploads($event)"
                         (onValueChange)="setUploadVersion($event)"
      ></app-auto-complete>
      <div class="ml-auto text-purplish-blue pointer" *ngIf="isMultiVersion && !showVersions" (click)="showVersions=true" [translate]="'uploads.form.pick_other'"></div>
    </div>
  `,
  styles: []
})
export class UploadsAutoCompleteComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  @Input('formCtrl') public formControl: FormControl;
  @Input('versionFormCtrl') public versionFormControl: FormControl;
  @Input('formGroup') public uploadForm: FormGroup;
  @Input('testPlanLabType') testPlanLabType: TestPlanLabType;
  @Input('deviceId') deviceId: number;
  @Input('upload') upload?: Upload;
  @Output('isContainsApp') isContainsApp = new EventEmitter<boolean>();
  public value?: Upload;
  public uploads: Page<Upload>;
  public isMultiVersion: boolean;
  public uploadVersions: Page<UploadVersion>;
  public uploadVersion?: UploadVersion;
  public showVersions: boolean = false;

  constructor(
    private uploadService: UploadService, private uploadVersionService: UploadVersionService
  ) {
  }

  ngOnInit(): void {
    this.fetchUploads();
  }

  ngOnChanges() {
    this.fetchUploads();
  }

  fetchUploads(term?: string) {
    let termQuery = '';
    if (term) {
      termQuery = ",name:" + term + "*"
    }
    if (this.version.workspace.workspaceType == WorkspaceType.AndroidNative) {
      termQuery += ",workspaceId:" + this.version.workspace.id;
    } else {
      termQuery += ",workspaceId:" + this.version.workspace.id;
    }
    if(this.isIOSNativeHybridAndDeviceNotNull){
      termQuery = termQuery+",deviceId:"+this.deviceId;
    }
    this.formControl.enable();
    this.uploadService.findAll(termQuery, "name").subscribe(res => {
      this.uploads = res;
      if(this.upload && this.upload.id){
        this.value = this.upload;
        this.formControl.setValue(this.value?.id);
        this.versionFormControl.setValue(this.upload.latestVersionId);
      }else{
        if(res.content.length) {
          if(this.formControl.value && res.content.find(upload => upload.id == this.formControl.value)) {
            if(this.isIOSNativeHybridAndDeviceNotNull) {
              this.setUpload(res.content.find(upload => upload.id == this.formControl.value));
              this.formControl.setValue(this.value?.id);
            } else {
              this.setUpload(res.content.find(upload => upload.id == this.formControl.value));
              this.formControl.setValue(this.value?.id);
            }
          } else if(this.formControl.value && !res.content.find(upload => upload.id == this.formControl.value)) {
            this.uploadService.findAll("id:"+this.formControl.value+termQuery).subscribe(currentUpload => {
              this.setUpload(currentUpload.content[0]);
              this.formControl.setValue(this.value?.id);
              this.uploads.content.push(this.value)
            })
          } else if(!this.formControl.value && !this.isIOSNativeHybridAndDeviceNotNull){
            this.setUpload(res.content[0]);
            this.formControl.setValue(this.value?.id);
          } else if(!this.formControl.value && this.isIOSNativeHybridAndDeviceNotNull){
            if(this.isIOSNativeHybridAndDeviceNotNull){
              this.setUpload(res.content.find(upload => upload.signed));
            }
            this.formControl.setValue(this.value?.id);
          }
          if(this.isIOSNativeHybridAndDeviceNotNull){
            res.content.forEach((upload: Upload) => {
              upload.isDisabled = false;
            });
          }
          this.isContainsApp.emit(true);
        } else if(!Boolean(term)) {
          this.isContainsApp.emit(false);
        }
      }
    });

  }

  get isIOSNativeHybridAndDeviceNotNull() {
    return this.version.workspace.workspaceType == WorkspaceType.IOSNative && !!this.deviceId;
  }

  setUpload(upload) {
    if(this.value?.id != upload.id) {
      this.showVersions = false;
      this.uploadVersion = null;
      this.isMultiVersion = false;
    }
    this.value = upload;
    this.versionFormControl.setValue(upload?.latestVersionId);
    this.fetchVersions();
  }

  setUploadVersion(version) {
    this.uploadVersion = version;
    this.versionFormControl.setValue(this.uploadVersion.id);
  }

  fetchVersions(term?: String) {
    if (!this.value)
      return
    let termQuery = 'uploadId:' + this.value.id;
    if (term) {
      termQuery = ",name:" + term + "*"
    }
    this.uploadVersionService.findAll(termQuery, "id,desc").subscribe(versions => {
      this.uploadVersions = versions;
      if(this.uploadVersions.content.length > 1) {
        this.isMultiVersion = true;
        if(this.versionFormControl?.value && versions.content.find(uploadVersion => uploadVersion.id == this.versionFormControl?.value)) {
          this.setUploadVersion(versions.content.find(uploadVersion => uploadVersion.id == this.versionFormControl?.value));
        }else {
          this.setUploadVersion(this.uploadVersions.content[0]);
        }
      }
    })
  }

}
