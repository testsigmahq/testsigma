import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationConfig} from "../../../../models/authentication-config.model";
import {AuthenticationConfigService} from "../../../../services/authentication-config.service";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {AuthenticationType} from "../../../../shared/enums/authentication-type.enum";
import {BaseComponent} from "../../../../shared/components/base.component";

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styles: [
  ]
})
export class FormComponent extends BaseComponent implements OnInit {

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
    private dialogRef: MatDialogRef<FormComponent>,
    @Inject(MAT_DIALOG_DATA) public options: { authConfig:AuthenticationConfig},) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.authConfig = this.options.authConfig;
    const reg = '(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?';
    this.updateForm = new FormGroup({
      'secret': new FormControl(null,  Validators.required),
      'confirmSecret':new FormControl(null, Validators.required)
    })
  }

  onSubmit(){
    this.saving = true;
    this.authConfig.jwtSecret = this.updateForm.value.secret;

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
