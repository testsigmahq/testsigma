import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {BaseComponent} from "../../../shared/components/base.component";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {TestDevice} from "../../../models/test-device.model";
import {TestPlan} from "../../../models/test-plan.model";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {TestSuite} from "../../../models/test-suite.model";
import {TestPlanType} from "../../../enums/execution-type.enum";
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {DevicesService} from "../../../agents/services/devices.service";
import {TestPlanLabType} from "../../../enums/test-plan-lab-type.enum";
import {ApplicationPathType} from "../../../enums/application-path-type.enum";

@Component({
  selector: 'app-test-plan-machine-selection-form',
  templateUrl: './test-plan-machine-selection-form.component.html',
  styles: [
  ]
})
export class TestPlanMachineSelectionFormComponent extends BaseComponent implements OnInit {
  @ViewChild('trigger') trigger;

  public activeEnvironmentFormGroup: FormGroup;
  public activeExecutionEnvironment = new TestDevice();
  public execution: TestPlan;

  public isSettingsOpen: boolean;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { testSuites:TestSuite[], execution: TestPlan, executionEnvironments: TestDevice[],executionType: TestPlanType, executionEnvironment: TestDevice, version:WorkspaceVersion, isEdit: boolean },
    private matDialog: MatDialog,
    public dialogRef: MatDialogRef<TestPlanMachineSelectionFormComponent>,
    private formBuilder: FormBuilder,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public devicesService: DevicesService) {
    super(authGuard, notificationsService, translate);
  }

  ngOnInit(): void {
    this.activeEnvironmentFormGroup = this.initFormGroup(this.data?.executionEnvironment);
    this.activeExecutionEnvironment = new TestDevice().deserialize(this.activeEnvironmentFormGroup.getRawValue());
    this.activeExecutionEnvironment.testSuites = this.data.testSuites;

    /*if(!this.isEdit && this.isWeb && !this.isHybrid) {
      this.handleMachineSettings('runTestSuitesInParallel', true);
      this.handleMachineSettings('runTestCasesInParallel', true);
    }*/
  }

  get version(): WorkspaceVersion {
    return this.activeExecutionEnvironment?.version || this.data?.executionEnvironment?.version || this.data?.version;
  }

  get testPlanLabType() {
    return this.activeEnvironmentFormGroup.controls['testPlanLabType'].value;
  }

  get isMobileNative() {
    return this?.version?.workspace?.isMobileNative
  }

  get isHybrid() {
    return this.testPlanLabType == TestPlanLabType.Hybrid;
  }

  get appPathTypeValue(): ApplicationPathType {
    return <ApplicationPathType>(<FormGroup>this.activeEnvironmentFormGroup)?.controls['appPathType']?.value;
  }

  get isMobile() {
    return this.version?.workspace?.isMobileWeb
      || this.version?.workspace?.isIosNative
      || this.version?.workspace?.isAndroidNative;
  }

  get application() {
    return this.version?.workspace;
  }

  get isEdit() {
    return !!this.data?.executionEnvironment;
  }

  get isWeb() {
    return this?.version?.workspace?.isWeb;
  }


  get activeExecutionEnvIndex() {
    let idx = this.data.executionEnvironments.indexOf(this.data.executionEnvironment);
    return idx >= 0? idx : this.data.executionEnvironments.length;
  }

  initFormGroup(environment?: TestDevice) {
    let testSuites = this.data.testSuites.map(suite => suite.id);

    let environmentFormGroup = this.formBuilder.group({
      id: new FormControl(environment?.id, []),
      title: new FormControl(environment?.title, [Validators.required, Validators.minLength(4), Validators.maxLength(120)]),
      testPlanLabType: new FormControl(environment?.testPlanLabType || TestPlanLabType.TestsigmaLab, [Validators.required]),
      workspaceVersionId: new FormControl(environment?.workspaceVersionId || this.version.id, [Validators.required]),
      prerequisiteEnvironmentId: new FormControl(environment?.prerequisiteEnvironmentId, []),
      prerequisiteEnvironmentIdIndex: new FormControl(environment?.prerequisiteEnvironmentIdIndex, []),
      //targetMachine: new FormControl(environment?.targetMachine, [this.requiredIfValidator(() => this.isHybrid)]),
      deviceId: new FormControl(environment?.deviceId, [this.requiredIfValidator(() => this.isHybrid && this.version?.workspace.isMobile)]),
      createSessionAtCaseLevel: new FormControl(environment?.createSessionAtCaseLevel, []),
      suiteIds: new FormControl(testSuites, [Validators.required]),
      platformOsVersionId : new FormControl(environment?.platformOsVersionId, []),
      platformBrowserVersionId : new FormControl(environment?.platformBrowserVersionId, []),
      platformScreenResolutionId : new FormControl(environment?.platformScreenResolutionId, []),
      platformDeviceId : new FormControl(environment?.platformDeviceId, [this.requiredIfValidator(() => this.isMobile )]),
      platform: new FormControl(environment?.platform, []),
      osVersion: new FormControl(environment?.osVersion, []),
      browser: new FormControl(environment?.browser, [this.requiredIfValidator(() => !this.version?.workspace.isMobileNative )]),
      browserVersion: new FormControl(environment?.browserVersion, []),
      resolution: new FormControl(environment?.resolution, []),
      deviceName: new FormControl(environment?.deviceName, []),
      capabilities : new FormControl(environment?.capabilities, [])
    });
    if(this.isMobileNative){
      return this.addControls(environmentFormGroup, environment);
    }
    return environmentFormGroup;
  }

  addControls(environmentFormGroup: FormGroup, environment?: TestDevice) {
    environmentFormGroup.addControl('deviceId', new FormControl(environment?.deviceId, [this.requiredIfValidator(() => this.isHybrid)]));
    environmentFormGroup.addControl('appPathType', new FormControl(environment?.appPathType || ApplicationPathType.UPLOADS, [Validators.required]));
    environmentFormGroup.addControl('appUploadId', new FormControl(environment?.appUploadId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.UPLOADS)]));
    environmentFormGroup.addControl('appUploadVersionId', new FormControl(environment?.appUploadVersionId, []));
    environmentFormGroup.addControl('appUrl', new FormControl(environment?.appUrl, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.USE_PATH), Validators.pattern(/^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/)]));
    if (this.version.workspace.isAndroidNative) {
      environmentFormGroup.addControl('androidAppPackage', new FormControl(environment?.appPackage, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isHybrid)]));
      environmentFormGroup.addControl('androidAppActivity', new FormControl(environment?.appActivity, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS&& this.isHybrid)]));/*
      environmentFormGroup.addControl('sauceLabAppId', new FormControl(environment?.sauceLabAppId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isExternalLabs && this.isSauceLab)]));
      environmentFormGroup.addControl('browserStackAppId', new FormControl(environment?.browserStackAppId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isExternalLabs && this.isBrowserStack)]));*/
    }
    else {
      environmentFormGroup.addControl('iosBundleId', new FormControl(environment?.appBundleId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isHybrid)]));/*
      environmentFormGroup.addControl('sauceLabAppId', new FormControl(environment?.sauceLabAppId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isExternalLabs && this.isSauceLab)]));
      environmentFormGroup.addControl('browserStackAppId', new FormControl(environment?.browserStackAppId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isExternalLabs && this.isBrowserStack)]));*/
    }
    return environmentFormGroup;
  }

  updateTitle($event) {
    this.activeEnvironmentFormGroup.get('title').setValue($event.target.value);
  }

  // TODO: E2E
  setEnvironmentVersion(version) {}

  setEnvironmentPreRequisite(executionEnvironment: TestDevice) {
    const index = this.data.executionEnvironments.findIndex(env => env == executionEnvironment);
    const prerequisiteEnvironmentIdIndex = index > -1 ? index : null;
    this.activeEnvironmentFormGroup.controls['prerequisiteEnvironmentIdIndex'].setValue(prerequisiteEnvironmentIdIndex);
    this.activeEnvironmentFormGroup.controls['prerequisiteEnvironmentId'].setValue(executionEnvironment?.id || null);
  }

  saveMachine() {
    if(!this.isEdit && this.isHybrid) {
      /*this.activeEnvironmentFormGroup.controls['runTestCasesInParallel'].setValue(false);
      this.activeEnvironmentFormGroup.controls['runTestSuitesInParallel'].setValue(false);
      this.activeEnvironmentFormGroup.controls['createSessionAtCaseLevel'].setValue(false);*/
    }
/*    if(!this.isEdit) {
      let isRunTestCasesInParallel = this.activeEnvironmentFormGroup.value.runTestCasesInParallel;
      let value = isRunTestCasesInParallel? [] : this.activeExecutionEnvironment.suiteIds;
      this.activeEnvironmentFormGroup.controls['runTestCasesInSequenceSuiteIds'].setValue(value);
    }*/
    this.dialogRef.close({formGroup: this.activeEnvironmentFormGroup, isEdit: this.isEdit });
  }

  handleMachineSettings(action, value) {
    switch (action) {
      case 'runTestSuitesInParallel':
      case 'createSessionAtCaseLevel':
        this.updateMachineSettings(action, value);
        break;
      case 'runTestCasesInParallel':
        this.updateMachineSettings('createSessionAtCaseLevel', value);
        this.updateMachineSettings('runTestCasesInParallel', value);
        this.updateMachineSettings('runTestCasesInSequenceSuiteIds', value? [] : this.activeExecutionEnvironment.suiteIds);
        break;
    }
  }

  updateMachineSettings(key, value) {
    this.activeExecutionEnvironment[key] = value;
    this.activeEnvironmentFormGroup.get(key).patchValue(value);
  }
}
