import {Component, Inject, OnInit} from '@angular/core';
import {AuthenticationConfig} from "../../../../models/authentication-config.model";
import {AuthenticationConfigService} from "../../../../services/authentication-config.service";
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AuthenticationType} from "../../../../shared/enums/authentication-type.enum";
import {ConfirmationModalComponent} from "../../../../shared/components/webcomponents/confirmation-modal.component";
import {BaseComponent} from "../../../../shared/components/base.component";

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styles: [
  ]
})
export class DetailsComponent extends BaseComponent implements OnInit {

  authConfig: AuthenticationConfig ;
  saving = false;
  sending=false;

  constructor(
    private authConfigService: AuthenticationConfigService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private dialogRef: MatDialogRef<DetailsComponent>,
    private matModal: MatDialog,
    @Inject(MAT_DIALOG_DATA) public options: { authConfig: AuthenticationConfig},) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.authConfig=this.options.authConfig;
  }

  delete(){

      this.translate.get("message.common.confirmation.message", {FieldName: 'API key'}).subscribe((res) => {
        const dialogRef = this.matModal.open(ConfirmationModalComponent, {
          width: '450px',
          data: {
            description: res
          },
          panelClass: ['matDialog', 'delete-confirm']
        });
        dialogRef.afterClosed().subscribe(result => {
          if (result) {
            this.update()
          }
        });
      })
  }

  update(){
    this.saving = true;
    this.authConfig.apiKey=null;
    this.authConfig.isApiEnabled = false;

    this.authConfigService.update(this.authConfig).subscribe({
        next: () => {
          this.saving = false;
          this.dialogRef.close(true);

          this.translate.get("message.common.deleted.success", {FieldName: 'API key'}).subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
          });
        },
        error: (error) => {
          this.saving = false;
          this.translate.get("message.common.deleted.failure", {FieldName: 'API key'}).subscribe((res: string) => {
            this.showAPIError(error, res);
          });
        }
      }
    );
  }

}
