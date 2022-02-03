import {Component, Input, OnInit, EventEmitter, Output, Injectable} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {Upload} from "../../models/upload.model";
import {Page} from "../../models/page";
import {WorkspaceType} from "../../../enums/workspace-type.enum";
import {UploadType} from "../../enums/upload-type.enum";
import {UploadService} from "../../services/upload.service";
import {UploadStatus} from "../../enums/upload-status.enum";
import {TestPlanLabType} from '../../../enums/test-plan-lab-type.enum';

@Component({
  selector: 'app-uploads-auto-complete',
  template: `
    <app-auto-complete
      [class.d-none]=""
      class="d-block"
      id="appUploadId"
      [formGroup]="uploadForm"
      [formCtrlName]="formControl"
      [testPlanLabType]="testPlanLabType"
      [items]="uploads"
      [value]="value"
      (onSearch)="fetchUploads($event)"
      (onValueChange)="setUpload($event)"
    ></app-auto-complete>
  `,
  styles: []
})
export class UploadsAutoCompleteComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  @Input('formCtrl') public formControl: FormControl;
  @Input('formGroup') public uploadForm: FormGroup;
  @Input('testPlanLabType') testPlanLabType: TestPlanLabType;
  @Input('deviceId') deviceId: number;
  @Input('upload') upload?: Upload;
  @Output('isContainsApp') isContainsApp = new EventEmitter<boolean>();
  public value?: Upload;
  public uploads: Page<Upload>;

  constructor(
    private uploadService: UploadService
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
      termQuery += ",type:" + UploadType.APK + ",workspaceId:" + this.version.workspace.id;
    } else {
      termQuery += ",type:" + UploadType.IPA + ",workspaceId:" + this.version.workspace.id;
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
      }else{
        if(res.content.length) {
          if(this.formControl.value && res.content.find(upload => upload.id == this.formControl.value)) {
            if(this.isIOSNativeHybridAndDeviceNotNull) {
              this.value = res.content.find(upload => upload.id == this.formControl.value && upload.signed);
              this.formControl.setValue(this.value?.id);
            } else {
              this.value = res.content.find(upload => upload.id == this.formControl.value);
              this.formControl.setValue(this.value?.id);
            }
          } else if(this.formControl.value && !res.content.find(upload => upload.id == this.formControl.value)) {
            this.uploadService.findAll("id:"+this.formControl.value+termQuery).subscribe(currentUpload => {
              this.value = currentUpload.content[0];
              this.formControl.setValue(this.value?.id);
              this.uploads.content.push(this.value)
            })
          } else if(!this.formControl.value && !this.isIOSNativeHybridAndDeviceNotNull){
            this.value = res.content[0];
            this.formControl.setValue(this.value?.id);
          } else if(!this.formControl.value && this.isIOSNativeHybridAndDeviceNotNull){
            if(this.isIOSNativeHybridAndDeviceNotNull){
              this.value = res.content.find(upload => upload.signed);
            }
            this.formControl.setValue(this.value?.id);
          }
          if(this.isIOSNativeHybridAndDeviceNotNull){
            res.content.forEach((upload: Upload) => {
              upload.isDisabled = !upload.signed;
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
    this.value = upload;
  }
}
