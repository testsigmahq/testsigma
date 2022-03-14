import {Component, OnInit} from '@angular/core';
import {AgentService} from "app/agents/services/agent.service";
import {Page} from "app/shared/models/page";
import {Agent} from "app/agents/models/agent.model";
import {Pageable} from "app/shared/models/pageable";
import {BaseComponent} from "app/shared/components/base.component";
import {ActivatedRoute} from "@angular/router";
import {ConfirmationModalComponent} from "../../shared/components/webcomponents/confirmation-modal.component";
import {MatDialog} from '@angular/material/dialog';
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService, NotificationType} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TitleCasePipe} from "@angular/common";
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestPlanService} from "../../services/test-plan.service";
import {LinkedEntitiesModalComponent} from "../../shared/components/webcomponents/linked-entities-modal.component";

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styles: [],
  host: {'class': 'page-content-container'},
  providers: [TitleCasePipe]
})
export class ListComponent extends BaseComponent implements OnInit {
  public agents: Page<Agent>;
  public currentPage: Pageable = new Pageable();
  public osName: String;
  public today: Date;
  public searchQuery: string;
  public isFiltered: boolean;
  public hasAgents: boolean;
  public isFetching: boolean;
  public activeAgentId: any;
  public filterByColumns = ['all', 'active', 'inactive'];
  public filteredBy = this.filterByColumns[0];
  public showVideo: boolean;
  public agentDownloadTag: String;

  get getSource() {
    return "https://s3.amazonaws.com/assets.testsigma.com/videos/agents/setup.mp4";
  }

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private testPlanService: TestPlanService,
    private agentService: AgentService,
    private dialog: MatDialog,
    public titleCasePipe: TitleCasePipe) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.today = new Date();
    this.fetchAgents();
    this.pushToParent(this.route, undefined);
    this.setSystemOSName();
    this.fetchAgentDownloadTag();
  }

  fetchAgents(): void {
    this.isFetching = true;
    let query = this.searchQuery?.length ? "title:*" + this.searchQuery + "*" : '';
    if (this.filteredBy != 'all') {
      query += query.length ? ',' : '';
      query += 'isActive:';
      query += this.filteredBy == "active" ? 'true' : 'false';
    }
    this.currentPage.pageSize = 10;
    this.agentService.findAll(query, undefined, this.currentPage).subscribe((res: Page<Agent>) => {
      this.isFetching = false;
      this.goToPreviousPageIfEmpty(res)
      this.agents = res;
      this.currentPage = res.pageable;
      if (!this.searchQuery?.length)
        this.hasAgents = (this.agents.content.length != 0);
    })
  }

  public fetchLinkedPlans(agent) {
    let testPlans: InfiniteScrollableDataSource;
    testPlans = new InfiniteScrollableDataSource(this.testPlanService, "agentId:" + agent.id, "name,asc");
    waitTillRequestResponds();
    let _this = this;

    function waitTillRequestResponds() {
      if (testPlans.isFetching)
        setTimeout(() => waitTillRequestResponds(), 100);
      else {
        if (testPlans.isEmpty)
          _this.deleteAgent(agent);
        else
          _this.openLinkedTestPlansDialog(testPlans);
      }
    }
  }

  private openLinkedTestPlansDialog(list) {
    this.translate.get("test_device.linked_with_test_plans").subscribe((res) => {
      this.dialog.open(LinkedEntitiesModalComponent, {
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


  deleteAgent(agent) {
    this.translate.get("agents.delete.confirmation.message").subscribe((res) => {
      const dialogRef = this.dialog.open(ConfirmationModalComponent, {
        width: '450px',
        data: {
          description: res
        },
        panelClass: ['matDialog', 'delete-confirm']
      });

      dialogRef.afterClosed()
        .subscribe(result => {
          if (result) {
            this.agentService.destroy(agent.id).subscribe({
                next: () => {
                  this.agentService.ping().subscribe((res) => {
                    if (res.uniqueId == agent.uniqueId) {
                      this.agentService.deregisterAgent(agent).subscribe(() => {
                      });
                    }
                  });
                  this.fetchAgents();
                  this.translate.get("agents.delete.success").subscribe((res: string) => {
                    this.showNotification(NotificationType.Success, res);
                  });
                },
                error: (error) => {
                  if (error.status == "400") {
                    this.showNotification(NotificationType.Error, error.error);
                  } else {
                    this.translate.get("agents.delete.failure").subscribe((res: string) => {
                      this.showNotification(NotificationType.Error, res);
                    });
                  }
                }
              }
            );
          }
        });
    })
  }

  setSystemOSName() {
    this.osName = "linux";
    if (navigator.appVersion.indexOf("Win") !== -1) this.osName = "windows";
    if (navigator.appVersion.indexOf("Mac") !== -1) this.osName = "mac";
  };

  agentDownloadLink(os?: String) {
    os = os || this.osName;
    let camelizeOsName = os[0].toUpperCase() + os.substr(1).toLowerCase();
    return "https://github.com/Testsigmahq/testsigma/releases/download/" + this.agentDownloadTag
      + "/TestsigmaAgent-" + camelizeOsName + ".zip";
  };

  fetchAgentDownloadTag() {
    this.agentService.downloadTag().subscribe(
      (data) => {
        this.agentDownloadTag = data["tag"];
      },
      (error) => {
        console.log("Error while fetching download tag", error);
        this.agentDownloadTag = "latest";
      }
    );
  }

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0) {
      this.currentPage.pageNumber--;
      this.fetchAgents();
      return;
    }
  }

  search(term: string) {
    if (term) {
      this.isFiltered = true;
      this.searchQuery = term;
    } else {
      this.isFiltered = false;
      this.searchQuery = "";
    }
    this.fetchAgents()
  }

  filterBy(filterBy) {
    this.filteredBy = filterBy;
    this.fetchAgents();
  }

  agentIsActive(agent) {
    return !(!agent.isOnline() || agent.isOutOfSync());
  }

  getEmptyMessage(): string {
    return this.isFiltered ?
      (this.filteredBy != this.filterByColumns[0] ?
        this.translate.instant('agents.search.not_found_for', {FieldName: this.titleCasePipe.transform(this.filteredBy)})
        : this.translate.instant('agents.search.not_found_for', {FieldName: ''}))
      :
      this.filteredBy == this.filterByColumns[0] ?
        this.translate.instant('agents.none')
        : this.translate.instant('agents.filter.not_found', {FieldName: this.titleCasePipe.transform(this.filteredBy)});
  }
}
