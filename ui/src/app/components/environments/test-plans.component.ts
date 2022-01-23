import { Component, OnInit } from '@angular/core';
import {Page} from "../../shared/models/page";
import {TestPlan} from "../../models/test-plan.model";
import { ActivatedRoute } from '@angular/router';
import {Pageable} from "../../shared/models/pageable";
import {TestPlanService} from "../../services/test-plan.service";
import {TestDeviceService} from "../../services/test-device.service";

@Component({
  selector: 'app-environment-test-plans',
  templateUrl: './test-plans.component.html',
  styles: [
  ]
})
export class TestPlansComponent implements OnInit {
  fetchingCompleted: boolean;
  public testPlans: Page<TestPlan>;
  public environmentId: number;
  private versionId: number;
  public currentPage: Pageable;

  constructor(
    private testPlanService: TestPlanService,
    private executionEnvironmentService: TestDeviceService,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.environmentId = this.route.parent.snapshot.params.environmentId;
    this.versionId = this.route.parent.snapshot.queryParams['v'];
    this.fetchTestPlans();
  }

  fetchTestPlans() {
    this.fetchingCompleted = false;
    this.testPlanService.findAll("workspaceVersionId:"+this.versionId+",environmentId:" + this.environmentId, "name,asc", this.currentPage).subscribe(res => {
      this.fetchingCompleted = true;
      this.testPlans = res;
      this.currentPage = this.testPlans.pageable;
      if (this.testPlans.content.length > 0)
        this.fetchExecutionEnvironments();
    })
  }

  fetchExecutionEnvironments() {
    let pageable = new Pageable();
    pageable.pageSize = 200;
    let query = "testPlaId@" + this.testPlans.content.map((exe) => exe.id).join("#");
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
