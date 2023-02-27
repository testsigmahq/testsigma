import {Component, OnInit} from '@angular/core';
import {Page} from "../../shared/models/page";
import {Pageable} from "../../shared/models/pageable";
import {BaseComponent} from "../../shared/components/base.component";
import {TestPlanService} from "../../services/test-plan.service";
import {TestPlan} from "app/models/test-plan.model";
import {TestDeviceService} from "../../services/test-device.service";
import {ActivatedRoute, Params, Router} from '@angular/router';
import {interval, Subscription} from "rxjs";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceVersionService} from "../../shared/services/workspace-version.service";
import {TestPlanType} from "../../enums/execution-type.enum";
import {FilterFormComponent} from "../results/filter-form.component";
import {MatDialog} from "@angular/material/dialog";


@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  host: {'class': 'page-content-container'},
  styles: []
})
export class ResultsListComponent extends BaseComponent implements OnInit {
  public testPlans: Page<TestPlan>;
  public currentPage: Pageable;
  public sortBy: string;
  public fetchingCompleted: Boolean = false;
  public isSearchEnable: Boolean = false;
  public versionId: Number;
  public searchQuery: string = undefined;
  public autoRefreshSubscription: Subscription;
  public autoRefreshInterval: number = 10000;
  public isDisabledAutoRefresh: boolean = false;
  public isRunning: boolean;
  public isFiltered: boolean = false;
  public version: WorkspaceVersion
  public direction = ",asc";
  public sortedBy = "name";
  public sortByColumns = ["name", "executionType", "createdDate", "updatedDate","lastRun"];
  public query: string;

  constructor(
    private testPlanService: TestPlanService,
    public testDeviceService: TestDeviceService,
    public route: ActivatedRoute,
    private router: Router,
    public matModal: MatDialog,
    private versionService: WorkspaceVersionService) {
    super();
  }

  ngOnInit() {
    this.route.parent.params.subscribe((res: Params) => {
      this.pushToParent(this.route, res);
      this.versionId = res.versionId;
      this.fetchVersion();
      this.fetchTestPlans();
      this.attachAutoRefreshEvents();
    });
    this.route.params.subscribe((params) => {
      const allParams = {...params, ...{versionId: this.versionId}};
      this.pushToParent(this.route, allParams);
      this.refreshListView(this.route.snapshot.queryParamMap['params']['q']);
    });
  }

  refreshListView(query) {
    this.query = query;
    setTimeout(()=>{
      this.fetchTestPlans(this.query);
    },0);
  }

  fetchVersion() {
    this.versionService.show(this.versionId).subscribe(res => this.version = res);
  }

  getCrossBrowser(){
    return TestPlanType.CROSS_BROWSER
  }

  ngOnDestroy(): void {
    this.removeAutoRefresh();
  }

  toggleAutoRefresh(isDisabledAutoRefresh: boolean) {
    this.isDisabledAutoRefresh = isDisabledAutoRefresh;
    if (this.isDisabledAutoRefresh) {
      this.removeAutoRefresh();
    } else {
      this.addAutoRefresh();
    }
  }

  changeAutoRefreshTime(event: number) {
    this.autoRefreshInterval = event;
    this.addAutoRefresh()
  }

  fetchTestPlans(query?: string) {
    query = query ? query : '';
    query = "workspaceVersionId:" + this.versionId + query;
    this.isRunning = false;
    this.removeAutoRefresh();
    this.testPlanService.findAll(query, this.sortBy, this.currentPage).subscribe(executions => {
      this.testPlans = executions;
      this.testPlans.content.filter((execution) => {
        if (execution.lastRun && execution.lastRun.isQueued) {
          this.isRunning = true;
          this.addAutoRefresh();
          return;
        }
      })
      this.currentPage = executions.pageable;
      if(this.testPlans?.content?.length)
        this.fetchExecutionEnvironments();
      else
        this.fetchingCompleted = true;
    });
  }

  sortThisBy(value, direction) {
    if (!(this.sortedBy != value || this.direction != direction))
      return;
    this.direction = direction;
    this.sortedBy = value;
    this.sortBy = this.sortedBy + this.direction ;
    this.fetchTestPlans(this.searchQuery);
  }

  fetchExecutionEnvironments() {
    let pageable = new Pageable();
    pageable.pageSize = 200;
    let query = "testPlanId@" + this.testPlans.content.map((exe) => exe.id).join("#");
    this.testDeviceService.findAll(query, undefined, pageable).subscribe((environments) => {
      this.testPlans.content.forEach((exe) => {
        let filteredEnvs = environments.content.filter((exeEnv) => exeEnv.testPlanId === exe.id);
        if (filteredEnvs)
          exe.testDevices = filteredEnvs;
      });
      this.fetchingCompleted = true;
    })
  }

  attachAutoRefreshEvents() {
    document.addEventListener("visibilitychange", () => {
      document.hidden ? this.removeAutoRefresh() : this.addAutoRefresh(true);
    });
  }

  addAutoRefresh(listenerChangeTrue?: boolean) {
    if (listenerChangeTrue && this.isRunning && !this.isDisabledAutoRefresh){
      this.fetchTestPlans();
    }
    this.removeAutoRefresh();
    if (!this.isRunning || this.isDisabledAutoRefresh)
      return;
    this.autoRefreshSubscription = interval(this.autoRefreshInterval).subscribe(() => {
      this.fetchTestPlans();
    });
  }

  removeAutoRefresh() {
    if (this.autoRefreshSubscription) {
      this.isRunning = false;
      this.autoRefreshSubscription.unsubscribe();
    }
  }

  search(term: string) {
    if (term) {
      this.isFiltered = true;
      this.searchQuery = ",name:*" + term + "*";
    } else {
      this.isFiltered = false;
      this.searchQuery = undefined;
    }
    this.fetchTestPlans(this.searchQuery)
  }

  checkIfLastRunExsists(testPlan: TestPlan){
    return !(testPlan?.lastRun == null && testPlan?.lastRun?.id == null);

  }
  openFilter() {

    let filterDialogRef = this.matModal.open(FilterFormComponent, {
      width: '25%',
      height: '100vh',
      position: {top: '0', right: '0', bottom: '0'},
      panelClass: ['mat-overlay'],
      data: {query: this.query}
    });
    filterDialogRef.componentInstance.filterEvent.subscribe(query => {
      if (query) {
        this.query = query;
        this.router.navigate(['/td', this.version.id, 'results'], {queryParams: {q: this.query}});
        this.fetchTestPlans(this.query);
      } else
        this.discard();
    });
  }
  removeHtmlTags(str) {
    return str.replace(/<[^>]*>/g, '');
  }

  discard() {
    this.query = undefined;
    this.router.navigate(['/td', this.version.id, 'results']);
    this.fetchTestPlans();
  }
}
