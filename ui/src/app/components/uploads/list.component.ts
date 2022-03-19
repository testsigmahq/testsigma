import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {Page} from "../../shared/models/page";
import {Pageable} from "../../shared/models/pageable";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";

import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {OverlayContainer} from '@angular/cdk/overlay';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute, Params} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {Upload} from "../../shared/models/upload.model";
import {UploadService} from "../../shared/services/upload.service";
import {MatSnackBar} from '@angular/material/snack-bar';
import {UploadType} from "../../shared/enums/upload-type.enum";
import {UploadsFormComponent} from "../../shared/components/webcomponents/uploads-form.component";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {UploadEntitiesModalComponent} from "../../shared/components/webcomponents/upload-entities-modal.component";
import {TestDeviceService} from "../../services/test-device.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-uploads',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
})
export class ListComponent extends BaseComponent implements OnInit {
  public uploads: Page<Upload>;
  public currentPage: Pageable = new Pageable();
  public versionId: number;
  public version: WorkspaceVersion;
  public defaultQuery: string;
  public sortByColumns = ['name', 'createdDate', 'updatedDate'];
  public sortedBy = this.sortByColumns[0];
  public direction: string = ",asc";
  public selectAll: Boolean = false;
  public selectedUploads = [];
  public searchQuery = "";
  public fetchingCompleted: Boolean = false;
  public isFiltered: Boolean = false;
  public uploadType = UploadType;


  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private uploadService: UploadService,
    private matDialog: MatDialog,
    private workspaceVersionService: WorkspaceVersionService,
    private _snackBar: MatSnackBar,
    private _appOverlayContainer: OverlayContainer,
    public environmentService: TestDeviceService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get hideHeaderToolBar() {
    return (this.selectedUploads.length ||
      (!this.uploads?.content.length && !this.searchQuery))
  };

  ngOnInit(): void {
    this.route.parent.params.subscribe((params: Params) => {
      this.versionId = params.versionId;
      this.pushToParent(this.route, params);
      this.workspaceVersionService.show(this.versionId).subscribe((res) => {
        this.version = res;
        this.defaultQuery = "workspaceId:" + this.version.workspace.id;
        this.fetchUploads();
      })
    });
  }

  fetchUploads() {
    this.fetchingCompleted = false;
    this.currentPage.pageSize = 10;
    let sortBy = this.sortedBy + this.direction;
    let query = this.defaultQuery + this.searchQuery;
    this.uploadService.findAll(query, sortBy, this.currentPage)
      .subscribe((res: Page<Upload>) => {
        this.fetchingCompleted = true;
        this.goToPreviousPageIfEmpty(res);
        this.uploads = res;
        this.currentPage = res.pageable;
        this.selectAllToggle(false);
      });
  }

  sortBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.fetchUploads()
  }

  selectAllToggle(selectAll: Boolean) {
    let testDataProfileIds = [];
    this.uploads.content.find((testData, i) => {
      this.uploads.content[i].selected = selectAll;
      testDataProfileIds.push(this.uploads.content[i].id);
    })
    this.selectedUploads = selectAll ? testDataProfileIds : [];
  }

  setSelectedList(id: number, checked: Boolean) {
    if (checked)
      this.selectedUploads.push(id)
    else {
      this.selectedUploads.splice(this.selectedUploads.indexOf(id), 1);
    }
  }

  checkForLinkedEnvironments(id) {
    let environmentResults: InfiniteScrollableDataSource;
    environmentResults = new InfiniteScrollableDataSource(this.environmentService, "appUploadId@"+(id ? id : this.selectedUploads.join("#"))+",entityType:TEST_PLAN");
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (environmentResults.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (environmentResults.isEmpty)
          _this.openDeleteDialog(id);
        else
          _this.openLinkedUploadsDialog(environmentResults);
      }
    }
  }

  private openLinkedUploadsDialog(list) {
    this.translate.get("message.delete.uploads").subscribe((res) => {
      this.matDialog.open(UploadEntitiesModalComponent, {
        width: '568px',
        height: 'auto',
        data: {
          description: res,
          linkedEntityList: list,
        },
        panelClass: ['mat-dialog', 'rds-none']
      });
    });
  }

  openDeleteDialog(id?) {
    let message = id ? "message.common.confirmation.default" : "uploads.bulk_delete.confirmation.message";
    this.translate.get(message, {FieldName: this.selectedUploads.length}).subscribe((res) => {
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
            if (!id) {
              this.multipleDelete();
            } else {
              this.destroyUpload(id)
            }
          }

        });
    })
  }

  openSaveUploadForm(upload?: Upload) {
    const dialogRef = this.matDialog.open(UploadsFormComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      width: '350px',
      position: {top: '63px', right: '40px', bottom: '0'},
      panelClass: ['mat-overlay'],
      disableClose: true,
      data: {
        version: this.version,
        upload: upload? new Upload().deserialize(upload.serialize()) : null
      }
    });
    dialogRef.afterClosed()
      .subscribe((res) => {
        if (res)
          this.fetchUploads();
      });
  }

  showCopyTooltip(upload: Upload) {
    upload.filePathCopied = true;
    setTimeout(() => upload.filePathCopied = false, 10000);
  }

  search(term: string) {
    if (term) {
      this.isFiltered = true;
      this.searchQuery = ",name:*" + term + "*";
    } else {
      this.isFiltered = false;
      this.searchQuery = "";
    }
    this.fetchUploads()
  }

  private destroyUpload(id: any) {
    this.uploadService.destroy(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Upload"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchUploads();
        this.selectedUploads = [];
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: "Upload"})
        .subscribe(res => this.showNotification(NotificationType.Error, res))
    );
  }

  private multipleDelete() {
    this.uploadService.bulkDestroy(this.selectedUploads).subscribe(
      () => {
        this.translate.get("message.common.deleted.success", {FieldName: "Uploads"}).subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchUploads();
        this.selectedUploads = []
      },
      (err) => this.translate.get("message.common.deleted.failure", {FieldName: "Uploads"}).subscribe(res => this.showAPIError(NotificationType.Error, res,"Uploads","Test Case")))
  }

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0) {
      this.currentPage.pageNumber--;
      this.fetchUploads();
      return;
    }
  }
}

