import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Integrations} from "../../../../shared/models/integrations.model";
import {IntegrationsService} from "../../../../shared/services/integrations.service";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../../../shared/components/base.component";

@Component({
  selector: 'app-jira',
  templateUrl: './create.component.html',
})
export class CreateComponent extends BaseComponent implements OnInit {
  updateForm: FormGroup;
  plug: Integrations = new Integrations();
  saving = false;
  constructor(
    private integrationsService: IntegrationsService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    private dialogRef: MatDialogRef<CreateComponent>,
    @Inject(MAT_DIALOG_DATA) public options: { workspaceId: number, name: string},) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.plug.workspaceId= this.options.workspaceId;
    this.plug.name=this.options.name;
    const reg = '(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?';
    this.updateForm = new FormGroup({
      'url': new FormControl(null,  [Validators.required, Validators.pattern(reg)]),
      'token':new FormControl(null, Validators.required),
      'username':new FormControl(null, Validators.required),
      'confirmToken': new FormControl(null, Validators.required)
    })
  }

  onSubmit(){
    this.saving = true;
    this.plug.username=this.updateForm.value.username;
    this.plug.password=this.updateForm.value.token;
    this.plug.url=this.updateForm.value.url;
    this.plug.token=this.updateForm.value.token;
    this.integrationsService.testIntegration(this.plug).subscribe( {
        next: (data) => {
          if (data['status_code'] == 200) {

            this.integrationsService.create(this.plug).subscribe((element: Integrations) => {
                this.saving = false;
                this.translate.get('message.common.plugin_integration_configuration.success', {FieldName: this.plug.name}).subscribe((res) => {
                  this.showNotification(NotificationType.Success, res);
                  this.dialogRef.close(element);
                })
              }, error => {
                this.saving = false;
                this.translate.get('message.common.plugin_integration_configuration.failure', {FieldName: this.plug.name}).subscribe((res) => {
                  this.showNotification(NotificationType.Error, res);
                })
              }
            );

          }

          else{
            console.log(data);
            this.saving = false;
            this.translate.get("message.common.plugin_integration_test.failure", {FieldName: this.plug.name}).subscribe((res: string) => {
              this.showAPIError(data, res);
            });
          }
        },
        error: (error) => {
          this.saving = false;
          this.translate.get("message.common.plugin_integration_test.failure", {FieldName: this.plug.name}).subscribe((res: string) => {
            this.showAPIError(error, res);
          });
        }
      }
    );

  }
}

