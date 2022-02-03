import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanService} from "../../services/test-plan.service";
import {Pageable} from "../../shared/models/pageable";
import {TestDeviceService} from "../../services/test-device.service";
import {Page} from "../../shared/models/page";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {NotificationsService} from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-suite-test-plans',
  templateUrl: './test-plans.component.html',
  host: {'class': 'page-content-container'}
})
export class TestPlansComponent extends BaseComponent implements OnInit {
  public testPlans: Page<TestPlan>;
  public fetchingCompleted = false;
  private testSuiteId: number;
  public currentPage: Pageable;

  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    private testPlanService: TestPlanService,
    private route: ActivatedRoute,
    private executionEnvironmentService: TestDeviceService
  ) {
    super(authGuard, notificationsService, translate, toastrService);
  }

  ngOnInit(): void {
    this.testSuiteId = this.route.parent.snapshot.params.testSuiteId;
    this.pushToParent(this.route, this.route.parent.snapshot.params);
    this.fetchTestPlans();
  }

  fetchTestPlans() {
    this.fetchingCompleted = false;
    delete this.testPlans
    this.testPlanService.findAll("suiteId:" + this.testSuiteId, "name,asc", this.currentPage).subscribe(res => {
      this.fetchingCompleted = true;
      this.testPlans = res;
      this.testPlans.content = [...res.content]
      this.currentPage = this.testPlans.pageable;
      if (this.testPlans.content.length > 0)
        this.fetchExecutionEnvironments();
    })
  }

  fetchExecutionEnvironments() {
    let pageable = new Pageable();
    pageable.pageSize = 200;
    let query = "testPlanId@" + this.testPlans.content.map((exe) => exe.id).join("#");
    this.executionEnvironmentService.findAll(query, undefined, pageable).subscribe((environments) => {
      this.testPlans.content.forEach((exe) => {
        let filteredEnvs = environments.content.filter((exeEnv) => exeEnv.testPlanId === exe.id);
        if (filteredEnvs)
          exe.environments = filteredEnvs;
      });
      this.fetchingCompleted = true;
    })
  }
}
