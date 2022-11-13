import {Component, Inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../../../shared/components/base.component";
import {Integrations} from "../../../../shared/models/integrations.model";
import {IntegrationsService} from "../../../../shared/services/integrations.service";
import {Integration} from "../../../../shared/enums/integration.enum";

@Component({
  selector: 'app-xray',
  templateUrl: './create.component.html',
})
export class CreateComponent extends BaseComponent implements OnInit {
  updateForm: FormGroup;
  plug: Integrations = new Integrations();
  jiraPlug: Integrations = new Integrations();
  plugins: Integrations[] = [];
  saving = false;
  constructor(
    private integrationsService: IntegrationsService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    private router: Router,
    private dialogRef: MatDialogRef<CreateComponent>,
    @Inject(MAT_DIALOG_DATA) public options: { applicationId: number, name: string},) {
    super(authGuard, notificationsService, translate);
  }

  ngOnInit(): void {
    this.plug.workspaceId= this.options.applicationId;
    this.plug.name=this.options.name;
    this.updateForm = new FormGroup({
      'url' : new FormControl(null, Validators.required),
      'username': new FormControl(null,  Validators.required),
      'password':new FormControl(null, Validators.required),
      'jira_username': new FormControl(null,  Validators.required),
      'jira_token': new FormControl(null, Validators.required),
      'toggle_logs': new FormControl(false, [])
    })
    this.getJiraPlug();
  }

  getJiraPlug(){
    this.jiraPlug.workspaceId = (Object.keys(Integration).indexOf(Integration.Jira) + 1);
    this.integrationsService.findAll("workspaceId:"+this.jiraPlug.workspaceId).subscribe(
      res => {
        this.plugins = res;
        if(this.plugins.length>0) {
          this.jiraPlug = res[0];
          this.toggleFormFields();
        }else {
          this.toggleFormFields();
        }
      },
      error => {
        console.log("Jira not integrated");
      }
    )
  }

  toggleFormFields() {
    console.log(this.jiraPlug?.id, this.getToggle);
    if(!this.jiraPlug?.id && this.getToggle) {
      this.updateForm.controls['jira_username'].enable();
      this.updateForm.controls['jira_token'].enable();
    }
    else if(!this.jiraPlug?.id && !this.getToggle){
      this.updateForm.controls['jira_username'].disable();
      this.updateForm.controls['jira_token'].disable();
    } else{
      this.updateForm.controls['url'].setValue(this.jiraPlug.url);
      this.updateForm.controls['jira_username'].setValue(this.jiraPlug.username);
      this.updateForm.controls['jira_token'].setValue(this.jiraPlug.password);
      this.updateForm.controls['url'].disable();
      this.updateForm.controls['jira_username'].disable();
      this.updateForm.controls['jira_token'].disable();
    }
  }

  onSubmit(){
    this.saving = true;
    if(this.getToggle && !this.jiraPlug.id){
      this.enableJira();
    }
    else{
      this.updateForm.controls['url'].enable();
      this.enableXrayCloud();
    }
  }

  enableXrayCloud(){
    if(!this.getToggle || (this.getToggle && this.jiraPlug.id)) {
      this.plug.username = this.updateForm.value.username;
      this.plug.password = this.updateForm.value.password;
      this.plug.url = this.updateForm.value.url;
      this.integrationsService.testIntegration(this.plug).subscribe({
          next: (data) => {
            if (data['status_code'] == 200) {
              this.plug.token = data['api_token'];
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

            } else {
              this.toggleFormFields();
              this.saving = false;
              this.translate.get("message.common.plugin_integration_test.failure", {FieldName: this.plug.name}).subscribe((res: string) => {
                this.showAPIError(data, res);
              });
            }
          },
          error: (error) => {
            this.toggleFormFields();
            this.saving = false;
            this.translate.get("message.common.plugin_integration_test.failure", {FieldName: this.plug.name}).subscribe((res: string) => {
              this.showAPIError(error, res);
            });
          }
        }
      );
    }
  }

  enableJira(){
    this.jiraPlug = new Integrations()
    this.jiraPlug.workspaceId = (Object.keys(Integration).indexOf(Integration.Jira) + 1);
    this.jiraPlug.name = Integration.Jira;
    this.jiraPlug.username=this.updateForm.value.jira_username;
    this.jiraPlug.password=this.updateForm.value.jira_token;
    this.jiraPlug.url=this.updateForm.value.url;
    this.jiraPlug.token=this.updateForm.value.jira_token;
    this.integrationsService.testIntegration(this.jiraPlug).subscribe( {
        next: (data) => {
          if (data['status_code'] == 200) {
            this.integrationsService.create(this.jiraPlug).subscribe((jiraPlug: Integrations) => {
                this.jiraPlug = jiraPlug;
                this.enableXrayCloud();
              }
            );
          }
          else{
            this.saving = false;
            this.translate.get("message.common.plugin_integration_test.failure", {FieldName: this.jiraPlug.name}).subscribe((res: string) => {
              this.showAPIError(data, res);
            });
          }
        },
        error: (error) => {
          this.saving = false;
          this.translate.get("message.common.plugin_integration_test.failure", {FieldName: this.jiraPlug.name}).subscribe((res: string) => {
            this.showAPIError(error, res);
          });
        }
      }
    );
  }

  toggleSelection(){
    this.updateForm?.controls['toggle_logs']?.setValue(!this.getToggle);
    this.toggleFormFields();
  }

  get getToggle(){
    return this.updateForm?.controls['toggle_logs']?.value;
  }
}
