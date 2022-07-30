import {Component, Inject, OnInit, SimpleChanges} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {TestDevice} from "../../models/test-device.model";
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanAddSuiteFormComponent} from "./test-plan-add-suite-form.component";
import {TestPlanType} from "../../enums/execution-type.enum";
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {TestSuite} from "../../models/test-suite.model";
import {Workspace} from "../../models/workspace.model";
import {TestSuiteService} from "../../services/test-suite.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {NotificationsService} from 'angular2-notifications';
import {ApplicationPathType} from "../../enums/application-path-type.enum";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {DevicesService} from "../../agents/services/devices.service";

@Component({
  selector: 'app-test-plan-test-machine-select-form',
  templateUrl: './test-plan-test-machine-select-form.component.html'
})
export class TestPlanTestMachineSelectFormComponent extends BaseComponent implements OnInit {
  public activeExecutionEnvironment = new TestDevice();
  public originalExecutionEnvironment = new TestDevice();
  public submitted: Boolean;
  public workspace: Workspace;
  public testPlan: TestPlan;
  public activeEnvironmentFormGroup: FormGroup;
  public isEdit: boolean;
  public createSessionAtCaseLevel: Boolean;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { testPlan: TestPlan, testDevice: TestDevice, isEdit: boolean },
    private matDialog: MatDialog,
    public dialogRef: MatDialogRef<TestPlanTestMachineSelectFormComponent>,
    private testSuiteService: TestSuiteService,
    private formBuilder: FormBuilder,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public devicesService: DevicesService) {
    super(authGuard, notificationsService, translate, toastrService);
    this.createSessionAtCaseLevel = this.data.testDevice.createSessionAtCaseLevel;
  }

  get advancedSettings(): boolean {
    return this.data?.testPlan?.testPlanType == TestPlanType.DISTRIBUTED;
  }

  get environmentControl() {
    return this.activeEnvironmentFormGroup?.controls;
  }

  get version(): WorkspaceVersion {
    return this.activeExecutionEnvironment?.version || this.data.testDevice?.version || this.testPlan?.workspaceVersion;
  }

  get isHybrid() {
    return this.testPlan?.testPlanLabType == TestPlanLabType.Hybrid;
  }

  get isAppUploadIdRequired() {
    return this.appPathType == ApplicationPathType.UPLOADS;
  }

  get appPathType(): ApplicationPathType {
    return <ApplicationPathType>(<FormGroup>this.activeEnvironmentFormGroup)?.controls['appPathType']?.value;
  }

  get isMobile() {
    return this.testPlan?.workspaceVersion?.workspace?.isMobileWeb
        || this.testPlan?.workspaceVersion?.workspace?.isIosNative
        || this.testPlan?.workspaceVersion?.workspace?.isAndroidNative;
  }

  get isWeb() {
    return this.testPlan?.workspaceVersion?.workspace?.isWeb;
  }

  get isMobileNative() {
    return this.testPlan?.workspaceVersion?.workspace?.isIosNative
      || this.testPlan?.workspaceVersion?.workspace?.isAndroidNative;
  }

  ngOnInit(): void {
    this.activeExecutionEnvironment.testSuites = [];
    this.workspace = this.data.testPlan?.workspaceVersion?.workspace;
    this.testPlan = this.data.testPlan;
    if (this.data?.testDevice && this.data.isEdit) {
      this.isEdit = true;
      this.fetchActiveSuites("testDeviceId:" + this.data?.testDevice?.id);
    } else {
      this.data['testDevice'] = undefined;
      this.fetchActiveSuites("testPlanId:" + this.data?.testPlan?.id);
    }
  }

  fetchActiveSuites(query) {
    this.testSuiteService.findAll(query).subscribe(res => {
      this.activeExecutionEnvironment.testSuites = res.content;
      this.originalExecutionEnvironment = this.activeExecutionEnvironment;
      this.data.testDevice = this.data.testDevice? this.data.testDevice : new TestDevice();
      this.activeEnvironmentFormGroup = this.initFormGroup(this.data.testDevice);
    });
  }

  addSuites() {
    this.matDialog.open(TestPlanAddSuiteFormComponent, {
      width: '65vw',
      height: '85vh',
      data: {
        testDevice: this.activeExecutionEnvironment, version: this.version,
        execution: this.testPlan
      },
      panelClass: ['mat-dialog', 'full-width', 'rds-none']
    }).afterClosed().subscribe(res => {
      if(res == 'close') {
        this.dialogRef.close();
      } else if (res)
        this.updateSuites();
      else
        this.activeExecutionEnvironment = this.originalExecutionEnvironment;
    });
  }

  removeSuite(suite: TestSuite) {
    const index = this.activeExecutionEnvironment.testSuites.indexOf(suite);
    this.activeExecutionEnvironment.testSuites.splice(index, 1);
    this.updateSuites();
  }

  drop(event: CdkDragDrop<TestSuite[]>) {
    if (event.previousIndex != event.currentIndex) {
      moveItemInArray(this.activeExecutionEnvironment.testSuites, event.previousIndex, event.currentIndex);
      this.updateSuites();
    }
  }

  saveEnvironment() {
    this.submitted = true;
    if (!this.activeEnvironmentFormGroup.getRawValue().suiteIds.length && this.advancedSettings) return;
    let testDevice = new TestDevice().deserialize(this.activeEnvironmentFormGroup.getRawValue());
    testDevice.platform = this.activeEnvironmentFormGroup.getRawValue().platform;
    testDevice.osVersion = this.activeEnvironmentFormGroup.getRawValue().osVersion;
    testDevice.browserVersion = this.activeEnvironmentFormGroup.getRawValue().browserVersion;
    testDevice.deviceName = this.activeEnvironmentFormGroup.getRawValue().deviceName;
    testDevice.testPlanId = this.testPlan.id;
    testDevice.id = this.data.testDevice.id;
    testDevice.disable = false
    if (!this.testPlan.isCustomPlan)
      testDevice.suiteIds = this.activeEnvironmentFormGroup.getRawValue().suiteIds
    testDevice.createSessionAtCaseLevel = this.createSessionAtCaseLevel;
    if(this.testPlan.isCustomPlan){
      testDevice.suiteIds = this.activeEnvironmentFormGroup.getRawValue().suiteIds;
      testDevice.createSessionAtCaseLevel = this.activeEnvironmentFormGroup.getRawValue().createSessionAtCaseLevel;
    }
    if (this.testPlan.isHybrid) {
      testDevice.agentId = this.activeEnvironmentFormGroup.getRawValue().agentId;
    }
    if(!this.activeEnvironmentFormGroup?.value?.title){
      if (this.testPlan.isHybrid) {
        testDevice.title = testDevice.platform + "(" + testDevice.osVersion + ")";
      }
      if (testDevice.browser)
        testDevice.title += testDevice.browser + "";
      if (testDevice.browserVersion)
        testDevice.title += " (" + testDevice.browserVersion + ")";
      if (testDevice.deviceName)
        testDevice.title += " (" + testDevice.deviceName + ")";
    } else{
      testDevice.title = this.activeEnvironmentFormGroup.value.title
    }


    testDevice = this.normalizeFormValue(testDevice);
    if(this.isRest){
      testDevice.title = "Environment of("+this.activeExecutionEnvironment?.testSuites.map((suite) => suite.name).join(",")+")"
    }

    if(this.isHybrid && this.isMobile){
      this.devicesService.findByAgentId(testDevice.agentId).subscribe(res => {
        let device = res.content.find(device => device.id == testDevice.deviceId);
        testDevice.title = device.osName + "(" + device.osVersion +")"+" ("+device.name+")"
        this.dialogRef.close(testDevice);
      })
    } else {
      this.dialogRef.close(testDevice);
    }
  }

  private normalizeFormValue(environment) {
    if(!this.isMobileNative){
      environment.appPathType = null;
    }
    if(!environment.isAppUploadType) {
      environment.appUploadId = null;
    }
    if (!environment.isAppDetailsType) {
      environment.appPackage = null;
      environment.appActivity = null;
    }
    if (!environment.isAppPathType) {
      environment.appUrl = null;
    }
    if (this.isHybrid){
      environment.platformBrowserVersionId=null;
      environment.platformDeviceId = null;
      environment.platformOsVersionId= null;
      environment.platformScreenResolutionId = null;
    }
    if (!this.isHybrid) {
      environment.agentId = null;
      environment.deviceId = null;
    }
    if(!this.isWeb || (this.isWeb && !this.isHybrid)){
      environment.browser = null;
    }
    return environment;
  }

  get isRest() {
    return this?.version?.workspace?.isRest
  }

  initFormGroup(environment?: TestDevice) {
    let environmentFormGroup = this.formBuilder.group({
          id: new FormControl(environment?.id),
          agentId: new FormControl(environment?.agentId, [this.requiredIfValidator(() => this.isHybrid)]),
          deviceId: new FormControl(environment?.deviceId, [this.requiredIfValidator(() => this.isHybrid && this.version?.workspace.isMobile)]),
          createSessionAtCaseLevel: new FormControl(environment?.createSessionAtCaseLevel, []),
          suiteIds: this.formBuilder.array([this.formBuilder.control([], Validators.required)]),
          platformOsVersionId: new FormControl(environment.platformOsVersionId, [this.requiredIfValidator(() => !this.isRest && !this.isHybrid)]),
          platformScreenResolutionId: new FormControl(environment.platformScreenResolutionId, [this.requiredIfValidator(() => this.version?.workspace.isWeb && !this.isHybrid)]),
          platformBrowserVersionId: new FormControl(environment.platformBrowserVersionId, [this.requiredIfValidator(() => !this.version?.workspace.isMobile && !this.isRest && !this.isHybrid)]),
          platformDeviceId: new FormControl(environment.platformDeviceId, [this.requiredIfValidator(() => !this.version?.workspace.isWeb && !this.isRest && !this.isHybrid)]),
          browser: new FormControl(environment?.browser? environment.browser.toUpperCase() : environment.browser, [this.requiredIfValidator(() => !this.version?.workspace.isMobile && !this.isRest && this.isHybrid)]),
          platform: new FormControl(environment?.platform, [this.requiredIfValidator(() => !this.isRest && this.isHybrid)]),
          osVersion: new FormControl(environment?.osVersion, []),
          browserVersion: new FormControl(environment?.browserVersion, []),
          deviceName : new FormControl(environment?.deviceName, []),
          resolution : new FormControl(environment?.resolution, []),
          capabilities : new FormControl(this.formBuilder.array(environment?.capabilities || [])),
          appUploadId : new FormControl(environment?.appUploadId, [this.requiredIfValidator(() => this.isAppUploadIdRequired && this.isMobileNative)]),
          appUploadVersionId: new FormControl(environment?.appUploadVersionId, []),
          appPathType : new FormControl(environment?.appPathType || (ApplicationPathType.UPLOADS), [this.requiredIfValidator(() => this.isMobileNative)]),
          appUrl: new FormControl(environment?.appUrl, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.USE_PATH), Validators.pattern(/^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/)]),
          appPackage: new FormControl(environment?.appPackage, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.APP_DETAILS)]),
          appActivity:  new FormControl(environment?.appActivity, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.APP_DETAILS)]),
          workspaceVersionId: new FormControl(environment?.workspaceVersionId || this?.version?.id),
          testPlanLabType: new FormControl(environment?.testPlanLabType),
          prerequisiteTestDevicesId: new FormControl(environment?.prerequisiteTestDevicesId, []),
          prerequisiteTestDevicesIdIndex: new FormControl(environment?.prerequisiteTestDevicesIdIndex, []),
          title: environment?.title || ''
    });
    if(this.activeExecutionEnvironment.testSuites?.length > 0) {
      environmentFormGroup.setControl('suiteIds', this.formBuilder.array([]));
      this.activeExecutionEnvironment.testSuites.forEach(suite => {
        (<FormArray>environmentFormGroup.controls['suiteIds']).push(new FormControl(suite.id, [Validators.required]));
      })
    }
    return environmentFormGroup;
  }

  private updateSuites() {
    this.originalExecutionEnvironment.testSuites = this.activeExecutionEnvironment.testSuites;
    this.activeEnvironmentFormGroup.setControl('suiteIds', this.formBuilder.array([]));
    for (let suite of this.originalExecutionEnvironment.testSuites)
      (<FormArray>this.activeEnvironmentFormGroup.controls['suiteIds']).push(new FormControl(suite.id));
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

  setEnvironmentVersion(version: WorkspaceVersion) {
    this.activeExecutionEnvironment.version = version;
    this.addControls(this.activeEnvironmentFormGroup, this.activeExecutionEnvironment);
    this.activeExecutionEnvironment.testSuites = [];
    this.activeEnvironmentFormGroup.setControl('suiteIds', this.formBuilder.array([]));
    if(version.workspace.isMobileNative) {

      // setTimeout(()=>{
      this.activeExecutionEnvironment.browser=undefined;
      this.activeExecutionEnvironment.browserVersion=undefined;
      this.activeExecutionEnvironment.resolution=undefined;
      this.activeExecutionEnvironment.testSuites = [];
      this.activeExecutionEnvironment.appUploadId = undefined;

      this.activeEnvironmentFormGroup.get('suiteIds').patchValue([]);
      this.activeEnvironmentFormGroup.get('browser').patchValue('');
      this.activeEnvironmentFormGroup.get('browserVersion').patchValue('');
      this.activeEnvironmentFormGroup.get('resolution').patchValue('');
      this.activeEnvironmentFormGroup.get('appUploadId').patchValue(undefined);
      // }, 500);
    }
  }

  setEnvironmentPreRequisite(executionEnvironment: TestDevice) {
    const index = this.testPlan.testDevices.findIndex(env => env == executionEnvironment);
    const prerequisiteTestDevicesIdIndex = index > -1 ? index : null;
    this.activeEnvironmentFormGroup.controls['prerequisiteTestDevicesIdIndex'].setValue(prerequisiteTestDevicesIdIndex);
    this.activeEnvironmentFormGroup.controls['prerequisiteTestDevicesId'].setValue(executionEnvironment?.id || null);
  }

  get testLabType() {
    return this.activeEnvironmentFormGroup.controls['testPlanLabType'].value;
  }

  get activeExecutionEnvIndex() {
    let idx = this.testPlan.testDevices.indexOf(this.testPlan.testDevices.find(device => device.id == this.activeEnvironmentFormGroup.value.id));
    return idx >= 0? idx : this.testPlan.testDevices.length;
  }

  addControls(environmentFormGroup: FormGroup, environment?: TestDevice) {
    environmentFormGroup.addControl('deviceId', new FormControl(environment?.deviceId, [this.requiredIfValidator(() => this.isHybrid)]));
    environmentFormGroup.addControl('appPathType', new FormControl(environment?.appPathType || ApplicationPathType.UPLOADS, [Validators.required]));
    environmentFormGroup.addControl('appUploadId', new FormControl(environment?.appUploadId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.UPLOADS)]));
    environmentFormGroup.addControl('appUploadVersionId', new FormControl(environment?.appUploadVersionId, []));
    environmentFormGroup.addControl('appUrl', new FormControl(environment?.appUrl, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.USE_PATH), Validators.pattern(/^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/)]));
    if (this.version.workspace.isAndroidNative) {
      environmentFormGroup.addControl('appPackage', new FormControl(environment?.appPackage, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isHybrid)]));
      environmentFormGroup.addControl('appActivity', new FormControl(environment?.appActivity, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS&& this.isHybrid)]));
    }
    else {
      environmentFormGroup.addControl('appBundleId', new FormControl(environment?.appBundleId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isHybrid)]));
    }
    return environmentFormGroup;
  }

  get appPathTypeValue(): ApplicationPathType {
    return <ApplicationPathType>(<FormGroup>this.activeEnvironmentFormGroup)?.controls['appPathType']?.value;
  }

  updateTitle($event) {
    this.activeEnvironmentFormGroup.get('title').setValue($event.target.value);
  }

}
