import {Component, OnInit} from '@angular/core';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {BackupService} from "../services/backup.service";
import {Page} from "../../shared/models/page";
import {Backup} from "../models/backup.model";
import {BaseComponent} from "../../shared/components/base.component";
import {Pageable} from "../../shared/models/pageable";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import {BackupFormComponent} from "../../components/webcomponents/backup-form.component";
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-backups',
  templateUrl: './backups.component.html',
  host: {'class': 'page-content-container'},
  styles: []
})
export class BackupsComponent extends BaseComponent implements OnInit {
  public isFetchCompleted: Boolean;
  public backups: Page<Backup>;
  public currentPage: Pageable;
  isFiltered: boolean
  public  hasIncomplete: Backup;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public autoRefreshSubscription: Subscription;
  constructor(
    public route: ActivatedRoute,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private backupService: BackupService,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.fetchBackups();
    this.pushToParent(this.route, this.route.params);
  }

  fetchBackups() {
    this.backupService.findAll(this.currentPage).subscribe(res => {
      this.backups = res;
      this.hasIncomplete = res.content.find(item => item.isInProgress || item.isFailed)
      this.currentPage = res.pageable;
      this.isFetchCompleted = true;
    })
  }

  delete(backup: Backup) {
    this.translate.get("settings.backup.delete_confirm").subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            this.destroy(backup);
          }
        });
    })
  }

  openBackupForm() {
    let matDialogRef = this.matDialog.open(BackupFormComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      width: '60%',
      height: '63%',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {}
    });
    const matDialogConfig = new MatDialogConfig();
    matDialogRef.updatePosition(matDialogConfig.position);
    matDialogRef.afterClosed().subscribe(res => {
      this.fetchBackups();
    })
  }

  destroy(backup) {
    this.backupService.delete(backup.id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: 'Backup'})
          .subscribe(res => {
            this.showNotification(NotificationType.Success, res);
          });
        this.fetchBackups();
      },
      (exception) => {
        this.translate.get('message.common.deleted.failure', {FieldName: 'Backup'})
          .subscribe(res => {
            this.showAPIError(exception, res);
          })
      }
    );
  }

}
