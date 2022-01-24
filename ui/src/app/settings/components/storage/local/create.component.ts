import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../../../shared/components/base.component";
import {StorageConfig} from "../../../models/storage-config";
import {StorageConfigService} from "../../../../services/storage-config.service";
import {StorageType} from "../../../../enums/storage-type.enum";

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styles: [
  ]
})
export class CreateComponent extends BaseComponent implements OnInit {

  updateForm: FormGroup;
  storageConfig: StorageConfig;
  saving = false;
  constructor(
    private storageService: StorageConfigService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    private dialogRef: MatDialogRef<CreateComponent>,
    @Inject(MAT_DIALOG_DATA) public options: { storageConfig:StorageConfig},) {
    super(authGuard, notificationsService, translate, toastrService);
  }


  ngOnInit(): void {
    this.storageConfig = this.options.storageConfig;
    const reg = '(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?';
    this.updateForm = new FormGroup({
      'rootDirectory': new FormControl(null,  Validators.required),
    })
  }

  onSubmit(){
    this.saving = true;
    this.storageConfig.onPremiseRootDirectory=this.updateForm.value.rootDirectory;
    this.storageConfig.storageType = StorageType.ON_PREMISE;
    this.storageService.update(this.storageConfig).subscribe(
      (storage) => {
        this.translate.get('message.common.storage_configuration.success', {FieldName: "Local Storage"}).subscribe((res) => {
          this.showNotification(NotificationType.Success, res);
          this.dialogRef.close({storage,isCreated:true});
        })
        this.saving = false;
      }, error => {
        this.translate.get('message.common.created.failure', {FieldName: 'Local Storage Config'}).subscribe((res) => {
          this.showAPIError(error, res);
          this.saving = false;
        })
      });
  }
}
