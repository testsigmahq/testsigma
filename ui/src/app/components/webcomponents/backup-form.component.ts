import {Component, Inject, OnInit} from '@angular/core';
import {Page} from "../../shared/models/page";
import {FormControl, FormGroup} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../shared/components/base.component";
import {BackupVersionModel} from "../../settings/models/backup.version.model";
import {BackupService} from "../../settings/services/backup.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {UserPreference} from "../../models/user-preference.model";
import {Workspace} from "../../models/workspace.model";
import {Router} from "@angular/router";
import {WorkspaceService} from "../../services/workspace.service";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";

@Component({
  selector: 'back-up-form-component',
  templateUrl: './backup-form.component.html',
})
export class BackupFormComponent extends BaseComponent implements OnInit {
  public backupModel: BackupVersionModel = new BackupVersionModel();
  public backupForm: FormGroup;
  public userPreference: UserPreference;
  public isSaving:boolean=false;
  public show: Boolean = false;
  public applications: Page<Workspace>;
  public versions: Page<WorkspaceVersion>;
  public workspaceSwitcherForm: FormGroup;
  public initialized: Boolean = false;
  public selectedWorkspace: Workspace;
  public selectedVersion: WorkspaceVersion;
  public dependencies: string = '{"isTestCaseEnabled":["isTestStepEnabled", "isRestStepEnabled", "isTestDataEnabled", "isTestCasePriorityEnabled", "isTestCaseTypeEnabled", "isElementEnabled", "isElementScreenNameEnabled"],' +
    '"isTestStepEnabled" : [ "isRestStepEnabled", "isTestDataEnabled", "isTestCasePriorityEnabled", "isTestCaseTypeEnabled", "isElementEnabled", "isElementScreenNameEnabled"],' +
    '"isRequirementEnabled" : ["isTestCaseEnabled", "isTestStepEnabled", "isRestStepEnabled", "isTestDataEnabled", "isTestCasePriorityEnabled", "isTestCaseTypeEnabled", "isElementEnabled", "isElementScreenNameEnabled"],' +
    '"isTestPlanEnabled":["isTestCaseEnabled", "isTestDeviceEnabled", "isSuitesEnabled", "isUploadsEnabled", "isAgentEnabled", "isTestStepEnabled", "isRestStepEnabled", "isTestDataEnabled", "isTestCasePriorityEnabled", "isTestCaseTypeEnabled", "isElementEnabled", "isElementScreenNameEnabled"],' +
    '"isSuitesEnabled":["isRestStepEnabled", "isTestStepEnabled", "isTestDataEnabled", "isTestCasePriorityEnabled", "isTestCaseTypeEnabled", "isElementEnabled", "isElementScreenNameEnabled", "isTestCaseEnabled", "isTestCaseTypeEnabled"],'+
    '"isTestDeviceEnabled":["isSuitesEnabled", "isAgentEnabled", "isUploadsEnabled", "isTestCaseEnabled", "isTestStepEnabled", "isRestStepEnabled", "isTestDataEnabled", "isTestCasePriorityEnabled", "isTestCaseTypeEnabled", "isElementEnabled", "isElementScreenNameEnabled"],' +
    '"isElementEnabled":["isElementScreenNameEnabled"]}';

  constructor(
    private router: Router,
    private workspaceService: WorkspaceService,
    private versionService: WorkspaceVersionService,
    private matModal: MatDialog,
    private backupService: BackupService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    @Inject(MAT_DIALOG_DATA) public option: { filterId: Number, workspaceVersionId: Number },
    public dialogRef: MatDialogRef<BackupFormComponent>) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.fetchApplications();
    this.addControllers()
  }

  addControllers() {
    this.workspaceSwitcherForm = new FormGroup({
      application: new FormControl(this.selectedWorkspace, []),
      version: new FormControl(this.selectedVersion, [])
    });
    this.backupForm = new FormGroup({
      isTestCaseEnabled: new FormControl(this.backupModel.isTestCaseEnabled, []),
      isTestStepEnabled: new FormControl(this.backupModel.isTestStepEnabled, []),
      isRestStepEnabled: new FormControl(this.backupModel.isRestStepEnabled, []),
      isUploadsEnabled: new FormControl(this.backupModel.isUploadsEnabled, []),
      isTestCasePriorityEnabled: new FormControl(this.backupModel.isTestCasePriorityEnabled, []),
      isTestCaseTypeEnabled: new FormControl(this.backupModel.isTestCaseTypeEnabled, []),
      isElementEnabled: new FormControl(this.backupModel.isElementEnabled, []),
      isElementScreenNameEnabled: new FormControl(this.backupModel.isElementScreenNameEnabled, []),
      isTestDataEnabled: new FormControl(this.backupModel.isTestDataEnabled, []),
      isAttachmentEnabled: new FormControl(this.backupModel.isAttachmentEnabled, []),
      isAgentEnabled: new FormControl(this.backupModel.isAgentEnabled, []),
      isTestPlanEnabled: new FormControl(this.backupModel.isTestPlanEnabled, []),
      isSuitesEnabled: new FormControl(this.backupModel.isSuitesEnabled, []),
      isTestDeviceEnabled: new FormControl(this.backupModel.isTestDeviceEnabled, []),
      isLabelEnabled: new FormControl(this.backupModel.isLabelEnabled, [])
    });
  }

  fetchApplications(term?: string) {
    let query = "";
    query += term ? "name:*" + term + "*" : '';
    this.workspaceService.findAll(query).subscribe(res => {
      this.applications = res;
      this.selectedWorkspace = this.applications.content[0];
      this.fetchVersions();
    })
  }

  fetchVersions(term?: string) {
    let query = "";
    query += term ? ",name:*" + term + "*" : '';
    this.versionService.findAll("applicationId:" + this.selectedWorkspace.id + query).subscribe(res => {
      this.versions = res;
      this.selectedVersion = this.versions.content[0];
      console.log(this.userPreference);
    })
  }

  setSelectedApplication(application: any) {
    this.selectedWorkspace = null;
    if (application) {
      this.selectedWorkspace = application;
      console.log("Setting selected application as  - ", application);
      this.fetchVersions()
    }
  }

  setSelectedVersion(version: any) {
    if (version) {
      console.log("Setting selected version as - ", version);
      this.selectedVersion = version;
    }
  }

  backup() {
    if(!this.validateForm() || this.isSaving){
      return;
    }
    this.isSaving = true;
    this.backupModel.deserialize(this.backupForm.getRawValue());
    if (this.option.filterId > 0) {
      this.backupModel.filterId = this.option.filterId;
      this.backupModel.workspaceVersionId = this.option.workspaceVersionId;
    } else {
      this.backupModel.workspaceVersionId = this.selectedVersion.id;
    }
    this.backupService.create(this.backupModel).subscribe(
      () => {
        this.translate.get('backup.initiated.success')
          .subscribe(res => {
            this.showNotification(NotificationType.Success, res);
          });
        this.isSaving = false;
        this.dialogRef.close();
      },
      (exception) => {
        this.isSaving = false;
        this.translate.get('backup.initiated.failure')
          .subscribe(res => {
            this.showAPIError(exception, res);
          })
      });
  }
  validateForm(){
    for (let controlsKey in this.backupForm.controls) {
      if(this.backupForm.controls[controlsKey].value) {
        return true;
      }
    }
    this.translate.get('backup.select.least.one')
      .subscribe(res => {
        this.showAPIError(new Error(res), res);
      });
    return false;
  }
  checkDependencies($event, model) {
    let dependenciesList: String[] = JSON.parse(this.dependencies)[model];
    if(!dependenciesList){
      return;
    }
    dependenciesList.forEach(modelName => {
      if (this.backupForm.controls['' + modelName]) {
        this.backupForm.controls['' + modelName].setValue($event.checked);
        if ($event.checked)
          this.backupForm.controls['' + modelName].disable();
        else
          this.backupForm.controls['' + modelName].enable();
      }
    })
  }
}
