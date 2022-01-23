import {Component, OnInit} from '@angular/core';
import {Page} from "../../shared/models/page";
import {ActivatedRoute, Params} from '@angular/router';
import {Pageable} from "../../shared/models/pageable";

import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {TranslateService} from '@ngx-translate/core';
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {MatDialog} from '@angular/material/dialog';
import {RequirementsService} from "../../services/requirements.service";
import {Requirement} from "../../models/requirement.model";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";
import {TestCaseService} from "../../services/test-case.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-requirements',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'}
})
export class ListComponent extends BaseComponent implements OnInit {
  public requirements: Page<Requirement>;
  public currentPage: Pageable = new Pageable();
  public versionId: number;
  public defaultQuery: string;
  public sortByColumns = ['requirementName', 'createdDate', 'updatedDate'];
  public sortedBy: string = 'requirementName';
  public direction: string = ",asc";
  public selectAll: Boolean = false;
  public selectedRequirements = [];
  public searchQuery = "";
  public fetchingCompleted: Boolean = false;
  public isFiltered: Boolean = false;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    public route: ActivatedRoute,
    private requirementsService: RequirementsService,
    private testCaseService: TestCaseService,
    private matDialog: MatDialog) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  get hideHeaderToolBar() {
    return (this.selectedRequirements.length ||
      (!this.requirements?.content.length && !this.searchQuery))
  };

  ngOnInit(): void {
    this.route.parent.parent.params.subscribe((params: Params) => {
      this.versionId = params.versionId;
      this.pushToParent(this.route, params);
      this.defaultQuery = "workspaceVersionId:" + this.versionId;
      this.fetchRequirements();
    });

  }

  fetchRequirements() {
    this.fetchingCompleted = false;
    this.currentPage.pageSize = 10;
    let sortBy = this.sortedBy + this.direction;
    let query = this.defaultQuery + this.searchQuery;
    this.requirementsService.findAll(query, sortBy, this.currentPage)
      .subscribe((res: Page<Requirement>) => {
        this.fetchingCompleted = true;
        this.goToPreviousPageIfEmpty(res);
        this.requirements = res;
        this.currentPage = res.pageable;
        this.selectAllToggle(false);
      });
  }

  sortBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.fetchRequirements()
  }

  selectAllToggle(selectAll: Boolean) {
    let testDataProfileIds = [];
    this.requirements.content.find((testData, i) => {
      this.requirements.content[i].isSelected = selectAll;
      testDataProfileIds.push(this.requirements.content[i].id);
    })
    this.selectedRequirements = selectAll ? testDataProfileIds : [];
  }

  setSelectedList(id: number, checked: Boolean) {
    if (checked)
      this.selectedRequirements.push(id)
    else {
      this.selectedRequirements.splice(this.selectedRequirements.indexOf(id), 1);
    }
  }

  openDeleteDialog(id?) {
    let message = id ? "message.common.confirmation.default" : "requirements.bulk_delete.confirmation.message";
    this.translate.get(message, {FieldName: this.selectedRequirements.length}).subscribe((res) => {
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
              this.destroyRequirement(id);
            else
              this.multipleDelete()
          }
        });
    })
  }

  search(term: string) {
    if (term) {
      this.isFiltered = true;
      this.searchQuery = ",requirementName:*" + term + "*";
    } else {
      this.isFiltered = false;
      this.searchQuery = "";
    }
    this.fetchRequirements()
  }

  private destroyRequirement(id: any) {
    this.requirementsService.delete(id).subscribe(
      () => {
        this.translate.get('message.common.deleted.success', {FieldName: "Requirement"})
          .subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchRequirements();
        this.selectedRequirements = [];
      },
      (err) => this.translate.get('message.common.deleted.failure', {FieldName: "Requirement"})
        .subscribe(res => this.showNotification(NotificationType.Error, res))
    );
  }

  private multipleDelete() {
    this.requirementsService.bulkDestroy(this.selectedRequirements).subscribe(
      () => {
        this.translate.get("message.common.deleted.success", {FieldName: "Requirements"}).subscribe(res => this.showNotification(NotificationType.Success, res));
        this.fetchRequirements();
        this.selectedRequirements = []
      },
      (err) => this.translate.get("message.common.deleted.failure", {FieldName: "Requirements"}).subscribe(res => this.showNotification(NotificationType.Error, res)))
  }

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0) {
      this.currentPage.pageNumber--;
      this.fetchRequirements();
      return;
    }
  }

  public fetchLinkedCases(requirementId) {
    let testCases: InfiniteScrollableDataSource;
    testCases = new InfiniteScrollableDataSource(this.testCaseService, "workspaceVersionId:" + this.versionId + ",deleted:false,requirementId:" + requirementId);
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testCases.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testCases.isEmpty)
          _this.openDeleteDialog(requirementId);
        else
          _this.openLinkedTestCasesDialog(testCases);
      }
    }
  }

  private openLinkedTestCasesDialog(list) {
    this.translate.get("requirements.linked_with_cases").subscribe((res) => {
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
