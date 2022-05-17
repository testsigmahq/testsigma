import {Component, Inject, OnInit} from '@angular/core';
import {BaseComponent} from "../../../../shared/components/base.component";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {StorageType} from "../../../../enums/storage-type.enum";
import {StorageConfig} from "../../../models/storage-config";
import {StorageConfigService} from "../../../../services/storage-config.service";

@Component({
  selector: 'app-aws-storage',
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
    @Inject(MAT_DIALOG_DATA) public options: { event,storageConfig:StorageConfig},) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.storageConfig = this.options.storageConfig;
    const reg = '(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?';
    this.updateForm = new FormGroup({
      'bucketName': new FormControl(null,  Validators.required),
      'region':new FormControl(null, Validators.required),
      'accessKey':new FormControl(null, Validators.required),
      'secretKey': new FormControl(null, Validators.required),
      'endpoint': new FormControl(null, Validators.required)
    })
  }

  onSubmit(){
    this.saving = true;
    this.storageConfig.awsBucketName=this.updateForm.value.bucketName.trim();
    this.storageConfig.awsRegion=this.updateForm.value.region.trim();
    this.storageConfig.awsEndpoint=this.updateForm.value.endpoint.trim();
    this.storageConfig.awsAccessKey=this.updateForm.value.accessKey.trim();
    this.storageConfig.awsSecretKey = this.updateForm.value.secretKey.trim();
    this.storageConfig.storageType = StorageType.AWS_S3;

    this.storageService.update(this.storageConfig).subscribe(
      (storage) => {
        this.translate.get('message.common.storage_configuration.success', {FieldName: "AWS"}).subscribe((res) => {
          this.showNotification(NotificationType.Success, res);
          this.dialogRef.close(true);
        })
        this.saving = false;
    }, error => {
        this.translate.get('message.common.created.failure', {FieldName: 'AWS Storage Config'}).subscribe((res) => {
        this.showAPIError(error, res, 'AWS Storage Config Details');
        this.saving = false;
      })
    });

  }
}


