import { Component, OnInit } from '@angular/core';
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanService} from "../../services/test-plan.service";
import { ActivatedRoute } from '@angular/router';
import {TestPlanTagService} from "../../services/test-plan-tag.service";

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  styles: [
  ],
  host: {
    'class': 'd-flex ts-col-100 h-100'
  }
})
export class DetailsComponent implements OnInit {

  public testPlan: TestPlan;

  constructor(
    private route: ActivatedRoute,
    private testPlanService: TestPlanService,
    public testPlanTagService: TestPlanTagService) { }

  ngOnInit(): void {
    this.testPlanService.find(this.route.parent.snapshot.params.testPlanId).subscribe(res => this.testPlan = res);
  }

}
