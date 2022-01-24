import {Component, Inject, OnInit} from '@angular/core';
import {BaseComponent} from "../../../../shared/components/base.component";
import {Integrations} from "../../../../shared/models/integrations.model";
import {IntegrationsService} from "../../../../shared/services/integrations.service";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ConfirmationModalComponent} from "../../../../shared/components/webcomponents/confirmation-modal.component";

@Component({
  selector: 'app-view',
  templateUrl: './details.component.html',
})
export class DetailsComponent extends BaseComponent implements OnInit  {
  plug: Integrations ;
  workspaceId: number;
  saving = false;
  sending=false;

  constructor(
    private integrationsService: IntegrationsService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    private dialogRef: MatDialogRef<DetailsComponent>,
    private matModal: MatDialog,
    @Inject(MAT_DIALOG_DATA) public options: { workspaceId: number},) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.workspaceId=this.options.workspaceId;
    this.fetchPlugin();
  }

  fetchPlugin(){
    this.integrationsService.find(this.workspaceId).subscribe(data => {
      this.plug=data;
    },error => { console.log(error)})
  }

  delete(){
    this.translate.get("message.common.confirmation.message", {FieldName: 'Jira Integration'}).subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.destroy()
        }
      });
    })
  }

  destroy(){
    this.saving = true;
    this.integrationsService.delete(this.workspaceId).subscribe({
        next: () => {
          this.saving = false;
          this.dialogRef.close(true);
          this.translate.get("message.common.plugin_integration.deleted.success", {FieldName: 'Jira'}).subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
          });
        },
        error: (error) => {
          this.saving = false;
          this.translate.get("message.common.plugin_integration.deleted.failure", {FieldName: 'Jira'}).subscribe((res: string) => {
            this.showAPIError(error, res);
          });
        }
      }
    );
  }
}

