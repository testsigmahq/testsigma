import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from "@ngx-translate/core";
import {ActivatedRoute, NavigationEnd, Router, RouterEvent} from "@angular/router";
import {MatDialog, MatDialogConfig, MatDialogRef} from "@angular/material/dialog";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {TestCaseService} from "../../services/test-case.service";
import {BaseComponent} from "../../shared/components/base.component";
import {TestCasesFilterComponent} from "../webcomponents/test-cases-filter.component";
import {TestCasesFiltersListComponent} from "../webcomponents/test-cases-filters-list.component";
import {TestCaseFilter} from "../../models/test-case-filter.model";
import {Page} from "../../shared/models/page";
import {TestCaseFilterService} from "../../services/test-case-filter.service";
import {filter, pairwise, takeUntil} from 'rxjs/operators';
import {Subject} from 'rxjs';
import {TestCaseFilterFormComponent} from "../webcomponents/test-case-filter-form.component";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {Pageable} from "../../shared/models/pageable";
import {TestCase} from "../../models/test-case.model";
import {StepGroupFilter} from "../../models/step-group-filter.model";
import {StepGroupFilterService} from "../../services/step-group-filter.service";
import {UserPreferenceService} from "../../services/user-preference.service";
import {UserPreference} from "../../models/user-preference.model";
import {BackupFormComponent} from "../webcomponents/backup-form.component";
import {ToastrService} from "ngx-toastr";
import { InfiniteScrollableDataSource } from 'app/data-sources/infinite-scrollable-data-source';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
  styles: []
})
export class TestCasesListComponent extends BaseComponent implements OnInit {
  public testCases: Page<TestCase>;
  public versionId: number;
  public filterId: number;
  public filters: Page<TestCaseFilter>;
  public stepGroupFilters: Page<StepGroupFilter>;
  public currentFilter: TestCaseFilter | StepGroupFilter;
  public version: WorkspaceVersion;
  public filterDialogRef: MatDialogRef<TestCasesFilterComponent, any>;
  public filterListDialogRef: MatDialogRef<TestCasesFiltersListComponent, any>;
  public filterFormDialogRef: MatDialogRef<TestCaseFilterFormComponent, TestCaseFilter>;
  public query: string;
  public destroyed = new Subject<any>();
  public sortByColumns = ["name", "createdDate", "updatedDate"];
  public direction = ",asc";
  public sortedBy = "name";
  public selectedTestCases = [];
  public selectAll: Boolean;
  public currentPage: Pageable = new Pageable();
  public isFetching: boolean = true;
  public userPreference: UserPreference;
  private resultFilter: string;

  @ViewChild('filterListBtn') public filterListBtn: ElementRef;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public testCaseService: TestCaseService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private matModal: MatDialog,
    private router: Router,
    private matDialog: MatDialog,
    private workspaceVersionService: WorkspaceVersionService,
    private testCaseFilterService: TestCaseFilterService,
    private stepGroupFilterService: StepGroupFilterService,
    private userPreferenceService: UserPreferenceService) {
    super(authGuard, notificationsService, translate,toastrService);
  }

  get isStepGroup() {
    return this.router.url.indexOf("/step_groups") !== -1;
  }

  get urlString() {
    return this.isStepGroup ? 'step_groups' : 'cases';
  }

  ngOnInit() {
    this.userPreferenceService.show().subscribe(res => {
      this.userPreference = res;
      this.versionId = this.route.parent.parent.snapshot.params['versionId'];
      this.fetchVersion();
      this.userPreference.testCaseFilterId = this.route.snapshot.params['filterId'];
      this.setTestCaseFilterId(this.userPreference);
      this.filterId = <number>this.route.snapshot.params['filterId'];
      if (this.route.snapshot.queryParamMap['params']['result']) {
        this.resultFilter = this.route.snapshot.queryParamMap['params']['result']
      } else {
        this.resultFilter = undefined;
      }
      this.router.events.pipe(
        filter((event: RouterEvent) => event instanceof NavigationEnd),
        pairwise(),
        filter((events: RouterEvent[]) => {
          return events[0].url === events[1].url
        }),
        takeUntil(this.destroyed)
      ).subscribe((params) => {
        const allParams = {...params, ...{versionId: this.versionId}};
        this.pushToParent(this.route, allParams);
        this.refreshListView(this.route.snapshot.params['filterId'], this.route.snapshot.queryParamMap['params']['q']);
      });
      this.route.params.subscribe((params) => {
        const allParams = {...params, ...{versionId: this.versionId}};
        this.pushToParent(this.route, allParams);
        this.refreshListView(params['filterId'], this.route.snapshot.queryParamMap['params']['q']);
      })
    })
  }

  refreshListView(filterId, query) {
    this.userPreference.testCaseFilterId = this.route.snapshot.params['filterId'];
    this.setTestCaseFilterId(this.userPreference);
    this.filterId = filterId;
    this.query = query;
    this.fetchFilters();
  }

  ngOnDestroy(): void {
    this.destroyed.next();
    this.destroyed.complete();
  }

  fetchVersion() {
    this.workspaceVersionService.show(this.versionId).subscribe(res => this.version = res);
  }

  searchWithName(term?: string) {
    if (term) {
      if (!this.query?.includes('workspaceVersionId'))
        this.query += ",workspaceVersionId:" + this.versionId;
      if (!this.query?.includes('deleted'))
        this.query += ",deleted:" + this.getQueryHashValue('deleted');
      if (!this.query?.includes('isStepGroup'))
        this.query += ",isStepGroup:" + this.getQueryHashValue('isStepGroup');
      if (this.query?.includes('name')) {
        term = encodeURIComponent(term);
        this.query = this.query.replace(/,name:\*.+(.?)\*/g, ",name:*" + term + "*")
      } else {
        this.query += ",name:*" + term + "*";
      }
      this.router.navigate(['/td', this.version.id, this.urlString, 'filter', this?.userPreference?.testCaseFilterId], {queryParams: {q: this.query}});
      this.fetchTestCases()
    } else {
      this.discard()
    }
  }

  getQueryHashValue(name) {
    let returnValue;
    this.currentFilter.queryHash.forEach(item => {
      if (item.key == name) {
        returnValue = item.value
      }
    })
    return returnValue;
  }

  fetchTestCases() {
    this.isFetching = true;
    let sortBy = this.sortedBy + this.direction;
    if (this.resultFilter) {
      this.query = this.query ? this.query : '';
      if (this.resultFilter == 'passed')
        this.query += ",deleted:false,isStepGroup:false,result@SUCCESS,workspaceVersionId:" + this.versionId;
      else if (this.resultFilter == 'failed')
        this.query += ",deleted:false,isStepGroup:false,result@FAILURE#ABORTED,workspaceVersionId:" + this.versionId;
      else if (this.resultFilter == 'notExecuted')
        this.query += ",deleted:false,isStepGroup:false,result@QUEUED#STOPPED#NOT_EXECUTED#null,workspaceVersionId:" + this.versionId;
    }
    if (this.query) {
      this.currentFilter.normalizeQuery(this.versionId);
      let query = this.query + this.currentFilter.queryString;
      this.query = query;
      this.testCaseService.findAll(query, sortBy, this.currentPage).subscribe(res => {
        this.testCases = res;
        this.currentPage = res.pageable;
        this.selectAllToggle(false)
        this.isFetching = false;
      }, () => {
        this.isFetching = false;
      })
    } else {
      this.testCaseService.filter(this.filterId, this.versionId, this.currentPage, sortBy).subscribe(res => {
        this.testCases = res;
        this.currentPage = res.pageable;
        this.selectAllToggle(false)
        this.isFetching = false;
      }, () => {
        this.isFetching = false;
      })
    }
  }

  openFilter() {

    this.filterDialogRef = this.matModal.open(TestCasesFilterComponent, {
      width: '25%',
      height: '100vh',
      position: {top: '0', right: '0', bottom: '0'},
      panelClass: ['mat-overlay'],
      data: {version: this.version, filter: this.currentFilter, query: this.query, isStepGroup: this.isStepGroup}
    });
    this.filterDialogRef.componentInstance.filterEvent.subscribe(query => {
      if (query) {
        this.query = query;
        this.router.navigate(['/td', this.version.id, this.urlString, 'filter', this?.userPreference?.testCaseFilterId], {queryParams: {q: this.query}});
        this.fetchTestCases();
      } else
        this.discard();
    });
  }

  discard() {
    this.query = undefined;
    this.resultFilter = undefined;
    this.router.navigate(['/td', this.version.id, this.urlString, 'filter', this?.userPreference?.testCaseFilterId]);
    this.fetchTestCases();
  }

  openFiltersList() {
    this.filterListDialogRef = this.matModal.open(TestCasesFiltersListComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      height: '100%',
      width: '364px',
      panelClass: ['mat-overlay', 'cases-overlay'],
      data: {
        list: this.filters || this.stepGroupFilters,
        currentFilter: this.currentFilter,
        version: this.version,
        isStepGroups: this.isStepGroup
      }
    });

    const matDialogConfig = new MatDialogConfig();
    const rect: DOMRect = this.filterListBtn.nativeElement.getBoundingClientRect();
    matDialogConfig.position = {left: `${rect.left - 42}px`, top: `${rect.bottom}px`}
    this.filterListDialogRef.updatePosition(matDialogConfig.position);
  }

  fetchFilters() {
    if (this.isStepGroup)
      this.stepGroupFilterService.findAll(this.versionId).subscribe(res => {
        this.stepGroupFilters = res;
        this.setCurrentFilter(res);
        this.fetchTestCases();
      });
    else
      this.testCaseFilterService.findAll(this.versionId).subscribe(res => {
        this.filters = res;
        this.setCurrentFilter(res);
        this.fetchTestCases();
      });
  }

  setCurrentFilter(filters) {
    this.currentFilter = filters.content.find(filter => filter.id == parseInt(String(this.filterId)));
    if (!this.currentFilter)
      this.currentFilter = filters.content.find(filter => filter.id == this.userPreference?.testCaseFilterId);
    if (!this.currentFilter)
      this.currentFilter = filters.content.find(filter => filter.isDefault);
  }

  saveViewAs() {
    let filter;
    if (this.isStepGroup) {
      filter = new StepGroupFilter();
      filter.name = "";
      filter.versionId = this.version.id;
      filter.isDefault = false;
      filter.isPublic = true;
    } else {
      filter = new TestCaseFilter();
      filter.name = "";
      filter.versionId = this.version.id;
      filter.isDefault = false;
      filter.isPublic = true;
    }

    this.openFilterForm(filter);
  }

  saveView() {
    this.openFilterForm(this.currentFilter);
  }

  deleteView() {
    if (this.isStepGroup)
      this.deleteStepGroupFilter();
    else
      this.deleteCaseFilter();
  }

  deleteStepGroupFilter() {
    this.stepGroupFilterService.destroy(this.currentFilter.id).subscribe(() => {
      this.translate.get('filter.delete.success', {name: this.currentFilter.name}).subscribe((key: string) => {
        this.postFilterDestroy(key);
      });
    });
  }

  deleteCaseFilter() {
    this.testCaseFilterService.destroy(this.currentFilter.id).subscribe(() => {
      this.translate.get('filter.delete.success', {name: this.currentFilter.name}).subscribe((key: string) => {
        this.postFilterDestroy(key);
      });
    });
  }

  postFilterDestroy(key) {
    this.filters.content = this.filters.content.filter(filter => filter.id != this?.userPreference?.testCaseFilterId);
    this.showNotification(NotificationType.Success, key);
    this.currentFilter = this.filters.content[0];
    this.userPreference.testCaseFilterId = this.currentFilter.id;
    this.setTestCaseFilterId(this.userPreference);
    this.filterId = this.currentFilter.id;
    this.router.navigate(['/td', this.version.id, this.urlString, 'filter', this?.userPreference?.testCaseFilterId]);
    this.fetchFilters();
  }


  sortBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.fetchTestCases();
  }

  private openFilterForm(filter: TestCaseFilter | StepGroupFilter) {
    this.filterFormDialogRef = this.matModal.open(TestCaseFilterFormComponent, {
      width: '40%',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {query: this.query, version: this.version, filter: filter}
    });

    this.filterFormDialogRef.afterClosed().subscribe((result: TestCaseFilter | StepGroupFilter) => {
      if (result) {
        this.userPreference.testCaseFilterId = result.id;
        this.setTestCaseFilterId(this.userPreference);
        this.filterId = result.id;
        this.query = undefined;
        this.currentFilter = result;
        this.router.navigate(['/td', this.version.id, this.urlString, 'filter', result.id],{ queryParams: {tempFilter:true}});
        this.fetchFilters();
      }
    });
  }

  openDeleteDialog() {
    this.translate.get("message.common.selected.confirmation.message", {Name: (this.isStepGroup ? 'Step Groups' : 'Test Cases') + '(' + this.selectedTestCases.length + ')'}).subscribe((res) => {
      const dialogRef = this.matModal.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            this.multipleDelete()
          }
        });
    })
  }

  selectAllToggle(select) {
    let testCases = this.testCases.content;
    for (let i = 0; i < testCases.length; i++) {
      if (select && this.selectedTestCases.indexOf(testCases[i]["id"]) == -1) {
        this.selectedTestCases.push(testCases[i]["id"]);
        this.testCases.content[i]['isSelected'] = true;
      } else if (!select) {
        this.testCases.content[i]['isSelected'] = false;
        this.selectedTestCases = [];
      }
    }
  }

  setSelectedList(event, id) {
    if (!this.selectedTestCases.includes(id))
      this.selectedTestCases.push(id);
    else
      this.selectedTestCases.splice(this.selectedTestCases.indexOf(id), 1);
    event.stopPropagation();
    event.stopImmediatePropagation();
  }
  openBackupForm() {
    let matDialogRef = this.matDialog.open(BackupFormComponent, {
      backdropClass: 'cdk-overlay-transparent-backdrop',
      width: '45%',
      height: '63%',
      panelClass: ['mat-dialog', 'rds-none'],
      data: {filterId:this.filterId, workspaceVersionId: this.version.id}
    });
    const matDialogConfig = new MatDialogConfig();
    matDialogRef.updatePosition(matDialogConfig.position);
    matDialogRef.afterClosed().subscribe(res => {

    })
  }
  multipleDelete() {
    let fieldName = this.isStepGroup ? 'Selected Step Group' : ' Selected Test Case';
    fieldName = this.selectedTestCases.length > 1 ? fieldName + 's' : fieldName;
    this.testCaseService.bulkMarkAsDeleted(this.selectedTestCases).subscribe(
      () => {
        this.translate.get("message.common.deleted.success", {FieldName: fieldName}).subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchTestCases();
        this.selectedTestCases = []
      },
      (err) =>{
        if (err.status == "400") {
          this.showNotification(NotificationType.Error, err.error);
          this.fetchTestCases();
          this.selectedTestCases = []
        } else {
          this.translate.get("message.common.deleted.failure", {FieldName: fieldName}).subscribe(res => this.showAPIError(err, res));
        }
      })
  }

  get isDeletedFilter() {
    return this.currentFilter?.isDeleted
  }

  setTestCaseFilterId(userPreference) {
    let tempFilter = this.route.snapshot.queryParamMap['params']['tempFilter'];
    if (!tempFilter) {
      this.userPreferenceService.save(userPreference).subscribe(res => this.userPreference = res)
    }
  }
  public fetchLinkedTestCases(stepGroupId) {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, "workspaceVersionId:" + this.versionId + ",deleted:false,stepGroupId:" + stepGroupId);
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testCases.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        _this.openStepGroupDeleteDialog(testCases, stepGroupId);
      }
    }
  }

  private openStepGroupDeleteDialog(list, id) {

    this.translate.get("message.common.confirmation.default").subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          testCaseId: id,
          description: res,
          isPermanentDelete: true,
          linkedEntityList: list,
          item : "Step group",
          note: this.translate.instant('message.common.confirmation.requirement_type')
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            this.selectedTestCases = id;
            this.multipleDelete();
          }
        });
    })
  }
  fetchTestCase($event,id){
    $event.preventDefault();
    this.fetchLinkedTestCases(id);
    $event.stopPropagation();
  }
}
