import {Component, OnInit} from '@angular/core';
import {TestDataService} from "../../services/test-data.service";
import {Page} from "../../shared/models/page";
import {TestData} from "../../models/test-data.model";
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Pageable} from "../../shared/models/pageable";

import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {TestCaseService} from "../../services/test-case.service";
import {TestCase} from "../../models/test-case.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-data-profile',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
})
export class ListComponent extends BaseComponent implements OnInit {
  public dataProfiles: Page<TestData>;
  public currentPage: Pageable = new Pageable();
  public versionId: number;
  public searchQuery: string;
  public defaultQuery: string;
  public sortByColumns = ['testDataName', 'createdDate', 'updatedDate'];
  public sortedBy: string = 'testDataName';
  public direction: string = ",asc";
  public selectAll: Boolean = false;
  public selectedDataProfiles = [];
  public fetchingCompleted: Boolean = false;
  public isFiltered: Boolean = false;
  public filteredByEnumList: string[] = ['all', 'used', 'unused'];
  public filteredByValue: string = 'all';
  public filteredByValueString: string;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private testDataService: TestDataService,
    private matDialog: MatDialog,
    private testCaseService: TestCaseService,
    private router: Router) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get hideHeaderToolBar() {
    return (this.selectedDataProfiles.length ||
      (!this.dataProfiles?.content.length && !this.isFiltered))
  };

  ngOnInit(): void {
    this.route.parent.params.subscribe((params: Params) => {
      this.versionId = params.versionId;
      this.pushToParent(this.route, params);
      this.defaultQuery = "versionId:" + this.versionId;
      this.fetchDataProfiles();
    });
  }

  fetchDataProfiles(term?: string) {
    this.fetchingCompleted = false;
    this.currentPage.pageSize = 10;
    let sortBy = this.sortedBy + this.direction;
    let query = this.defaultQuery;
    if (term) {
      this.isFiltered = true;
      this.searchQuery = ",testDataName:*" + term + "*";
      query += ",testDataName:*" + term + "*";
    } else this.searchQuery = undefined;
    if(this.filteredByValue!==undefined) {
      query += ',isMapped:'+this.filteredByValue;
    }
    this.testDataService.findAll(query, sortBy, this.currentPage)
      .subscribe((res: Page<TestData>) => {
        this.fetchingCompleted = true;
        this.goToPreviousPageIfEmpty(res);
        this.dataProfiles = res;
        this.currentPage = res.pageable;
        this.selectAllToggle(false);
      });
  }


  filter(event) {
    this.filteredByValue = event.filterBy;
    this.filteredByValueString = event.filterByStr
    this.fetchDataProfiles();
  }

  sortBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.fetchDataProfiles(this.searchQuery)
  }

  selectAllToggle(selectAll: Boolean) {
    let testDataProfileIds = [];
    this.dataProfiles.content.find((testData, i) => {
      this.dataProfiles.content[i].isSelected = selectAll;
      testDataProfileIds.push(this.dataProfiles.content[i].id);
    })
    this.selectedDataProfiles = selectAll ? testDataProfileIds : [];
  }

  setSelectedList(id: number, checked: Boolean) {
    if (checked)
      this.selectedDataProfiles.push(id)
    else {
      this.selectedDataProfiles.splice(this.selectedDataProfiles.indexOf(id), 1);
    }
  }

  openDeleteDialog(id) {
    let message = id ? "message.common.confirmation.default" : "test_data_profiles.bulk_delete.confirmation.message";
    this.translate.get(message, {selectedDataProfiles: this.selectedDataProfiles.length}).subscribe((res) => {
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
            if (id)
              this.destroyDataProfile(id);
            else
              this.multipleDelete()
          }
        });
    })
  }

  private destroyDataProfile(id: any) {
    this.testDataService.delete(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Test Data Profile"})
          .subscribe(res => {
            this.showNotification(NotificationType.Success, res);
          });
        this.fetchDataProfiles();
        this.selectedDataProfiles = [];
      },
      (err) => {
        this.translate.get('message.common.deleted.failure', {FieldName: "Test Data Profile"})
          .subscribe(msg => {
            this.showAPIError(err, msg);
          })
      }
    );
  }

  public fetchLinkedCases(id) {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, "workspaceVersionId:" + this.versionId + ",deleted:false,testDataId:" + id);
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

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0) {
      this.currentPage.pageNumber--;
      this.fetchDataProfiles();
      return;
    }
  }

  private multipleDelete() {
    this.testDataService.bulkDestroy(this.selectedDataProfiles).subscribe(
      () => {
        this.translate.get("message.common.deleted.success", {FieldName: "Test Data Profiles"}).subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchDataProfiles();
        this.selectedDataProfiles = []
      },
      (err) => {
        this.translate.get("message.common.deleted.failure", {FieldName: "Test Data Profiles"}).subscribe(msg => this.showAPIError(err, msg));
        this.fetchDataProfiles();
        this.selectedDataProfiles = [];
      })
  }

  private openLinkedTestCasesDialog(list) {
    this.translate.get("test_data_profiles.linked_with_cases").subscribe((res) => {
      this.matDialog.open(LinkedEntitiesModalComponent, {
        width: '568px',
        height: '55vh',
        data: {
          description: res,
          linkedEntityList: list,
        },
        panelClass: ['mat-dialog', 'rds-none']
      });
    });
  }

}
