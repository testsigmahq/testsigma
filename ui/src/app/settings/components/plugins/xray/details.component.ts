import {Component, Inject, OnInit} from '@angular/core';
import {Integrations} from "../../../../shared/models/integrations.model";
import {IntegrationsService} from "../../../../shared/services/integrations.service";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../../../shared/components/base.component";
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

  fetchPlugin():any{
    this.integrationsService.find(this.workspaceId).subscribe(data => {
      this.plug=data;
    },error => { console.log(error)})
  }

  delete(){
    this.translate.get("message.common.confirmation.message", {FieldName: this.plug.name}).subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.markAsDeleted()
        }
      });
    })
  }

  markAsDeleted(){
    this.saving = true;
    this.integrationsService.delete(this.workspaceId).subscribe({
        next: () => {
          this.saving = false;
          this.fetchPlugin();
          this.dialogRef.close(true);
          this.translate.get("message.common.plugin_integration.deleted.success", {FieldName: this.plug.name}).subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
          });
        },
        error: (error) => {
          this.saving = false;
          this.translate.get("message.common.plugin_integration.deleted.failure", {FieldName:this.plug.name}).subscribe((res: string) => {
            this.showAPIError(error, res);
          });
        }
      }
    );
  }

  /*isWriteAccess(name) {
    let entityAccessList = this.authGuard.session.user.privileges.filter((access)=> access.entity.name != "test_group_report");
    return entityAccessList.filter(access => {
      return access.getAccessLevel(name) &&
        (AccessLevel[access.accessLevel] == AccessLevel.WRITE || AccessLevel[access.accessLevel] == AccessLevel.FULL_ACCESS)}
    ).length > 0;
  }*/

}
