import {Component, Inject, OnInit} from '@angular/core';
import {AuthenticationGuard} from "../../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {BaseComponent} from "../../../../shared/components/base.component";
import {Router} from "@angular/router";
import {ConfirmationModalComponent} from "../../../../shared/components/webcomponents/confirmation-modal.component";
import {StorageConfig} from "../../../models/storage-config";
import {StorageConfigService} from "../../../../services/storage-config.service";
import {StorageType} from "../../../../enums/storage-type.enum";

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styles: [
  ]
})
export class DetailsComponent extends BaseComponent implements OnInit {

  storageConfig: StorageConfig ;
  saving = false;
  sending=false;

  constructor(
    private storageService: StorageConfigService,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    private dialogRef: MatDialogRef<DetailsComponent>,
    private matModal: MatDialog,
    @Inject(MAT_DIALOG_DATA) public options: { storageConfig: StorageConfig},) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.storageConfig=this.options.storageConfig;
  }

  delete(){
    if(this.storageConfig.storageType == StorageType.AZURE_BLOB)
    {
      this.translate.get('message.common.cannot_remove_storage').subscribe((res) => {
        this.showNotification(NotificationType.Error, res);
      });
    }
    else{
      this.translate.get("message.common.confirmation.message", {FieldName: 'Azure Storage Config'}).subscribe((res) => {
        const dialogRef = this.matModal.open(ConfirmationModalComponent, {
          width: '450px',
          data: {
            description: res
          },
          panelClass: ['matDialog', 'delete-confirm']
        });
        dialogRef.afterClosed().subscribe(result => {
          if (result) {
            this.update();
          }
        });
      })
    }

  }

  update(){
    this.saving = true;
    this.storageConfig.azureContainerName=null;
    this.storageConfig.azureConnectionString = null;

    this.storageService.update(this.storageConfig).subscribe({
        next: () => {
          this.saving = false;
          this.dialogRef.close(true);

          this.translate.get("message.common.deleted.success", {FieldName: 'Azure Config Details'}).subscribe((res: string) => {
            this.showNotification(NotificationType.Success, res);
          });
        },
        error: (error) => {
          this.saving = false;
          this.translate.get("message.common.deleted.failure", {FieldName: 'Azure Config Details'}).subscribe((res: string) => {
            this.showAPIError(error, res,'Azure Storage Config Details' );
          });
        }
      }
    );
  }

}
