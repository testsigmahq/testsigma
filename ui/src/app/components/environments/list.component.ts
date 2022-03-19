import {Component, OnInit} from '@angular/core';
import {Page} from "../../shared/models/page";
import {Pageable} from "../../shared/models/pageable";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from "angular2-notifications";
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Params} from "@angular/router";
//
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {BaseComponent} from "../../shared/components/base.component";
import {Environment} from "../../models/environment.model";
import {EnvironmentService} from "../../services/environment.service";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestPlanService} from "../../services/test-plan.service";

@Component({
  selector: 'app-environment',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'}
})
export class ListComponent extends BaseComponent implements OnInit {
  public environments: Page<Environment>;
  public currentPage: Pageable = new Pageable();
  public versionId: number;
  public defaultQuery: string;
  public sortByColumns = ['name', 'createdDate', 'updatedDate'];
  public sortedBy = this.sortByColumns[0];
  public direction: string = ",asc";
  public selectAll: Boolean = false;
  public selectedEnvironments = [];
  public searchQuery = "";
  public fetchingCompleted: Boolean = false;
  public isFiltered: Boolean = false;
  private projectId: number;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private environmentService: EnvironmentService,
    // private userService: UserService,
    private matDialog: MatDialog,
    private workspaceVersionService: WorkspaceVersionService,
    private testPlanService: TestPlanService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get hideHeaderToolBar() {
    return (this.selectedEnvironments.length ||
      (!this.environments?.content.length && !this.searchQuery))
  };

  ngOnInit(): void {
    this.route.parent.parent.params.subscribe((params: Params) => {
      this.versionId = params.versionId;
      this.pushToParent(this.route, params);
      this.workspaceVersionService.show(this.versionId).subscribe((res) => {
        this.fetchEnvironments();
      })
    });

  }

  fetchEnvironments() {
    this.fetchingCompleted = false;
    this.currentPage.pageSize = 10;
    let sortBy = this.sortedBy + this.direction;
    let query = this.defaultQuery + this.searchQuery;
    this.environmentService.findAll(query, sortBy, this.currentPage)
      .subscribe((res: Page<Environment>) => {
        this.fetchingCompleted = true;
        this.goToPreviousPageIfEmpty(res);
        this.environments = res;
        this.currentPage = res.pageable;
        this.selectAllToggle(false);
      });
  }

  sortBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.fetchEnvironments()
  }

  selectAllToggle(selectAll: Boolean) {
    let testDataProfileIds = [];
    this.environments.content.find((testData, i) => {
      this.environments.content[i].selected = selectAll;
      testDataProfileIds.push(this.environments.content[i].id);
    })
    this.selectedEnvironments = selectAll ? testDataProfileIds : [];
  }

  setSelectedList(id: number, checked: Boolean) {
    if (checked)
      this.selectedEnvironments.push(id)
    else {
      this.selectedEnvironments.splice(this.selectedEnvironments.indexOf(id), 1);
    }
  }

  openDeleteDialog(id?) {
    let message = id ? "message.common.confirmation.default" : "environments.bulk_delete.confirmation.message";
    this.translate.get(message, {FieldName: this.selectedEnvironments.length}).subscribe((res) => {
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
              this.destroyEnvironment(id);
            else
              this.multipleDelete()
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
    this.fetchEnvironments()
  }

  private destroyEnvironment(id: any) {
    this.environmentService.delete(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Environment"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchEnvironments();
        this.selectedEnvironments = [];
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: "Environment"})
        .subscribe(res => this.showAPIError(err, res))
    );
  }

  private multipleDelete() {
    this.environmentService.bulkDestroy(this.selectedEnvironments).subscribe(
      () => {
        this.translate.get("message.common.deleted.success", {FieldName: "Environment"}).subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchEnvironments();
        this.selectedEnvironments = []
      },
      (err) => this.translate.get("message.common.deleted.failure", {FieldName: "Environment"}).subscribe(res => this.showAPIError(err, res,"Environments","Test Plan")))
  }

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0) {
      this.currentPage.pageNumber--;
      this.fetchEnvironments();
      return;
    }
  }

  checkForLinkedTestPlans(environmentId) {
    let testPlans: InfiniteScrollableDataSource;
    testPlans = new InfiniteScrollableDataSource(this.testPlanService, ",environmentId:" + environmentId);
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testPlans.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testPlans.isEmpty)
          _this.openDeleteDialog(environmentId);
        else
          _this.openLinkedTestPlansDialog(testPlans);
      }
    }
  }

  private openLinkedTestPlansDialog(list) {
    this.translate.get("environments.list.linked_with_test_plans").subscribe((res) => {
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

}
