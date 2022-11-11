import {Component, Inject, OnInit} from '@angular/core';
import {Page} from "../../shared/models/page";
import {FormControl, FormGroup} from "@angular/forms";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../shared/components/base.component";
import {BackupVersionModel} from "../../settings/models/backup.version.model";
import {BackupService} from "../../settings/services/backup.service";
import {UserPreference} from "../../models/user-preference.model";
import {Router} from "@angular/router";
import {Workspace} from "../../models/workspace.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceService} from "../../services/workspace.service";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {ToastrService} from "ngx-toastr";
import {ImportGuideLinesWarningComponent} from "./import-guide-lines-warning.component";

@Component({
  selector: 'import-form-component',
  templateUrl: './import-form.component.html',
})
export class ImportFormComponent extends BaseComponent implements OnInit {
  public importModel: BackupVersionModel = new BackupVersionModel();
  public importForm: FormGroup;
  public userPreference: UserPreference;
  public uploadedFileObject;
  public fileName:String;
  public isSaving:boolean=false;
  public show: Boolean = false;
  public applications: Page<Workspace>;
  public versions: Page<WorkspaceVersion>;
  public workspaceSwitcherForm: FormGroup;
  public initialized: Boolean = false;
  public selectedWorkspace: Workspace;
  public selectedVersion: WorkspaceVersion;
  public skipEntityExists: Boolean=true;

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
    private importService: BackupService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    @Inject(MAT_DIALOG_DATA) public option: { filterId: Number, workspaceVersionId: Number },
    public dialogRef: MatDialogRef<ImportFormComponent>) {
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
    this.importForm = new FormGroup({
      isTestCaseEnabled: new FormControl(this.importModel.isTestCaseEnabled, []),
      isTestStepEnabled: new FormControl(this.importModel.isTestStepEnabled, []),
      isRestStepEnabled: new FormControl(this.importModel.isRestStepEnabled, []),
      isUploadsEnabled: new FormControl(this.importModel.isUploadsEnabled, []),
      isTestCasePriorityEnabled: new FormControl(this.importModel.isTestCasePriorityEnabled, []),
      isTestCaseTypeEnabled: new FormControl(this.importModel.isTestCaseTypeEnabled, []),
      isElementEnabled: new FormControl(this.importModel.isElementEnabled, []),
      isElementScreenNameEnabled: new FormControl(this.importModel.isElementScreenNameEnabled, []),
      isTestDataEnabled: new FormControl(this.importModel.isTestDataEnabled, []),
      isAttachmentEnabled: new FormControl(this.importModel.isAttachmentEnabled, []),
      isAgentEnabled: new FormControl(this.importModel.isAgentEnabled, []),
      isTestPlanEnabled: new FormControl(this.importModel.isTestPlanEnabled, []),
      isSuitesEnabled: new FormControl(this.importModel.isSuitesEnabled, []),
      isTestDeviceEnabled: new FormControl(this.importModel.isTestDeviceEnabled, []),
      isLabelEnabled: new FormControl(this.importModel.isLabelEnabled, [])
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

  public uploadedFile(event): void {
    this.uploadedFileObject = event.target.files ? event.target.files[0] : null;
    this.fileName = this.uploadedFileObject.name;
  }

  import() {
    if(!this.validateForm() || this.isSaving){
      return;
    }
    let description = this.translate.instant('import.warning.message')
    const dialogRef = this.matModal.open(ImportGuideLinesWarningComponent, {
      width: '568px',
      height: 'auto',
      data: {
        description: description
      },
      panelClass: ['mat-dialog', 'rds-none']
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result){
        this.importModel.deserialize(this.importForm.getRawValue());
        this.importModel.skipEntityExists = this.skipEntityExists;
        this.isSaving = true;
        this.importService.importXml(this.formData()).subscribe(
          () => {
            this.translate.get('import.initiated.success', {FieldName: 'Imports'})
              .subscribe(res => {
                this.showNotification(NotificationType.Success, res);
              });
            this.isSaving = false;
            this.dialogRef.close();
          },
          (exception) => {
            this.isSaving = false;
            this.translate.get('import.initiated.failure', {FieldName: 'Imports'})
              .subscribe(res => {
                this.showAPIError(exception, res);
              })
          });
      }
      }
    );
  }


  validateForm(){
    if(!this.uploadedFileObject){
      this.translate.get('import.initiated.failure.file.not.selected', {})
        .subscribe(res => {
          this.showNotification(NotificationType.Error, res);
        });
      return false;
    }

    for (let controlsKey in this.importForm.controls) {
      if(this.importForm.controls[controlsKey].value) {
        return true;
      }
    }
    this.translate.get('import.select.least.one', {FieldName: 'Imports'})
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
      if (this.importForm.controls['' + modelName]) {
        this.importForm.controls['' + modelName].setValue($event.checked);
        if ($event.checked)
          this.importForm.controls['' + modelName].disable();
        else
          this.importForm.controls['' + modelName].enable();
      }
    })
  }

  public formData(): FormData {
    let fileData:FormData = new FormData();
    if (this.option.filterId > 0) {
      this.importModel.filterId = this.option.filterId;
      this.importModel.workspaceVersionId = this.selectedVersion.id;
    } else {
      this.importModel.workspaceVersionId = this.selectedVersion.id;
    }

    fileData.append("file", this.uploadedFileObject || new File([""], ""));
    let importBlob:Blob = new Blob([JSON.stringify(this.importModel.serialize())], { type: "application/json"});
    fileData.append("request", importBlob);
    return fileData;
  }

  public removeUpload() {
    this.fileName = null;
    this.uploadedFileObject = null;
  }
}
