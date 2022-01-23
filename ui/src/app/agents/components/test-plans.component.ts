import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {AgentService} from "../services/agent.service";
import {Page} from "../../shared/models/page";
import {TestDeviceResult} from "../../models/test-device-result.model";
import {BaseComponent} from "../../shared/components/base.component";
import {Pageable} from "../../shared/models/pageable";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";
import {TestDeviceResultService} from "../../shared/services/test-device-result.service";
import {Agent} from "../models/agent.model";

@Component({
  selector: 'app-test-plans',
  templateUrl: './test-plans.component.html',
  styles: []
})
export class TestPlansComponent extends BaseComponent implements OnInit {
  public agentId: Number;
  public pageNumber: number = 0;
  public environmentResults: Page<TestDeviceResult>;
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
    private environmentResultService: TestDeviceResultService) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      const allParams = {...params, ...{agentId: this.route.parent.params['_value'].agentId}};
      this.pushToParent(this.route, allParams);
      this.agentId = allParams.agentId;
      this.agentService.find(this.agentId).subscribe(agent => {
        this.agent = agent;
        this.fetchTestPlans();
      });
    });
  }

  fetchTestPlans() {
    this.environmentResultService.findAll("entityType:TEST_PLAN,targetMachine:" + this.agent.id, "id,desc", this.currentPage).subscribe(response => {
      this.goToPreviousPageIfEmpty(response);
      this.environmentResults = response;
      this.currentPage = response.pageable;
      this.fetchingCompleted = true;
    });
  }

  private goToPreviousPageIfEmpty(res) {
    if (this.currentPage?.pageNumber > 0 && res.content.length == 0  ) {
      this.currentPage.pageNumber--;
      this.fetchTestPlans();
      return;
    }
  }

}
