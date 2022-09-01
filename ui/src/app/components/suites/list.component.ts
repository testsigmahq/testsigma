import {Component, OnInit} from '@angular/core';
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TestSuiteService} from "../../services/test-suite.service";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {TestSuite} from "../../models/test-suite.model";
import {Page} from "../../shared/models/page";
import {Pageable} from "../../shared/models/pageable";

import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {MatDialog} from '@angular/material/dialog';
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestPlanService} from "../../services/test-plan.service";
import {ToastrService} from "ngx-toastr";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {FilterFormComponent} from "./filter-form.component";
import * as moment from "moment";


@Component({
  selector: 'app-test-suites',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
})
export class ListComponent extends BaseComponent implements OnInit {

  public defaultQuery = "";
  public versionId: number;
  public selectedSuites = [];
  public searchQuery = "";
  public testSuites: Page<TestSuite>;
  public query: string;
  public version: WorkspaceVersion;
  sortByColumns = ["name", "isManual","lastRun", "createdDate", "updatedDate"];
  direction = ",asc";
  sortedBy = "name";
  public sortByQuery:string
  selectAll = false;
  currentPage = new Pageable();
  fetchingCompleted: boolean;
  isFiltered: boolean;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testSuiteService: TestSuiteService,
    private testPlanService: TestPlanService,
    public route: ActivatedRoute,
    private router: Router,
    private workspaceVersionService: WorkspaceVersionService,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get hideHeaderToolBar() {
    return (this.selectedSuites.length ||
      (!this.testSuites?.content.length && !this.searchQuery && !this.query?.length))
  };

  ngOnInit(): void {
    this.route.parent.params.subscribe((params: Params) => {
      this.versionId = params.versionId;
      this.fetchVersion();
      this.pushToParent(this.route, this.route.parent.snapshot.params);
      this.defaultQuery = "workspaceVersionId:" + this.versionId;
      this.fetchTestSuites();
    });
    this.route.params.subscribe((params) => {
      const allParams = {...params, ...{versionId: this.versionId}};
      this.pushToParent(this.route, allParams);
      this.refreshListView(this.route.snapshot.queryParamMap['params']['q']);
    })
  };

  refreshListView(query) {
    this.query = query;
    setTimeout(()=>{
      this.fetchTestSuites();
    },0);
  }

  fetchVersion() {
    this.workspaceVersionService.show(this.versionId).subscribe(res => this.version = res);
  }

  fetchTestSuites() {
    let sortBy = this.sortedBy + this.direction;
    let query = this.defaultQuery + this.searchQuery + (this.query||'');
    this.testSuiteService.findAll(query, sortBy, this.currentPage).subscribe(res => {
        this.isFiltered = !!this.searchQuery.length;
        this.goToPreviousPageIfEmpty(res)
        this.fetchingCompleted = true;
        this.testSuites = res
        this.currentPage = res.pageable;
        this.selectAllToggle(false);
      });
  }

  sortBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.fetchTestSuites();
  }

  selectAllToggle(selectAll: Boolean) {
    let selectedSuiteIds = [];
    this.testSuites.content.find((testsuite, i) => {
      this.testSuites.content[i].isSelected = selectAll;
      selectedSuiteIds.push(this.testSuites.content[i].id);
    })
    this.selectedSuites = selectAll ? selectedSuiteIds : [];
  }

  setSelectedList(id: number, checked: Boolean) {
    if (checked)
      this.selectedSuites.push(id)
    else {
      this.selectedSuites.splice(this.selectedSuites.indexOf(id), 1);
    }
  }

  openDeleteDialog(id?, name?: string) {
    let message = id ? "message.common.confirmation.default" : "test_suites.bulk_delete.confirmation.message";
    this.translate.get(message, {FieldName: this.selectedSuites.length}).subscribe((res) => {
      const dialogRef = this.matDialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res,
          isPermanentDelete: true,
          title: name? 'Test Suite' : 'Test Suites',
          item: 'test suite',
          name: name ? name : 'multiple test suites',
          note: this.translate.instant('message.common.confirmation.test_suite_des', {Item:'test suite'})
        },
        panelClass: ['matDialog', 'delete-confirm']
      });
      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            if (id)
              this.destroyTestSuite(id);
            else
              this.bulkDelete()
          }
        });
    })
  }

  search(term: string) {
    if (term) {
      this.isFiltered = true;
      this.searchQuery = ",name:*" + term + "*";
    } else {
      this.isFiltered = false;
      this.searchQuery = "";
    }
    this.fetchTestSuites()
  }

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0) {
      this.currentPage.pageNumber--;
      this.fetchTestSuites();
      return;
    }
  }

  private destroyTestSuite(id: any) {
    this.testSuiteService.destroy(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Test Suite"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.testSuites.content = this.testSuites.content.filter(suite => suite.id != id);
        this.fetchTestSuites();
        this.selectedSuites = []
        this.selectAll = false;
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: "Test Suite"})
        .subscribe(res => this.showAPIError(err, res))
    );
  }

  private bulkDelete() {
    this.testSuiteService.bulkDestroy(this.selectedSuites).subscribe(
      () => {
        this.translate.get("message.common.deleted.success", {FieldName: "Test Suites"}).subscribe(res => this.showNotification(NotificationType.Success, res));
        this.testSuites.content = this.testSuites.content.filter(suite => {
          this.selectedSuites.forEach(selectSuite => {
            return suite.id != selectSuite.id
          })
        });
        this.fetchTestSuites();
        this.selectedSuites = []
      },
      (err) => this.translate.get("test_suite.notification.bulk_delete.failure").subscribe(res => {
        this.showAPIError(err, res);
        this.fetchTestSuites();
      }))
  }

  public fetchLinkedPlans(id, name?: string) {
    let testPlans: InfiniteScrollableDataSource;
    testPlans = new InfiniteScrollableDataSource(this.testPlanService, "suiteId:" + id , "name,asc" );
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testPlans.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testPlans.isEmpty)
          _this.openDeleteDialog(id, name);
        else
          _this.openLinkedTestPlansDialog(testPlans);
      }
    }
  }


  private openLinkedTestPlansDialog(list) {
    this.translate.get("suite.linked_with_test_plans").subscribe((res) => {
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
  openFilter() {

    let filterDialogRef = this.matDialog.open(FilterFormComponent, {
      width: '25%',
      height: '100vh',
      position: {top: '0', right: '0', bottom: '0'},
      panelClass: ['mat-overlay'],
      data: {
        query: this.query,
        version:this.version
      }
    });
    filterDialogRef.componentInstance.filterEvent.subscribe(query => {
      if (query) {
        this.query = query;
        this.router.navigate(['/td', this.version.id, 'suites'], {queryParams: {q: this.query}});
        this.fetchTestSuites();
      } else
        this.discard();
    });
  }

  discard() {
    this.query = undefined;
    this.router.navigate(['/td', this.version.id, 'suites']);
    this.fetchTestSuites();
  }

  humanizedDate(date) {
    return moment.duration(moment().diff(date)).humanize() + ' ago';
  }

}
