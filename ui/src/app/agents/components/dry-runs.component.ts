import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {AgentService} from "../services/agent.service";
import {BaseComponent} from "../../shared/components/base.component";
import {Page} from "../../shared/models/page";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {Agent} from "../models/agent.model";
import {Pageable} from "../../shared/models/pageable";
import {TestCaseResult} from "../../models/test-case-result.model";
import {TestCaseResultService} from "../../services/test-case-result.service";

@Component({
  selector: 'app-dry-runs',
  templateUrl: './dry-runs.component.html',
  styles: []
})
export class DryRunsComponent extends BaseComponent implements OnInit {
  public agentId: Number;
  public dryTestCaseResults: Page<TestCaseResult>;
  public currentPage: Pageable;
  public fetchingCompleted: Boolean = false;
  public agent: Agent;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private route: ActivatedRoute,
    private agentService: AgentService,
    private testCaseResultService: TestCaseResultService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      const allParams = {...params, ...{agentId: this.route.parent.params['_value'].agentId}};
      this.pushToParent(this.route, allParams);
      this.agentId = allParams.agentId;
      this.agentService.find(this.agentId).subscribe(res => {
        this.agent = res;
        this.fetchDryRuns();
      })
    });
  }

  fetchDryRuns() {
    this.testCaseResultService.findAll("entityType:ADHOC_TEST_PLAN,iteration:null,targetMachine:" + this.agent.id, "id,desc", this.currentPage).subscribe(res => {
      this.goToPreviousPageIfEmpty(res);
      this.dryTestCaseResults = res;
      this.fetchingCompleted = true;
      this.currentPage = this.dryTestCaseResults.pageable;
    });
  }

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0  ) {
      this.currentPage.pageNumber--;
      this.fetchDryRuns();
      return;
    }
  }

}
