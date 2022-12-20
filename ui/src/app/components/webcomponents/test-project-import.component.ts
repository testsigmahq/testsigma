import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {BackupService} from "../../settings/services/backup.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-project-import',
  templateUrl: './test-project-import.component.html',

  styles: [
  ]
})
export class TestProjectImportComponent extends BaseComponent implements OnInit {

  @Output() onImportClick : EventEmitter<any > = new EventEmitter<any>();
  public formGroup: FormGroup;
  public uploadedFileObject : any;
  public importType : string = "YAML";
  constructor(
    public authGuard : AuthenticationGuard,
    public notificationsService : NotificationsService,
    public translate : TranslateService,
    private backupService : BackupService,
    public toastService :ToastrService,
  ) {
    super(authGuard, notificationsService, translate, toastService)
  }

  ngOnInit(): void {
    this.addFormControls();
    this.backupService.initiateTestProjectImport.subscribe(() => {
      this.import();
    })
  }

  ngOnDestroy(){
    delete this.backupService;
  }

  setUploadFileObject(event){
    this.uploadedFileObject = event;
  }

  addFormControls(){
    this.formGroup = new FormGroup({
      importType : new FormControl(this.importType,[]),
      gitRepoUrl : new FormControl(null, this.requiredIfValidator(() => this.isGitImport)),
      gitToken : new FormControl(null, this.requiredIfValidator(() => this.isGitImport)),
      uploadedFileObject : new FormControl(this.uploadedFileObject, this.requiredIfValidator(() => !this.isGitImport))
    })
  }

  get isGitImport(){
    return  this.formGroup.controls['importType']?.value == "GIT";
  }

  import(){
    let importData = {};
    importData['importType'] = this.formGroup?.controls?.importType?.value;
    importData['gitRepoUrl'] = this.formGroup?.controls?.gitRepoUrl?.value;
    importData['gitToken'] = this.formGroup?.controls?.gitToken?.value;
    let formData:FormData = new FormData();
    formData.append("file", this.uploadedFileObject || new File([""], ""));
    let importBlob:Blob = new Blob([JSON.stringify(importData)], { type: "application/json"});
    formData.append("request", importBlob);
    this.postData(formData);
  }

  postData(formData : FormData){
    this.onImportClick.emit({button: true});
    this.backupService.importFromTestProject(formData).subscribe(() => {
      this.onImportClick.emit({button:false, closeDialog : true});
      this.showNotification(NotificationType.Success, 'Imported Successfully, Please check Workspace versions')
    }, error => {
      this.onImportClick.emit({button: false});
      this.showNotification(NotificationType.Error, 'Error while importing from Test Project, Please contact Support')
      console.log("Error while Sending Request to Import from Test Project");
    })
  }

}
