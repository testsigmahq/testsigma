import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BaseComponent} from "../../shared/components/base.component";
import {TestCaseService} from "../../services/test-case.service";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {TestCase} from "../../models/test-case.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {DryTestPlanService} from "../../services/dry-test-plan.service";
import {DryTestPlan} from "../../models/dry-test-plan.model";
import {Screenshot} from "../../enums/screenshot.enum";
import {TestPlanLabType} from "../../enums/test-plan-lab-type.enum";
import {DryTestDevice} from "../../models/dry-test-device.model";
import {Router} from '@angular/router';
import {TestDeviceSettings} from "../../models/test-device-settings.model";
import {Page} from "../../shared/models/page";
import {Capability} from "../../shared/models/capability.model";
import {AdhocRunConfiguration} from "../../models/adhoc-run-configuration.model";
import {AdhocRunConfigurationService} from "../../services/adhoc-run-configuration.service";
import {DryRunSavedConfigListComponent} from "./dry-run-saved-config-list.component";
import {DryRunSavedConfigFormComponent} from "./dry-run-saved-config-form.component";
import {Environment} from "../../models/environment.model";
import {EnvironmentService} from "../../services/environment.service";
import {ElementService} from "../../shared/services/element.service";
import {AgentService} from "../../agents/services/agent.service";
import {ApplicationPathType} from "../../enums/application-path-type.enum";
import {TestCaseResultService} from "../../services/test-case-result.service";
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestPlanResult} from "../../models/test-plan-result.model";
import {PlatformService} from "../../agents/services/platform.service";

@Component({
  selector: 'app-dry-run-form',
  templateUrl: './dry-run-form.component.html',
  styles: []
})
export class DryRunFormComponent extends BaseComponent implements OnInit {
  public dryExecutionForm: FormGroup;
  public testCase: TestCase;
  public version: WorkspaceVersion;
  public testPlan: DryTestPlan;
  public configurations: AdhocRunConfiguration[];
  public configuration: AdhocRunConfiguration;
  public saving: boolean = false;
  public savingConfig: boolean = false;
  public environment: Environment;
  public noneConfiguration: AdhocRunConfiguration;
  public emptyElements: String[] = [];
  public invalidUrls: string[] = [];
  public searchAutoComplete = new FormControl();
  public filteredDryRunConfigs: AdhocRunConfiguration[] =[];
  public refresh: boolean=true;
  public zeroActiveAgents:boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testCaseService: TestCaseService,
    private versionService: WorkspaceVersionService,
    private dryTestPlanService: DryTestPlanService,
    private formBuilder: FormBuilder,
    private testCaseResultService: TestCaseResultService,
    private router: Router,
    private dialogRef: MatDialogRef<DryRunFormComponent>,
    private dryRunSavedConfigurationService: AdhocRunConfigurationService,
    private environmentService: EnvironmentService,
    private elementService: ElementService,
    private agentService: AgentService,
    private platformService: PlatformService,
    @Inject(MAT_DIALOG_DATA) public options: {
      testCaseId: number
    },
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService)
  }

  get isConfigurationChanged() {
    return this.dryExecutionForm && this.configuration != this.noneConfiguration && !this.configuration?.equals(this.dryExecutionForm.getRawValue())
  }

  get isRest(): boolean {
    return this.version?.workspace?.isRest;
  }

  get isWeb(): boolean {
    return this.version?.workspace?.isWeb;
  }

  get isMobileNative(): boolean {
    return this.version?.workspace?.isMobileNative;
  }

  get isMobileWeb(): boolean {
    return this.version?.workspace?.isMobileWeb;
  }

  get isAndroidNative(): boolean {
    return this.version?.workspace?.isAndroidNative;
  }
  get isHybrid() {
    return this.dryExecutionForm?.controls['testPlanLabType']?.value === TestPlanLabType.Hybrid;
  }

  ngOnInit(): void {
    this.testCaseService.show(this.options.testCaseId).subscribe(res => {
      this.testCase = res;
      this.fetchVersion();
    });
    this.agentService.findAll().subscribe(res => {
      res.content.forEach(agent => {
        if (!agent.isOnline()) {
          this.zeroActiveAgents = true;
        }else{
          this.zeroActiveAgents = false;
        }
      })
    });

    this.searchAutoComplete.valueChanges.subscribe((term) => {
      this.searchDryRunConfigs(term);
    })
  }

  fetchVersion() {
    this.versionService.show(this.testCase.workspaceVersionId).subscribe(res => {
      this.version = res;
      this.configuration = undefined;
      this.fetchEmptyElements();
      this.validateNavigationUrls();
      this.fetchSavedConfigurations();
      this.setConfigurationId(null);
    });
  }

  addFormControls() {
    this.dryExecutionForm = new FormGroup({
      screenshot: new FormControl(this.testPlan.screenshot, [Validators.required]),
      testPlanLabType: new FormControl(this.testPlan.testPlanLabType, [Validators.required]),
      matchBrowserVersion: new FormControl(this.testPlan.matchBrowserVersion, []),
      pageTimeOut: new FormControl(this.testPlan.pageTimeOut, [Validators.required]),
      elementTimeOut: new FormControl(this.testPlan.elementTimeOut, [Validators.required]),
      environmentId: new FormControl(this.testPlan.environmentId, []),
      environments: this.formBuilder.array([
        this.formBuilder.group({
          agentId : new FormControl(this.testPlan.environments[0].agentId, [this.requiredIfValidator(() => this.isHybrid)]),
          deviceId: new FormControl(this.testPlan.environments[0]?.deviceId, [this.requiredIfValidator(() => this.isHybrid && this.version?.workspace.isMobile)]),
          platformOsVersionId :new FormControl(this.testPlan.environments[0].platformOsVersionId,  [this.requiredIfValidator(() => !this.isRest && !this.isHybrid)]),
          platformScreenResolutionId : new FormControl(this.testPlan.environments[0].platformScreenResolutionId, [this.requiredIfValidator(() => this.version?.workspace.isWeb && !this.isHybrid)]),
          platformBrowserVersionId :new FormControl(this.testPlan.environments[0].platformBrowserVersionId, [this.requiredIfValidator(() => !this.version?.workspace.isMobile && !this.isRest && !this.isHybrid)]),
          platformDeviceId : new FormControl(this.testPlan.environments[0].platformDeviceId,  [this.requiredIfValidator(() => !this.version?.workspace.isWeb && !this.isRest && !this.isHybrid)])
        })
      ])
    })
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

  save() {
    this.saving = true;
    this.normalizeFormValue();
    this.dryTestPlanService.create(this.testPlan).subscribe((res: TestPlanResult) => {
      this.testCaseResultService.findAll("testPlanResultId:"+res.id+",iteration:null", "id,desc").subscribe((res: Page<TestCaseResult>) => {
        this.saving = false;
        this.dialogRef.close();
        this.router.navigate(['/td', 'test_case_results', res?.content[0]?.id]);
      });
    }, error => {
      this.saving = false;
      this.translate.get('execution.initiate.failure').subscribe((res) => {
        this.showAPIError(error, res);
      })
    });
  }

  fetchSavedConfigurations() {
    this.dryRunSavedConfigurationService.findAll(this.version.workspace.workspaceType).subscribe(res => {
      this.configurations = res;
      this.noneConfiguration = new AdhocRunConfiguration();
    }, (error) => {
      this.noneConfiguration = new AdhocRunConfiguration();
      this.setConfigurationId(null);
    })
  }

  updateConfiguration() {
    this.savingConfig = true;
    let configId = this.configuration?.id;
    this.configuration = new AdhocRunConfiguration().deserializeDryRunForm(this.dryExecutionForm.getRawValue());
    this.configuration.id = configId;
    this.dryRunSavedConfigurationService.update(this.configuration).subscribe((res) => {
        this.savingConfig = false;
        this.configuration = res;
        this.translate.get('message.common.update.success', {FieldName: 'Favorite Ad-hoc Run Config'}).subscribe((res) => {
          this.showNotification(NotificationType.Success, res);
        })
        this.fetchSavedConfigurations();
        this.setConfigurationId(this.configuration);
      },
      error => {
        this.savingConfig = false;
        this.translate.get('message.common.update.failure', {FieldName: 'Favorite Ad-hoc Run Config'}).subscribe((res) => {
          this.showAPIError(error, res);
        })
      })
  }

  setConfigurationId(configuration: AdhocRunConfiguration) {
    this.refresh= false;
    this.configuration = undefined;
    if(configuration?.id) {
      this.configuration = configuration;
      if(configuration!=null && configuration.platformOsVersionId!=null) {
        this.platformService.findOsVersion(configuration.platformOsVersionId, this.testPlan.testPlanLabType).subscribe((platformOsversion) => {
          configuration.platform = platformOsversion.platform;
          this.setDryFormValues(configuration);
        });
      }
      else{
        this.setDryFormValues(configuration);
      }
    } else {
      this.configuration = this.noneConfiguration;
      this.createExecution();
      this.addFormControls();
    }
    setTimeout(()=> {this.refresh=true}, 100);
  }

  setDryFormValues(configuration: AdhocRunConfiguration) {
    let executionEnvironment = new DryTestDevice();
    executionEnvironment.platform = configuration.platform;
    executionEnvironment.capabilities = configuration.desiredCapabilities;
    this.testPlan.deserialize(configuration.serialize());
    this.testPlan.id = undefined;
    this.testPlan.environmentId = isNaN(parseInt(configuration.environmentId)) ? null : parseInt(configuration.environmentId);
    this.testPlan.screenshot = configuration.captureScreenshots;
    this.testPlan.testPlanLabType = configuration.type;
    executionEnvironment.platformDeviceId = configuration.platformDeviceId;
    executionEnvironment.platformOsVersionId = configuration.platformOsVersionId;
    executionEnvironment.platformBrowserVersionId = configuration.platformBrowserVersionId;
    executionEnvironment.platformScreenResolutionId = configuration.platformScreenResolutionId;
    executionEnvironment.browser = configuration.browser;
    executionEnvironment.appUploadId = configuration.appUploadId;
    executionEnvironment.appPackage = configuration.appPackage;
    executionEnvironment.appActivity = configuration.appActivity;
    executionEnvironment.appUrl = configuration.appUrl;
    executionEnvironment.appBundleId = configuration.appBundleId;
    executionEnvironment.appPathType = configuration.appPathType;
    executionEnvironment.agentId = configuration.agentId;
    executionEnvironment.deviceId = configuration.deviceId;
    executionEnvironment.settings = new TestDeviceSettings();
    this.testPlan.environments = [executionEnvironment];
    this.environment = configuration.environment;
    console.log("Execution Environment", this.testPlan, this.environment);
    this.dryExecutionForm = undefined;
    this.addFormControls();
  }

  openSavedConfigs() {
    this.configuration = new AdhocRunConfiguration().deserializeDryRunForm(this.dryExecutionForm.getRawValue());
    console.log(this.dryExecutionForm.getRawValue());
    this.configuration.workspaceType = this.version.workspace.workspaceType;
    let environment = this.dryExecutionForm.getRawValue().environments[0];
    if(this.isHybrid){
      this.agentService.findAll("id:"+ this.dryExecutionForm.getRawValue().environments[0].agentId).subscribe(res => {
        this.configuration.name = this.configuration.formattedHybridName(res.content[0].name, environment);
        this.openSaveConfigForm();
      });
    } else {
      this.configuration.name = this.configuration.formattedName(environment);
      this.openSaveConfigForm();
    }
  }

  openSaveConfigForm(){
    delete this.configuration.id;
    const dialogRef = this.matDialog.open(DryRunSavedConfigFormComponent, {
      width: '30%',
      panelClass: ['mat-overlay'],
      data: {
        configuration: this.configuration
      }
    });
    dialogRef.afterClosed()
      .subscribe((res: AdhocRunConfiguration) => {
        if (res) {
          this.configuration = res;
          this.fetchSavedConfigurations();
          this.setConfigurationId(this.configuration);
        }
      });
  }

  openSavedConfigsList() {
    let dialogRef = this.matDialog.open(DryRunSavedConfigListComponent, {
      width: '50vw',
      height: '60vh',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {
        application: this.version.workspace
      }
    });
    dialogRef.afterClosed().subscribe(res => {
      if (res) {
        this.fetchSavedConfigurations();
      }
    });
  }

  private normalizeFormValue() {
    const environment = new DryTestDevice().deserialize(this.dryExecutionForm.getRawValue()['environments'][0]);
    this.testPlan = new DryTestPlan().deserialize(this.dryExecutionForm.getRawValue());
    this.testPlan.workspaceVersionId = this.version.id;
    this.testPlan.testCaseId = this.testCase.id;
    environment.matchBrowserVersion = this.testPlan.matchBrowserVersion;
    this.testPlan.environments = [environment];
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

    if (this.testPlan.isHybrid){
      environment.platformBrowserVersionId=null;
      environment.platformDeviceId = null;
      environment.platformOsVersionId= null;
      environment.platformScreenResolutionId = null;
    }
    if (!this.testPlan.isHybrid) {
      environment.agentId = null;
      environment.deviceId = null;
      environment.browser = null;
    }
    if (this.dryExecutionForm.getRawValue()['environments'][0]) {
      let capabilities = [];
      this.dryExecutionForm.getRawValue()['environments'][0].capabilities.forEach(capability => {
        if (!!capability['name'] && !!capability['value'])
          capabilities.push(new Capability().deserialize(capability));
      })
      environment.capabilities = capabilities;
    }
  }

  private createExecution() {
    this.testPlan = new DryTestPlan();
    this.testPlan.name = "Dry run::"+ new Date();
    this.testPlan.elementTimeOut = 30;
    this.testPlan.pageTimeOut = 30;
    this.testPlan.screenshot = Screenshot.ALL_TYPES;
    this.testPlan.workspaceVersionId = this.version.id;
    this.testPlan.testPlanLabType = TestPlanLabType.TestsigmaLab;
    this.testPlan.testCaseId = this.testCase.id;
    let environment = new DryTestDevice();
    environment.settings = new TestDeviceSettings();
    this.testPlan.environments = [environment];
  }

  private fetchEmptyElements() {
    this.elementService.findEmptyElements( this.options.testCaseId ,this.version.id).subscribe(
      res => {
        res.content.forEach((element) => this.emptyElements.push(element.name));
      },
      err => {console.log("g")}
    )
  }

  private validateNavigationUrls() {
    this.testCaseService.validateNavigationUrls(this.options.testCaseId).subscribe(
      res => {
        res.forEach((url) => this.invalidUrls.push(url));
      },
      err => {}
    )
  }

  disableRunButton() {
    return this.saving || this.savingConfig || this.dryExecutionForm?.invalid || this.emptyElements.length>0
      || (!this.isHybrid &&  this.invalidUrls.length>0) || (this.isHybrid && this.zeroActiveAgents) ;
  }

  private searchDryRunConfigs(term: any) {
    this.filteredDryRunConfigs = this.configurations.filter(config => config.name.includes(term.trim()))
  }

  closeDialogTab(){
    this.dialogRef.close();
  }
}
