import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../../shared/components/base.component";
import {AuthenticationGuard} from "../../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from '@angular/router';
import {WorkspaceVersion} from "../../../models/workspace-version.model";
import {WorkspaceVersionService} from "../../../shared/services/workspace-version.service";
import {ConfirmationModalComponent} from "../../../shared/components/webcomponents/confirmation-modal.component";
import {MatDialog} from '@angular/material/dialog';
import {TestCaseService} from "../../../services/test-case.service";
import {CloneVersionComponent} from "../webcomponents/clone-version.component";

@Component({
  selector: 'app-version-details',
  templateUrl: './details.component.html',
  host: {'style': 'flex:1; width:calc(100% - 220px)', 'class': 'h-100'}
})
export class DetailsComponent extends BaseComponent implements OnInit {
  public totalTestCaseCount: number;
  public versionId: number;
  public version: WorkspaceVersion;
  public fullScreenDetails: Boolean;
  private versionCount: number;
  public showComments = false;
  public isDemo:Boolean = false;

  constructor(
    private dialog: MatDialog,
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    private versionService: WorkspaceVersionService,
    private testCaseService: TestCaseService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.route.parent.params.subscribe(res => {
      this.refreshComments();
      this.versionId = res.versionId || this.fullScreenDetails;
      this.fullScreenDetails = this.router.url.endsWith("/details");
      this.versionService.show(this.versionId).subscribe(res => {
        this.version = res;
        this.isDemo = res.workspace.isDemo;
        if(!this.fullScreenDetails) {
          this.router.navigate(['details'], {relativeTo: this.route})
        }
      });
      this.fetchTestCasesCount();
    });
  }

  fetchTestCasesCount() {
    this.testCaseService.findAll("workspaceVersionId:"+this.versionId).subscribe(res => this.totalTestCaseCount = res.totalElements)
  }

  deleteVersion(id) {
    let singleMandatoryVersion = this.versionCount == 1
    let msgKey = singleMandatoryVersion ? "version.delete.only_one.message" : "version.delete.confirmation.message"
    if (this.totalTestCaseCount > 0 && this.versionCount > 1) {
        msgKey = "version.delete.test_cases_exist.message";
    }

    this.translate.get(msgKey).subscribe((res) => {
      const dialogRef = this.dialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res,
          disabled: singleMandatoryVersion
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.destroyVersion(id);
        }
      })
    });
  }

  destroyVersion(id) {
    this.versionService.destroy(id).subscribe(
      () => {
        this.translate.get("message.common.deleted.success", {FieldName: 'Version'})
          .subscribe(res => this.showNotification(NotificationType.Success, res))
        let url;
        if(this.fullScreenDetails)
          url = ['/workspaces',this.version.workspace.id,'versions'];
        else
          url = ['/workspaces',this.version.workspace.id,'versions',this.version.workspaceId,'details'];
        this.router.navigate(url);
      },
      _err => {
        this.translate.get("message.common.deleted.failure", {FieldName: 'Version'})
          .subscribe(res => this.showNotification(NotificationType.Error, res))
      }
    );
  }

  deleteIfMoreThanOne(id) {
    this.versionService.findAll("workspaceId:" + this.version.workspaceId)
      .subscribe(res => {
        this.versionCount = res.content.length;
        this.deleteVersion(id)
      });
  }

  clone() {
    const dialogRef = this.dialog.open(CloneVersionComponent, {
      minWidth: '30rem',
      minHeight:'10rem',
      data: {
        version: this.version
      },
      panelClass: ['matDialog', 'delete-confirm']
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {

      }
    })
  }

  private refreshComments() {
    this.showComments = false;
    setTimeout(()=> this.showComments = true, 100);
  }
}
