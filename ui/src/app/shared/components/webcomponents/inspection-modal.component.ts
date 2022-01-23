import {Component, Inject, Input, OnInit} from '@angular/core';
import {Agent} from "../../../agents/models/agent.model";
import {Page} from "../../models/page";
import {AgentService} from "../../../agents/services/agent.service";
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {AgentDevice} from "../../../agents/models/agent-device.model";
import {UploadService} from "../../services/upload.service";
import {Upload} from "../../models/upload.model";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {WorkspaceVersionService} from "../../services/workspace-version.service";
import {MirroringData} from "../../../agents/models/mirroring-data.model";
import {WorkspaceType} from "../../../enums/workspace-type.enum";
import {MobileOsVersionService} from "../../../agents/services/mobile-os-version.service";
import {CloudDevice} from "../../../agents/models/cloud-device.model";
import {AuthenticationGuard} from "../../guards/authentication.guard";
import {BaseComponent} from "../base.component";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MobileOsType} from "../../../agents/enums/mobile-os-type.enum";
import {TestPlanLabType} from "../../../enums/test-plan-lab-type.enum";
import {PlatformService} from "../../../agents/services/platform.service";
import {PlatformOsVersion} from "../../../agents/models/platform-os-version.model";
import {Platform} from "../../../agents/models/platform.model";
import {Pageable} from "../../models/pageable";
import {UploadType} from "../../enums/upload-type.enum";
import {ActivatedRoute, Params} from "@angular/router";
import {MobileInspectionComponent} from "../../../agents/components/webcomponents/mobile-inspection.component";

@Component({
  selector: 'app-inspection-modal',
  templateUrl: './inspection-modal.component.html',
  host: {'class': 'ts-col-100'},
})
export class InspectionModalComponent extends BaseComponent implements OnInit {
  @Input('elementInspection') elementInspection: boolean;
  @Input('versionId') versionId: number;
  @Input('uiId') uiId: number;
  public physicalDeviceForm: FormGroup;
  public manuallyInstalledAppForm: FormGroup;
  public uploadSelectionForm: FormGroup;
  public agents: Page<Agent>;
  public devices: Page<AgentDevice>;
  public uploads: Page<Upload>;
  public isPhysicalDevice: Boolean = true;
  public isManually: Boolean;
  public version: WorkspaceVersion;
  public platformOsVersions: PlatformOsVersion[];
  public isPhysicalDeviceSupported: Boolean = true;
  public formSubmitted: boolean;
  public appSubmitted: boolean;
  public workspaceType = WorkspaceType;
  public mobileOsType: MobileOsType;
  public launchAllowed: boolean = false;
  public agentChecksFailed: boolean = false;
  public agentCheckFailedMessage: String;
  private platForm: Platform;
  private platForms: Platform[];
  public capabilitiesForm: FormGroup;
  public testsigmaAgentEnabled: boolean = false;
  public deviceList: Page<AgentDevice>;
  public isDevicesAvailable: boolean = true;

  public cloudDevicesPage = new Page<CloudDevice>();
  public agentId: number;
  public platformOsVersion: PlatformOsVersion;
  public testPlanLabType = TestPlanLabType.TestsigmaLab;
  public agent: Agent;
  public platformOsVersionsPage: Page<PlatformOsVersion>;
  public isContainsApp: boolean = false;
  public refreshAppUpload: boolean = false;
  public showUploadDropDown: boolean = true;
  public upload: Upload;
  public uploading: boolean = false;
  public selectedTestsigmaLab:boolean = false;
  public isStepGroup:boolean;

  get showInlineLaunch() {
    return this.show(this.agentChecksFailed, this.isPhysicalDevice) && this.elementInspection;
  };

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: MirroringData,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private dialogRef: MatDialogRef<InspectionModalComponent>,
    private uploadService: UploadService,
    private mobileOsVersionService: MobileOsVersionService,
    private workspaceVersionService: WorkspaceVersionService,
    private agentService: AgentService,
    private formBuilder: FormBuilder,
    private matDialog: MatDialog,
    private platformService: PlatformService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    let id;
    if (this.data.workspaceVersionId == undefined) {
      id = this.versionId;
    } else {
      id = this.data.workspaceVersionId;
    }
    this.workspaceVersionService.show(id).subscribe((version) => {
      this.setInitialData(version);
      this.fetchPlatForms();
      this.initManualInstalledAppForm();
      this.fetchUploads();
    });
    this.initPhysicalDeviceForm();
    this.initUploadSelectionForm();
    this.addFormControls();
    this.isStepGroup = this.route.snapshot.queryParams.isStepRecord;
    this.route.params.subscribe((params: Params) => {
      this.data = new MirroringData();
      this.data.recording = true;
      this.data.uiId = null;
    });
  }

  hasInspectorFeature() {
    return (
      this.version && this.version.workspace.isAndroidNative) || (this.version && this.version.workspace.isIosNative);
  }

  get disableRecordButton() {
    return !this.launchAllowed || !this.showUploadDropDown || this.uploading || (this.uploads && this.uploads.content.length == 0)
  }

  public uploadSuccess(event) {
    if (event) {
      this.showUploadDropDown = true;
      this.uploading = false;
      this.upload = event;
      this.uploads.content.push(event);
    }
    if (this.uploads.content.length == 0 || (this.uploads.content.length > 0 && !event)) {
      this.showUploadDropDown = true;
    }
  }

  public openUploadForm() {
    this.showUploadDropDown = false;
    this.upload = new Upload();
  }

  fetchUploads() {
    let termQuery = '';
    if (this.version.workspace.workspaceType == WorkspaceType.AndroidNative) {
      termQuery += ",type:" + UploadType.APK + ",workspaceId:" + this.version.workspace.id;
    } else {
      termQuery += ",type:" + UploadType.IPA + ",workspaceId:" + this.version.workspace.id;
    }
    this.uploadService.findAll(termQuery, "name").subscribe(res => {
      this.uploads = res;
    });
  }

  addFormControls() {
    this.capabilitiesForm = this.formBuilder.group({});
    this.triggerAgentChecks();
  }

  setInitialData(version: WorkspaceVersion) {
    this.version = version;
    this.data.workspaceVersion = this.version;
    this.data.workspaceVersionId = this.version.id;
    if (this.version.workspace.workspaceType == WorkspaceType.AndroidNative) {
      this.isManually = false;
      this.isPhysicalDeviceSupported = true;
      this.mobileOsType = MobileOsType.ANDROID;
    } else {
      this.isManually = false;
      this.isPhysicalDeviceSupported = true;
      this.mobileOsType = MobileOsType.IOS;
    }
  }

  get isIOS() {
    return this.mobileOsType == MobileOsType.IOS;
  }

  initPhysicalDeviceForm() {
    this.physicalDeviceForm = new FormGroup({
      deviceId: new FormControl(this.data.device?.id, [Validators.required])
    });
  }

  get deviceId() {
    return this.physicalDeviceForm.controls['deviceId']?.value?.id;
  }

  initUploadSelectionForm() {
    this.uploadSelectionForm = new FormGroup({
      app_upload_id: new FormControl(this.data.uploadId, [Validators.required]),
    });
  }

  initManualInstalledAppForm() {
    this.manuallyInstalledAppForm = new FormGroup({
      app_package: new FormControl(this.data.app_package, [this.requiredIfValidator(() => this.version.workspace.isAndroidNative)]),
      app_activity: new FormControl(this.data.app_activity, [this.requiredIfValidator(() => this.version.workspace.isAndroidNative)]),
      bundle_id: new FormControl(this.data.bundleId, [this.requiredIfValidator(() => this.version.workspace.isIosNative)])
    });
  }

  triggerAgentChecks() {
    this.agentService.ping().subscribe({
      next: (agentInfo) => {
        if (agentInfo.isRegistered == true) {
          this.agentService.findByUuid(agentInfo.uniqueId).subscribe({
            next: (agent) => {
              this.agent = agent;
              this.agentId = agent.id;
              this.launchAllowed = true;
            },
            error: () => {
              this.agentChecksFailed = true;
              this.agentCheckFailedMessage = "agents.not_found";
            }
          })
        } else {
          this.agentChecksFailed = true;
          this.agentCheckFailedMessage = "agents.not_registered";
        }
      },
      error: () => {
        this.agentChecksFailed = true;
        this.agentCheckFailedMessage = "agents.unable_to_reach";
      }
    });
  }

  launch() {
    this.data.uiId = this.uiId;
    this.data.agent = this.agent;
    this.data.uploadId = this.uploadSelectionForm.getRawValue().app_upload_id;
    this.data.os_version = this.platformOsVersion;
    this.data.isManually = this.isManually;
    this.data.capabilities = this.capabilitiesForm.getRawValue().capabilities;
    this.data.testsigmaAgentEnabled = <boolean>this.isPhysicalDevice || this.testsigmaAgentEnabled;
    console.log("testsigma agent enabled: " + this.data.testsigmaAgentEnabled);
    this.formSubmitted = true;
    this.appSubmitted = true
    if (this.isPhysicalDevice && this.physicalDeviceForm.invalid) {
      return false;
    }
    if (!this.isManually && this.uploadSelectionForm.invalid) {
      return false
    }
    if (this.isManually && this.manuallyInstalledAppForm.invalid) {
      return false
    }
    if (!this.isPhysicalDevice) {
      this.data.device = null;
    }
    this.data.testPlanLabType = this.testPlanLabType;
    this.dialogRef.close(this.data);
    if (this.elementInspection) {
      this.launchRecording();
    }
  }

  launchRecording() {
    this.matDialog.open(MobileInspectionComponent, {
      data: this.data,
      panelClass: ['mat-dialog', 'full-width', 'rds-none', 'w-100', 'h-100'],
      disableClose: true
    });
  }

  setAgentDevice(device) {
    this.data.device = device;
  }

  fetchPlatForms() {
    this.platformService
      .findAll(this.version.workspace.workspaceType, this.testPlanLabType)
      .subscribe(res => {
        this.platForms = res;
        if (!this.platForm) {
          this.platForm = this.platForms[0];
        }
        this.fetchOsVersions();
      });
  }

  fetchOsVersions(setValue?: Boolean) {
    this.platformService.findAllOsVersions(this.platForm, this.version.workspace.workspaceType, this.testPlanLabType).subscribe(res => {
      this.platformOsVersions = res;
      this.setPlatformOsVersionsPage();
      // if (this.settingsFormGroup?.controls['osVersion'].value)
      //this.platformOsVersion = this.platformOsVersions.find(osVersion => osVersion.version == this.settingsFormGroup?.controls['osVersion'].value)
      if (!this.platformOsVersion || setValue) {
        this.platformOsVersion = this.platformOsVersions[0];
      }
    });
  }

  searchPlatforms(term) {
    this.platformOsVersionsPage = new Page<PlatformOsVersion>();
    if (!term) {
      this.platformOsVersionsPage.content = this.platformOsVersions;
    } else {
      this.platformOsVersionsPage.content = this.platformOsVersions.filter(device => {
        return device.name.toLowerCase().indexOf(term.toLowerCase()) > -1
      });
    }
  }

  setAgent(agent: Agent) {
    this.agentId = agent.id;
    this.agent = agent;
  }

  setPlatformOsVersionsPage() {
    let platformsPage = new Page<PlatformOsVersion>();
    platformsPage.content = this.platformOsVersions;
    platformsPage.pageable = new Pageable();
    platformsPage.pageable.pageNumber = 0;
    platformsPage.totalPages = 1;
    platformsPage.totalElements = this.platformOsVersions?.length;
    platformsPage.content?.forEach((platform, i) => {
      platformsPage.content[i].name = platform.displayName;
    })
    this.platformOsVersionsPage = platformsPage;
  };

  setDeviceList($event: Page<AgentDevice>) {
    this.deviceList = $event;
    if (this.deviceList?.empty) {
      this.isDevicesAvailable = false
    }
  }

  openChat() {
    // @ts-ignore
    window.fcWidget.open();
  }

  setContainsApp(contains: boolean) {
    this.isContainsApp = contains;
  }

  navigateToUploads() {
    window.open(`${window.location.origin}/#/td/${this.version?.id}/uploads`, '_blank');
  }

  show(agentCheckFailed: boolean, isPhysicalDevice: Boolean) {
    if (agentCheckFailed) {
      this.launchAllowed = true;
      this.testsigmaAgentEnabled = false;
      return !isPhysicalDevice;
    } else {
      return true;
    }
  }

  settestPlanLabType(testPlanLabType: TestPlanLabType) {
    if(testPlanLabType == TestPlanLabType.Hybrid){
      this.isPhysicalDevice = true;
      this.formSubmitted=false;
      this.appSubmitted=false;
      this.selectedTestsigmaLab = false;
      return
    }else if(testPlanLabType == TestPlanLabType.TestsigmaLab){
      this.isPhysicalDevice = false;
      this.formSubmitted=false;
      this.appSubmitted=false;
      this.selectedTestsigmaLab = true;
    }

    this.isPhysicalDevice = false;
    this.formSubmitted=false;
    this.appSubmitted=false;
    this.isManually=false;
    this.testPlanLabType = testPlanLabType;
    this.platformOsVersion = null;
    this.fetchPlatForms();

  }


  isTestStep(){
    return this.isStepGroup;
  }
}
