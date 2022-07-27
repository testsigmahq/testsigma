import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {FormArray, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestPlan} from "../../models/test-plan.model";
import {MatHorizontalStepper} from '@angular/material/stepper';
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {TestDevice} from "../../models/test-device.model";
import {ApplicationPathType} from "../../enums/application-path-type.enum";
import {MatDialog} from '@angular/material/dialog';
import {TestPlanAddSuiteFormComponent} from "./test-plan-add-suite-form.component";
import {TestSuite} from "../../models/test-suite.model";
import {TestStep} from "../../models/test-step.model";
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {TestPlanType} from "../../enums/execution-type.enum";
import {DevicesService} from "../../agents/services/devices.service";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";
import {PlatformOsVersion} from "../../agents/models/platform-os-version.model";
import {PlatformService} from "../../agents/services/platform.service";
import {PlatformBrowserVersion} from "../../agents/models/platform-browser-version.model";
import {CloudDevice} from "../../agents/models/cloud-device.model";
import {TranslateService} from "@ngx-translate/core";

@Component({
  selector: 'app-test-plan-suite-selection',
  templateUrl: './test-plan-suite-selection.component.html',
  styles: []
})
export class TestPlanSuiteSelectionComponent implements OnInit {
  @Input('formGroup') testPlanForm: FormGroup;
  @Input('formSubmitted') formSubmitted: boolean;
  @Input('version') version: WorkspaceVersion;
  @Input('testPlan') testPlan: TestPlan;
  @Input('stepper') stepper: MatHorizontalStepper;
  @ViewChild('suitesValidationDiv') suitesValidationDiv: ElementRef;
  @ViewChild('machineValidationDiv') machineValidationDiv: ElementRef;
  public activeEnvironmentFormGroup: FormGroup;
  public testDevices: TestDevice[] = [];
  public activeExecutionEnvironment: TestDevice;
  public isAdvanceAddToggle: boolean = false;
  public isEditEnvironmentActive: boolean = false;
  public testDeviceTitle : string;
  public platformOsversion : PlatformOsVersion;
  public platformBrowserVersion : PlatformBrowserVersion;
  public cloudDevice : CloudDevice;

  public showAddEnvironmentForm:boolean = true;
  public showAddSuiteForm:boolean = true;
  public agentIsOffline:boolean;
  isOpen = false;
  @ViewChild('caseLevelParallelDialog') overlayDir: CdkConnectedOverlay;
  @Output('updateHeaderBtns') updateHeaderBtns = new EventEmitter<{tabPosition: Number, buttons: any[]}>();
  @Input('tabPosition') tabPosition: Number;

  openCaseLevelParallelDialog(){
    this.isOpen = true;
    setTimeout(() => {
      this.overlayDir.overlayRef._outsidePointerEvents.subscribe(res => {
        this.overlayDir.overlayRef.detach();
        this.isOpen = false;
      });
    }, 200);
  }

  get isSuiteIdsInvalid(){
    return this.testDevices?.filter(environment => !environment?.suiteIds?.length)?.length;
  }

  constructor(
      private formBuilder: FormBuilder,
      private matDialog: MatDialog,
      private devicesService: DevicesService,
      private platformService: PlatformService,
      private translate: TranslateService) {
  }

  get testPlanLabType() {
    return this.testPlanForm.controls['testPlanLabType'].value;
  }

  get isHybrid() {
    return this.testPlanLabType == TestPlanLabType.Hybrid;
  }

  get isPrivateGrid() {
    return this.testPlanLabType == TestPlanLabType.PrivateGrid;
  }

  get environmentsFormControls(): FormGroup[] {
    return (<FormGroup[]>(<FormArray>this.testPlanForm.controls['testDevices']).controls);
  }

  get isAppUploadIdRequired() {
    return (this.appPathType == ApplicationPathType.UPLOADS) && this.isMobileNative;
  }

  get appPathType(): ApplicationPathType {
    return <ApplicationPathType>(<FormGroup>this.activeEnvironmentFormGroup)?.controls['appPathType']?.value;
  }

  get advancedSettings(): boolean {
    return this.testPlan.testPlanType == TestPlanType.DISTRIBUTED;
  }

  get isInValid(): boolean {
    return !!this.environmentsFormControls.find(environmentFormControl => { return environmentFormControl.invalid}) || this.environmentsFormControls.length == 0;
  }

  get isActiveEnvironmentInValid() : boolean {
    let suiteIdsAloneInvalid = true;
    for(let control in this.activeEnvironmentFormGroup.controls) {
      if(this.activeEnvironmentFormGroup.controls.hasOwnProperty(control)) {
        if (this.activeEnvironmentFormGroup.controls[control].invalid && control != 'suiteIds')
          suiteIdsAloneInvalid = false;
      }
    }
    return (this.advancedSettings && !this.activeExecutionEnvironment?.testSuites.length) ||
      (!this.advancedSettings && !suiteIdsAloneInvalid);
  }

  set advancedSettings(value: boolean) {
    if(value)
      this.testPlan.testPlanType = TestPlanType.DISTRIBUTED;
    else
      this.testPlan.testPlanType = TestPlanType.CROSS_BROWSER;
    this.testPlanForm.controls['testPlanType'].setValue(this.testPlan.testPlanType);
  }

  get hasDifferentSuites() {
    let suiteIds = this.testDevices[0]?.testSuites?.map(suite => suite.id) || [];
    return this.testDevices.length > 1 && this.testDevices.filter(env => {
      return env.platform && JSON.stringify(env.testSuites?.map(suite => suite.id)) != JSON.stringify(suiteIds);
    }).length > 0;
  }

  get isRest() {
    return this?.version?.workspace?.isRest
  }

  get isWeb() {
    return this?.version?.workspace?.isWeb
  }

  get isMobileNative() {
    return this?.version?.workspace?.isMobileNative
  }

  get isMobile(){
    return this.isMobileNative || this?.version?.workspace?.isMobileWeb
  }

  get isMobileWeb(){
    return this?.version?.workspace?.isMobileWeb
  }

  ngOnInit(): void {
    this.setFormStates();
    this.invokeBtnState(this.isInValid);
  }

  ngOnChanges() {
    setTimeout(()=> {
      if (this.formSubmitted && !this.testDevices.length) {
        this.machineValidationDiv?.nativeElement.scrollIntoView({behavior: "smooth"});
      } else if (this.formSubmitted && this.isSuiteIdsInvalid) {
        this.suitesValidationDiv?.nativeElement.scrollIntoView({behavior: "smooth"});
      }
    }, 200);
    if(this.testDevices.length > 0)
      return
    if (this.testPlan?.testDevices) {
      this.testPlan.testDevices.forEach(environment => {
        this.testDevices.push(environment);
        (<FormArray>this.testPlanForm.controls['testDevices']).push(this.createEnvironmentFormGroup(environment))
      });
    } else if (this.environmentsFormControls?.length > 0) {
      this.environmentsFormControls.forEach((environmentGroup: FormGroup) => {
        this.testDevices.push(new TestDevice().deserialize(environmentGroup.getRawValue()));
      })
    }
    this.addNewExecutionEnvironment();
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

  createEnvironmentFormGroup(environment?: TestDevice) {
    let suiteIds = environment?.testSuites?.map(v => v.id) || (
      this.advancedSettings ? [] : this.activeExecutionEnvironment?.testSuites.map(v => v.id)
    );
    let environmentFormGroup =  new FormGroup({
      id: new FormControl(environment?.id, []),
      agentId: new FormControl(environment?.agentId, [this.requiredIfValidator(() => this.isHybrid)]),
      deviceId: new FormControl(environment?.deviceId, [this.requiredIfValidator(() => this.isHybrid && this.version?.workspace.isMobile)]),
      settings:new FormGroup({}),
      platformOsVersionId :new FormControl(environment?.platformOsVersionId,  [this.requiredIfValidator(() => !this.isRest && !this.isHybrid && !this.isPrivateGrid)]),
      platformScreenResolutionId : new FormControl(environment?.platformScreenResolutionId, [this.requiredIfValidator(() => this.isWeb && !this.isHybrid && !this.isPrivateGrid)]),
      platformBrowserVersionId :new FormControl(environment?.platformBrowserVersionId, [this.requiredIfValidator(() => !this.version?.workspace.isMobile && !this.isRest && !this.isHybrid && !this.isPrivateGrid)]),
      platformDeviceId : new FormControl(environment?.platformDeviceId,  [this.requiredIfValidator(() => !this.isWeb && !this.isRest && !this.isHybrid)]),
      appUploadId : new FormControl(environment?.appUploadId, [this.requiredIfValidator(() => this.isAppUploadIdRequired)]),
      appUploadVersionId: new FormControl(environment?.appUploadVersionId, []),
      appPathType : new FormControl(environment?.appPathType || (ApplicationPathType.UPLOADS), [this.requiredIfValidator(() => this.isMobileNative)]),
      appUrl: new FormControl(environment?.appUrl, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.USE_PATH), Validators.pattern(/^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/)]),
      appPackage: new FormControl(environment?.appPackage, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.APP_DETAILS)]),
      appActivity:  new FormControl(environment?.appActivity, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.APP_DETAILS)]),
      suiteIds: this.formBuilder.array([this.formBuilder.control(suiteIds, Validators.required)]),
      title: new FormControl(environment?.title, []),
      createSessionAtCaseLevel: new FormControl(environment?.createSessionAtCaseLevel, []),
      browser: new FormControl(environment?.browser, [this.requiredIfValidator(() => !this.version?.workspace.isMobile && !this.isRest && (this.isHybrid || this.isPrivateGrid) )]),
      platform: new FormControl(environment?.platform, []),
      osVersion: new FormControl(environment?.osVersion, []),
      browserVersion: new FormControl(environment?.browserVersion, []),
      deviceName : new FormControl(environment?.deviceName, []),
      resolution : new FormControl(environment?.resolution, []),
      testPlanLabType: new FormControl(this.testPlan.testPlanLabType, [Validators.required]),
      workspaceVersionId: new FormControl(this.testPlan.workspaceVersionId|| this.version.id, [Validators.required]),
    });
    if(suiteIds?.length > 0) {
      environmentFormGroup.setControl('suiteIds', this.formBuilder.array([]));
      suiteIds.forEach(suiteId => {
        (<FormArray>environmentFormGroup.controls['suiteIds']).push(new FormControl(suiteId, [Validators.required]));
      })
    }
    return environmentFormGroup;
  }

  previous() {
    this.stepper.previous();
  }

  next() {
    this.stepper.next();
  }

  setFormStates(showEnv?:boolean,showSuite?:boolean){
    if(this.testDevices.length == 0){
      this.showAddEnvironmentForm = true;
      this.showAddSuiteForm = true;
    } else if(this.isEditEnvironmentActive){
      this.showAddEnvironmentForm = true;
      this.showAddSuiteForm = true;
    } else if((this.testDevices.length > 0) && !this.isEditEnvironmentActive && !this.advancedSettings){
      this.showAddEnvironmentForm = false;
      this.showAddSuiteForm = true;
    } else if((this.testDevices.length > 0) && !this.isEditEnvironmentActive && this.advancedSettings){
      this.showAddEnvironmentForm = false;
      this.showAddSuiteForm = false;
    }
    if(showEnv!==undefined){
      this.showAddEnvironmentForm=showEnv;
    }
    if (showSuite!==undefined) {
      this.showAddSuiteForm = showSuite;
    }
  }

  addNewExecutionEnvironment() {
    this.isEditEnvironmentActive = false;
    this.activeEnvironmentFormGroup = this.createEnvironmentFormGroup();
    let testSuites = this.testDevices[this.testDevices.length-1]?.testSuites || [];
    this.activeExecutionEnvironment = new TestDevice().deserialize(this.activeEnvironmentFormGroup.getRawValue());
    this.activeExecutionEnvironment.testSuites = this.advancedSettings ? [] : [...testSuites];
    this.setFormStates();
  }

  saveEnvironment() {
    let testDevice = new TestDevice().deserialize(this.activeEnvironmentFormGroup.getRawValue());
    testDevice.testSuites = this.activeExecutionEnvironment?.testSuites || this.testDevices[this.testDevices?.length-1]?.testSuites || [];
    this.setSuiteIdsInFormGroup(this.activeEnvironmentFormGroup);
    testDevice.title = this.activeEnvironmentFormGroup.value?.platform+" ";
    if (this.activeEnvironmentFormGroup.value?.osVersion)
      testDevice.title += "(" + this.activeEnvironmentFormGroup.value?.osVersion + ") ";
    if(this.activeEnvironmentFormGroup.value?.browser)
      testDevice.title += this.activeEnvironmentFormGroup.value?.browser+" ";
    if(this.activeEnvironmentFormGroup.value?.browserVersion)
      testDevice.title += "(" + this.activeEnvironmentFormGroup.value?.browserVersion + ") ";
    if(this.activeEnvironmentFormGroup.value?.deviceName)
      testDevice.title += "("+this.activeEnvironmentFormGroup.value?.deviceName+")";

    testDevice = this.normalizeFormValue(testDevice);
    if(this.isRest){
      testDevice.title = "Environment of("+this.activeExecutionEnvironment?.testSuites.map((suite) => suite.name).join(",")+")"
    }

    if(this.isHybrid && this.isMobile){
      this.devicesService.findByAgentId(testDevice.agentId).subscribe(res => {
        let device = res.content.find(device => device.id == testDevice.deviceId);
        testDevice.title = device.osName + "(" + device.osVersion +")"+" ("+device.name+")";
        this.populateEnvironmentsList(testDevice);
        this.addNewExecutionEnvironment();
      })
    } else {
      this.populateEnvironmentsList(testDevice);
      this.addNewExecutionEnvironment();
    }
    this.setFormStates(false);
  }

  private populateEnvironmentsList(testDevice){
    this.activeEnvironmentFormGroup.controls['title'].setValue(testDevice.title);
    this.activeEnvironmentFormGroup.controls['platformOsVersionId'].setValue(testDevice.platformOsVersionId);
    this.activeEnvironmentFormGroup.controls['platformScreenResolutionId'].setValue(testDevice.platformScreenResolutionId);
    this.activeEnvironmentFormGroup.controls['platformBrowserVersionId'].setValue(testDevice.platformBrowserVersionId);
    this.activeEnvironmentFormGroup.controls['platformDeviceId'].setValue(testDevice.platformDeviceId);
    this.activeEnvironmentFormGroup.controls['appUploadId'].setValue(testDevice.appUploadId);
    this.activeEnvironmentFormGroup.controls['appPathType'].setValue(testDevice.appPathType);
    this.activeEnvironmentFormGroup.controls['appUrl'].setValue(testDevice.appUrl);
    this.activeEnvironmentFormGroup.controls['appPackage'].setValue(testDevice.appPackage);
    this.activeEnvironmentFormGroup.controls['appActivity'].setValue(testDevice.appActivity);
    this.activeEnvironmentFormGroup.controls['browser'].setValue(testDevice.browser);
    let index = this.environmentsFormControls.indexOf(this.activeEnvironmentFormGroup);
    if(index > -1) {
      this.testDevices.splice(index, 1, testDevice);
    } else {
      (<FormArray>this.testPlanForm.controls['testDevices']).push(this.activeEnvironmentFormGroup);
      this.testDevices.push(testDevice);
    }
    console.log("testPlanFrom", this.testPlanForm);
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
    if((!this.isWeb && !this.isMobileWeb) || (this.isWeb && !this.isHybrid && !this.isPrivateGrid)){
      environment.browser = null;
    }
    return environment;
  }

  removeEnvironment(index) {
    this.environmentsFormControls.splice(index, 1);
    this.testDevices.splice(index, 1);
    this.addNewExecutionEnvironment();
    this.setFormStates();
  }

  addSuites() {
    this.matDialog.open(TestPlanAddSuiteFormComponent, {
      width: '65vw',
      height: '85vh',
      data: {testDevice: this.activeExecutionEnvironment, version: this.version, execution: this.testPlan},
      panelClass: ['mat-dialog', 'full-width', 'rds-none']
    }).afterClosed().subscribe(res => {
      if(res && !this.advancedSettings)
        this.updateSuites();
      else
        this.setSuiteIdsInFormGroup(this.activeEnvironmentFormGroup);
    });
  }

  updateSuites() {
    this.testDevices.forEach((environment, index) => {
      environment.testSuites = [...this.activeExecutionEnvironment.testSuites];
      this.setSuiteIdsInFormGroup(this.environmentsFormControls[index]);
    })
    if(this.environmentsFormControls.indexOf(this.activeEnvironmentFormGroup) == -1) {
      this.setSuiteIdsInFormGroup(this.activeEnvironmentFormGroup);
    }
  }

  setSuiteIdsInFormGroup(environmentFormGroup: FormGroup) {
    environmentFormGroup.setControl('suiteIds', this.formBuilder.array([]));
    this.activeExecutionEnvironment.testSuites.forEach(suite => {
      (<FormArray>environmentFormGroup.controls['suiteIds']).push(new FormControl(suite.id));
    });
  }

  removeSuite(suite: TestSuite) {
    const index = this.activeExecutionEnvironment.testSuites.indexOf(suite);
    this.activeExecutionEnvironment.testSuites.splice(index, 1);
    if(!this.advancedSettings)
      this.updateSuites();
  }

  drop(event: CdkDragDrop<TestStep[]>) {
    if (event.previousIndex != event.currentIndex) {
      moveItemInArray(this.activeExecutionEnvironment.testSuites, event.previousIndex, event.currentIndex);
      if(!this.advancedSettings)
        this.updateSuites();
    }
  }

  editEnvironment(environmentForm: FormGroup, index: number) {
    if (this.activeEnvironmentFormGroup != environmentForm) {
      this.activeEnvironmentFormGroup = null;
      this.isEditEnvironmentActive = true;
      if(environmentForm.value.platformBrowserVersionId!=null) {
        this.platformService.findBrowserVersion(environmentForm.value.platformBrowserVersionId, this.testPlanLabType).subscribe((platformBrowsersversion) => {
          environmentForm.controls['browser'].setValue(platformBrowsersversion.name.toUpperCase());
          setTimeout(() => {
            this.activeEnvironmentFormGroup = environmentForm;
            this.activeExecutionEnvironment = this.testDevices[index];
          }, 10);
        });
      }
      if(environmentForm.value.platformOsVersionId!=null) {
          this.platformService.findOsVersion(environmentForm.value.platformOsVersionId, this.testPlanLabType).subscribe((platformOsversion) => {
            environmentForm.controls['platform'].setValue(platformOsversion.platform);
            setTimeout(() => {
              this.activeEnvironmentFormGroup = environmentForm;
              this.activeExecutionEnvironment = this.testDevices[index];
            }, 10);
          });
      }
      else{
        setTimeout(()=> {
          this.activeEnvironmentFormGroup = environmentForm;
          this.activeExecutionEnvironment = this.testDevices[index];
        }, 10);
      }
    }
    this.setFormStates(true,true);
  }

  setCaseLevelNdParallelFlags(event, value) {
    if(!this.advancedSettings){
      if(!this.isEditEnvironmentActive){
        const length = ( <FormArray> this.testPlanForm.controls['testDevices']).length;
        for (let index = 0; index < length; index++) {
          const environmentFormGroup = ( <FormGroup> ( <FormArray> this.testPlanForm.controls['testDevices']).at(index));
          environmentFormGroup.controls[value].setValue(event.checked);
        }
      }
    }
  }

  patternIfValidator(predicate, pattern) {
    return (formControl => {
      if (!formControl.parent) {
        return null;
      }
      if (predicate()) {
        return Validators.pattern(pattern)(formControl)
      }
      return null;
    })
  }

  showAddEnvButton():boolean {
    return !this.showAddEnvironmentForm && this.testDevices.length > 0 && !this.isEditEnvironmentActive;
  }

  setAgentStatus(isAgentOnline:boolean){
    this.agentIsOffline = !isAgentOnline;
  }

  setEnvironmentVersion(version: WorkspaceVersion) {
    this.activeExecutionEnvironment.version = version;
    if(version.workspace.isMobileNative) {
      this.addControls(this.activeEnvironmentFormGroup, this.activeExecutionEnvironment);
      this.activeExecutionEnvironment.testSuites = [];
      setTimeout(()=>{
        this.activeExecutionEnvironment.browser=undefined;
        this.activeExecutionEnvironment.browserVersion=undefined;
        this.activeExecutionEnvironment.resolution=undefined;
        this.activeExecutionEnvironment.testSuites = [];
        this.activeEnvironmentFormGroup.get('suiteIds').patchValue([]);
        this.activeEnvironmentFormGroup.get('browser').patchValue('');
        this.activeEnvironmentFormGroup.get('browserVersion').patchValue('');
        this.activeEnvironmentFormGroup.get('resolution').patchValue('');
      }, 500);
    }
  }

  setEnvironmentPreRequisite(executionEnvironment: TestDevice) {
    this.activeExecutionEnvironment.prerequisiteTestDevicesId = executionEnvironment?.id || null;
    let index = this.testDevices.findIndex(env => env == executionEnvironment);
    this.activeExecutionEnvironment.prerequisiteTestDevicesIdIndex = index > -1 ? index : null;
    this.activeEnvironmentFormGroup.controls['prerequisiteTestDevicesIdIndex'].setValue(this.activeExecutionEnvironment.prerequisiteTestDevicesIdIndex);
    this.activeEnvironmentFormGroup.controls['prerequisiteTestDevicesId'].setValue(executionEnvironment?.id || null);
  }

  addControls(environmentFormGroup: FormGroup, environment?: TestDevice) {
    environmentFormGroup.addControl('deviceId', new FormControl(environment?.deviceId, [this.requiredIfValidator(() => !this.isRest && this.isHybrid)]));
    environmentFormGroup.addControl('appPathType', new FormControl(environment?.appPathType || ApplicationPathType.UPLOADS, [Validators.required]));
    environmentFormGroup.addControl('appUploadId', new FormControl(environment?.appUploadId, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.UPLOADS)]));
    environmentFormGroup.addControl('appUploadVersionId', new FormControl(environment?.appUploadVersionId, []));
    environmentFormGroup.addControl('appUrl', new FormControl(environment?.appUrl, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.USE_PATH), Validators.pattern(/^(http[s]?:\/\/){0,1}(www\.){0,1}[a-zA-Z0-9\.\-]+\.[a-zA-Z]{2,5}[\.]{0,1}/)]));
    if (this.version.workspace.isAndroidNative) {
      environmentFormGroup.addControl('androidAppPackage', new FormControl(environment?.appPackage, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.APP_DETAILS && this.isHybrid)]));
      environmentFormGroup.addControl('androidAppActivity', new FormControl(environment?.appActivity, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.APP_DETAILS&& this.isHybrid)]));
      /*environmentFormGroup.addControl('sauceLabAppId', new FormControl(environment?.sauceLabAppId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isExternalLabs && this.isSauceLab)]));
      environmentFormGroup.addControl('browserStackAppId', new FormControl(environment?.browserStackAppId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isExternalLabs && this.isBrowserStack)]));*/
    }
    else {
      environmentFormGroup.addControl('iosBundleId', new FormControl(environment?.appBundleId, [this.requiredIfValidator(() => this.appPathType == ApplicationPathType.APP_DETAILS && this.isHybrid)]));
      /*environmentFormGroup.addControl('sauceLabAppId', new FormControl(environment?.sauceLabAppId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isExternalLabs && this.isSauceLab)]));
      environmentFormGroup.addControl('browserStackAppId', new FormControl(environment?.browserStackAppId, [this.requiredIfValidator(() => this.appPathTypeValue == ApplicationPathType.APP_DETAILS && this.isExternalLabs && this.isBrowserStack)]));*/
    }
    return environmentFormGroup;
  }

  get selectedVersion(){
    return this.activeExecutionEnvironment?.version || this.version;
  }

  setTestPlanType() {
    return;
    if(this.testPlan.testPlanType == TestPlanType.CROSS_BROWSER){
      this.testPlan.testPlanType = TestPlanType.DISTRIBUTED;
    } else {
      this.testPlan.testPlanType = TestPlanType.CROSS_BROWSER;
    }
  }

  invokeBtnState(isInvalid) {
    this.updateHeaderBtns.emit({
      tabPosition: this.tabPosition,
      buttons: [
        {
          className: 'theme-btn-clear-default',
          content: this.translate.instant('pagination.previous'),
          clickHandler: ()=> this.previous()
        },
        {
          className: 'theme-btn-primary ml-15',
          content: this.translate.instant('pagination.next'),
          isDisabled: isInvalid,
          clickHandler: ()=> (isInvalid ? '' : this.next())
        }
      ]
    });
  }
}
