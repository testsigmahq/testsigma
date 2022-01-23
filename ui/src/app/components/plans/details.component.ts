import { Component, OnInit } from '@angular/core';
import {TestPlan} from "../../models/test-plan.model";
import {TestPlanService} from "../../services/test-plan.service";
import { ActivatedRoute } from '@angular/router';

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

  public execution: TestPlan;

  constructor(
    private route: ActivatedRoute,
    private executionService: TestPlanService) { }

  ngOnInit(): void {
    this.executionService.find(this.route.parent.snapshot.params.testPlanId).subscribe(res => this.execution = res);
  }

}
