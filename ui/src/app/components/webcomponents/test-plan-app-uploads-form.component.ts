import {Component, Input, OnInit} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {ApplicationPathType} from "../../enums/application-path-type.enum";
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-test-plan-app-uploads-form',
  template: `
      <div class="d-flex form-group flex-wrap" *ngIf="appSrcTypeControl">
          <mat-radio-group
                  aria-labelledby="example-radio-group-label"
                  class="ts-col-100 d-flex pb-40" [formControl]="appSrcTypeControl">
              <mat-radio-button class="pr-20" [value]="pathTypes[0]">
                  <span [translate]="'test_plan.application.path_type.'+pathTypes[0]"></span>
              </mat-radio-button>
              <mat-radio-button class="pr-20" [value]="pathTypes[1]" *ngIf="testPlanLabType && isHybrid">
                  <span [translate]="'test_plan.application.path_type.'+pathTypes[1]"></span>
              </mat-radio-button>
              <mat-radio-button [value]="pathTypes[2]">
                  <span [translate]="'test_plan.application.path_type.'+pathTypes[2]"></span>
              </mat-radio-button>
          </mat-radio-group>
          <div class="ts-col-60 pr-60" *ngIf="isAppUploadType">
              <app-uploads-auto-complete
                      [class.d-none]="isEmptyUploads"
                      [formGroup]="environmentFormGroup"
                      [formCtrl]="environmentFormGroup?.controls['appUploadId']"
                      [version]="version"
                      [testPlanLabType]="testPlanLabType"
                      [deviceId]="deviceId"
                      (isContainsApp)="setContainsApp($event)"
              ></app-uploads-auto-complete>
              <div *ngIf="isEmptyUploads" class="batch-banner">
                  <i class="batch-icon"></i>
                  <span class="flex-column d-flex ts-col-100-25">
              <span
                      class="text-t-highlight"
                      [translate]="'uploads.list.not_created'"></span>
              <span
                      [translate]="'agents.mobile.recorder.not_upload_list'"
                      class="text-link pointer"
                      (click)="navigateToUploads()"
              ></span>
            </span>
              </div>
          </div>
          <div class="ts-col-60 pr-60"
               *ngIf="(version.workspace.isAndroidNative || version.workspace.isIosNative) && isAppPathType">
              <input
                      type="text"
                      id="appUrl"
                      name="appUrl"
                      [formControl]="environmentFormGroup.controls['appUrl']"
                      [placeholder]="version.workspace.isIosNative ? ('test_plan.application.path_type.USE_PATH.placeholder_ipa' | translate) :'test_plan.application.path_type.USE_PATH.placeholder'| translate"
                      class="form-control"/>
              <label
                      [translate]="'message.common.label.url'"
                      for="appUrl" class="control-label required"></label>
          </div>
          <div
                  *ngIf="(version.workspace.isIosNative && isAppDetailsType)"
                  class="ts-col-60 pr-60">
              <div class="form-group p-0 mb-0">
                  <input
                          type="text"
                          id="appId"
                          name="appId"
                          [formControl]="environmentFormGroup.controls['appBundleId']"
                          [placeholder]="isHybrid ? ('test_plan.application.path_type.APP_DETAILS.placeholder_bundle_id'| translate) : ('test_plan.application.path_type.APP_DETAILS.app_id' | translate)"
                          class="form-control"/>
                  <label
                          [translate]="isHybrid ? 'test_plan.app_details.bundle_id' : 'test_plan.app_details.app_id'"
                          for="appId" class="control-label required"></label>
              </div>
          </div>
          <div class="ts-col-100 pr-60 d-flex"
               *ngIf="version.workspace.isAndroidNative && isAppDetailsType && isHybrid">
              <div class="ts-col-60 pr-30">
                  <input
                          type="text"
                          id="appPackage"
                          name="appPackage"
                          [formControl]="environmentFormGroup.controls['appPackage']"
                          [placeholder]="'test_plan.application.path_type.APP_DETAILS.placeholder_package_apk'| translate"
                          class="form-control"/>
                  <label
                          [translate]="'agents.mobile.recorder.app.package'"
                          for="appPackage" class="control-label required"></label>
              </div>
              <div class="ts-col-40">
                  <input
                          type="text"
                          id="appActivity"
                          name="appActivity"
                          [formControl]="environmentFormGroup.controls['appActivity']"
                          [placeholder]="'test_plan.application.path_type.APP_DETAILS.placeholder_activity'| translate"
                          class="form-control"/>
                  <label
                          [translate]="'agents.mobile.recorder.app.activity'"
                          for="appActivity" class="control-label required"></label>
              </div>
          </div>
      </div>
  `,
  styles: [
  ]
})
export class TestPlanAppUploadsFormComponent implements OnInit {
  @Input('formGroup') environmentFormGroup: FormGroup;
  @Input('version') version: WorkspaceVersion;
  @Input('deviceId') deviceId: number;
  @Input('testPlanLabType') testPlanLabTypeInput: TestPlanLabType;
  @Input('testPlanForm') testPlanForm: FormGroup;
  @Input('isEdit') isEdit?: boolean;
  public isEmptyUploads: boolean = false;

  constructor() { }

  ngOnInit(): void {
    this.initControls();
  }

  resetUploadFormControls(value): void {
    if(value == ApplicationPathType.USE_PATH) {
      this.environmentFormGroup.controls['appUrl']?.enable();
      this.environmentFormGroup.controls['appActivity']?.disable();
      this.environmentFormGroup.controls['appPackage']?.disable();
    } else if(value == ApplicationPathType.APP_DETAILS) {
      this.environmentFormGroup.controls['appUrl']?.disable();
      this.environmentFormGroup.controls['appActivity']?.enable();
      this.environmentFormGroup.controls['appPackage']?.enable();
      this.environmentFormGroup.controls['appBundleId']?.enable();
    } else {
      this.environmentFormGroup.controls['appActivity']?.disable();
      this.environmentFormGroup.controls['appPackage']?.disable();
      this.environmentFormGroup.controls['appUrl']?.disable();
    }
    if(value == "0"){
      this.environmentFormGroup.controls['appUrl']?.enable();
      this.appSrcTypeControl.setValue(ApplicationPathType.USE_PATH);
    }
  }

  initControls() {
    console.log("test-plan-app-uploads-form");
    this.environmentFormGroup.patchValue({appPathType: this.isEdit ? this.environmentFormGroup.controls['appPathType'].value :  ApplicationPathType.USE_PATH});
  }

  ngOnChanges(): void {
    this.resetUploadFormControls(this.appSrcTypeControl.value);
    this.appSrcTypeControl.valueChanges.subscribe(value => this.resetUploadFormControls(value));
    if(this.appSrcTypeControl.value == this.pathTypes[2])
      this.environmentFormGroup.get('appUploadId')?.setValidators(Validators.required);
    else
      this.environmentFormGroup.get('appUploadId')?.setValidators([]);
  }

  get isHybrid() {
    return this.testPlanLabType == TestPlanLabType.Hybrid;
  }

  get testPlanLabType() {
    return this.testPlanForm?.controls['testPlanLabType']?.value || this.testPlanLabTypeInput || this.environmentFormGroup.parent?.parent?.controls['testPlanLabType']?.value;
  }

  get pathTypes() {
    return Object.keys(ApplicationPathType);
  }

  get appSrcTypeControl(): FormControl {
    return <FormControl>(this.environmentFormGroup.getRawValue().appPathType? this.environmentFormGroup.controls['appPathType'] : ApplicationPathType.USE_PATH);
  }

  get isAppPathType() {
    return this.appSrcTypeControl?.value == ApplicationPathType.USE_PATH;
  }

  get isAppUploadType() {
    return this.appSrcTypeControl?.value == ApplicationPathType.UPLOADS;
  }

  get isAppDetailsType() {
    return this.appSrcTypeControl?.value == ApplicationPathType.APP_DETAILS;
  }

  setContainsApp(contains: boolean) {
    this.isEmptyUploads = !contains;
  }

  navigateToUploads(){
    window.open(`${window.location.origin}/ui/td/${this.version?.id}/uploads`, '_blank');
  }

}
