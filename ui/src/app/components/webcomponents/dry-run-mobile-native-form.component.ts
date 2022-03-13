import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {DryTestPlan} from "../../models/dry-test-plan.model";
import {ApplicationPathType} from "../../enums/application-path-type.enum";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {Environment} from "../../models/environment.model";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-dry-run-mobile-native-form',
  templateUrl: './dry-run-mobile-native-form.component.html',
  styles: []
})
export class DryRunMobileNativeFormComponent extends BaseComponent implements OnInit {
  @Input('formGroup') nativeForm: FormGroup;
  @Input('version') version: WorkspaceVersion;
  @Input('testPlan') dryExecution: DryTestPlan;
  @Input('environment') environment?: Environment;
  @Output("closeDialog")closeDryRunDialog = new EventEmitter<any>();

  constructor( public authGuard: AuthenticationGuard,
               public notificationsService: NotificationsService,
               public translate: TranslateService) {
    super(authGuard,notificationsService,translate);
  }

  get environmentFormGroup(): FormGroup {
    return <FormGroup>(<FormGroup>(<FormGroup>this.nativeForm.controls['environments']).controls[0]);
  }

  get deviceId(): number {
    return (<FormGroup>(<FormGroup>this.nativeForm.controls['environments']).controls[0]).controls['deviceId']?.value;
  }

  get testPlanLabType(): TestPlanLabType {
    return <TestPlanLabType>this.nativeForm.controls['testPlanLabType'].value;
  }

  get isHybrid() {
    return this.nativeForm.controls['testPlanLabType'].value == TestPlanLabType.Hybrid;
  }

  get isAppUploadIdRequired() {
    return this.appPathType==ApplicationPathType.UPLOADS;
  }


  get isRest() {
    return this?.version?.workspace?.isRest
  }

  get isAndroidNative() {
    return this?.version.workspace.isAndroidNative
  }

  get isIosNative(){
    return this?.version.workspace.isIosNative
  }


  get appPathType(): ApplicationPathType {
    return <ApplicationPathType>(<FormGroup>this.environmentFormGroup)?.controls['appPathType']?.value;
  }

  ngOnInit(): void {
    this.addControls();
  }

  addControls() {
    this.environmentFormGroup.addControl('osVersion', new FormControl(this.dryExecution.environments[0].osVersion, []));
    this.environmentFormGroup.addControl('deviceName', new FormControl(this.dryExecution.environments[0].deviceName, []));
    this.environmentFormGroup.addControl('appUploadId', new FormControl(this.dryExecution.environments[0].appUploadId, [this.requiredIfValidator(() => this.isAppUploadIdRequired)]));
    (<FormGroup>(<FormGroup>this.nativeForm?.controls['environments']).controls[0]).addControl('deviceId', new FormControl(this.dryExecution.environments[0].deviceId, [this.requiredIfValidator(() => {
      return this.isHybrid;
    })]));
    this.environmentFormGroup.addControl('appPathType', new FormControl(this.dryExecution.environments[0].appPathType || (ApplicationPathType.UPLOADS), [Validators.required]));
    this.environmentFormGroup.addControl('appUrl', new FormControl(this.dryExecution.environments[0].appUrl, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.USE_PATH), Validators.pattern(/^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/)]));
    this.environmentFormGroup.addControl('appPackage', new FormControl(this.dryExecution.environments[0].appPackage, [this.requiredIfValidator(() => (this.appPathType == ApplicationPathType.APP_DETAILS && this.isAndroidNative))]));
    this.environmentFormGroup.addControl('appActivity', new FormControl(this.dryExecution.environments[0].appActivity, [this.requiredIfValidator(() => (this.appPathType == ApplicationPathType.APP_DETAILS && this.isAndroidNative))]));
    this.environmentFormGroup.addControl('platform', new FormControl(this.dryExecution.environments[0].platform, [this.requiredIfValidator(() => !this.isRest)]));
    this.environmentFormGroup.addControl('appBundleId', new FormControl(this.dryExecution.environments[0]?.appBundleId, [this.requiredIfValidator(() => (this.appPathType == ApplicationPathType.APP_DETAILS && this.isIosNative))]));
  }

  requiredIfValidator(predicate) {
    return (formControl => {
      if (!formControl.parent) {
        return null;
      }
      if (predicate()) {
        return Validators.required(formControl);
      }
      return null;
    })
  }
  patternIfValidator(predicate, pattern) {
    return (formControl => {
      if (!formControl.parent) {
        return null;
      }
      if (predicate()) {
        return Validators.pattern(pattern)(formControl);
      }
      return null;
    })
  }

  closeDialogTab(){
    this.closeDryRunDialog.emit();
  }
}
