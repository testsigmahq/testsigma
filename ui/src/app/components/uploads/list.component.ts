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
import {UploadVersionService} from "../../shared/services/upload-version.service";
import {TestCaseService} from "../../services/test-case.service";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";

@Component({
  selector: 'app-uploads',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
})
export class ListComponent extends BaseComponent implements OnInit {
  public uploads: InfiniteScrollableDataSource;
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
    private uploadVersionService: UploadVersionService,
    private matDialog: MatDialog,
    private workspaceVersionService: WorkspaceVersionService,
    private _snackBar: MatSnackBar,
    private _appOverlayContainer: OverlayContainer,

    private testCaseService :TestCaseService,

    public environmentService: TestDeviceService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get hideHeaderToolBar() {
    return (this.selectedUploads.length ||
      (!this.uploads?.cachedItems.length && !this.searchQuery))
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

  waitTillRequestRespondsForUploads() {
    if (this.uploads?.isFetching)
      setTimeout(() => this.waitTillRequestRespondsForUploads(), 100);
    else {
      this.fetchingCompleted = true;
      this.selectAllToggle(false);
      this.setLatestVersions();
    }
  }

  setLatestVersions() {
    let latestVersionId = [];
    this.uploads.cachedItems.forEach((data: Upload) => {
      if (data.latestVersionId)
        latestVersionId.push(data.latestVersionId)
    })
    if (latestVersionId.length == 0)
      return;
    this.uploadVersionService.findAll("id@" + latestVersionId.join("#")).subscribe(res => {
      res.content.forEach(version => {
        this.uploads.cachedItems.find((upload: Upload, i) => {
          if (upload.latestVersionId == version.id) {
            (<Upload>this.uploads.cachedItems[i]).latestVersion = version;
          }
        })
      })
    });
  }

  fetchUploads() {
    this.fetchingCompleted = false;
    this.currentPage.pageSize = 10;
    let sortBy = this.sortedBy + this.direction;
    let query = this.defaultQuery + this.searchQuery;
    this.uploads = new InfiniteScrollableDataSource(this.uploadService, query, sortBy, this.currentPage.pageNumber);
    this.waitTillRequestRespondsForUploads();
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
    this.uploads.cachedItems.find((testData: Upload, i) => {
      (<Upload>this.uploads.cachedItems[i]).selected = selectAll;
      testDataProfileIds.push((<Upload>this.uploads.cachedItems[i]).id);
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

  checkForLinkedTestCases(testData?,id?) {
    let testCases: InfiniteScrollableDataSource;
    let query = "workspaceVersionId:" + this.versionId + ",testData:" + encodeURI(testData?.join("#"))
    query = this.byPassSpecialCharacters(query);
    testCases = new InfiniteScrollableDataSource(this.testCaseService,query);

    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testCases.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testCases.isEmpty)
          _this.openDeleteDialog(id);
        else
          _this.openLinkedTestCasesDialog(testCases);
      }
    }
  }
  private openLinkedTestCasesDialog(list) {
    this.translate.get("uploads.linked.with.test_cases").subscribe((res) => {
      this.matDialog.open(LinkedEntitiesModalComponent, {
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



  checkForLinkedEntities(id, name?: string) {
    this.checkForLinkedEnvironments(id, name);
  }
  checkForLinkedEnvironments(id, name?: string) {
    let environmentResults: InfiniteScrollableDataSource;
    environmentResults = new InfiniteScrollableDataSource(this.environmentService, "appUploadId@"+(id ? id : this.selectedUploads.join("#"))+",entityType:TEST_PLAN");
    waitTillRequestResponds(environmentResults, id, name);
    let _this = this;

    function waitTillRequestResponds(environmentResults: InfiniteScrollableDataSource, id, name?: string) {
      if (environmentResults.isFetching)
        setTimeout(() => waitTillRequestResponds(environmentResults, id, name), 100);
      else {
        if (environmentResults.isEmpty)
          _this.uploadVersionService.findAll("uploadId:" + id).subscribe(res => {
            let uploadPaths = res.content.map(version =>"testsigma-storage:/" + version.path)
            _this.checkForLinkedTestCases(uploadPaths,id);
          })
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

  openDeleteDialog(id?, name?: string) {
    let message = id ? "message.common.confirmation.default" : "uploads.bulk_delete.confirmation.message";
    this.translate.get(message, {FieldName: this.selectedUploads.length}).subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res,
          isPermanentDelete: true,
          title: name? 'Upload' : 'Uploads',
          item: 'upload',
          name: name ? name : 'multiple uploads',
          note: this.translate.instant('message.common.confirmation.upload_des', {Item:'upload'})
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
        this.translate.get('message.common.deleted.success', {FieldName: "Upload"}).subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchUploads();
        this.selectedUploads = [];
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: "Upload"})
        .subscribe(res => this.showNotification(NotificationType.Error, res))
    );
  }

  private multipleDelete() {
    this.uploadService.bulkDestroy(this.selectedUploads, this.versionId).subscribe(
      () => {
        this.translate.get("message.common.deleted.success", {FieldName: "Uploads"}).subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchUploads();
        this.selectedUploads = []
      },
      (err) => {
        this.translate.get("message.common.uploads.deleted.failure", {FieldName: "Uploads"}).subscribe(res => this.showAPIError(NotificationType.Error, res,"Uploads","Test Case"));
        this.fetchUploads();
      })
  }

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0) {
      this.currentPage.pageNumber--;
      this.fetchUploads();
      return;
    }
  }
}

