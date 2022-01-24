import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {StorageConfigService} from "../../../../services/storage-config.service";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../../../shared/components/base.component";
import {AuthenticationConfig} from "../../../../models/authentication-config.model";
import {AuthenticationType} from "../../../../shared/enums/authentication-type.enum";
import {AuthenticationConfigService} from "../../../../services/authentication-config.service";

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styles: [
  ]
})
export class CreateComponent extends BaseComponent implements OnInit {

  updateForm: FormGroup;
  authConfig: AuthenticationConfig;
  saving = false;
  constructor(
    private authConfigService: AuthenticationConfigService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    private dialogRef: MatDialogRef<CreateComponent>,
    @Inject(MAT_DIALOG_DATA) public options: { authConfig:AuthenticationConfig},) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.authConfig = this.options.authConfig;
    const reg = '(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?';
    this.updateForm = new FormGroup({
      'username': new FormControl(null,  Validators.required),
      'password':new FormControl(null, Validators.required),
      'confirmPassword':new FormControl(null, Validators.required)

    })
  }

  onSubmit(){
    this.saving = true;
    this.authConfig.userName = this.updateForm.value.username;
    this.authConfig.password = this.updateForm.value.password;
    this.authConfig.authenticationType=AuthenticationType.FORM;

    this.authConfigService.update(this.authConfig).subscribe(
      (storage) => {
        this.translate.get('message.common.auth_config.success',).subscribe((res) => {
          this.showNotification(NotificationType.Success, res);
          this.dialogRef.close({storage,isCreated:true});
        })
        this.saving = false;
      }, error => {
        this.translate.get('message.common.update.failure', {FieldName: 'Authentication Config'}).subscribe((res) => {
          this.showAPIError(error, res);
          this.saving = false;
        })
      });

  }

}
