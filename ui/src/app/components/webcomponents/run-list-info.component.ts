import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TestPlan} from "../../models/test-plan.model";


@Component({
  selector: 'app-run-list-info',
  templateUrl: './run-list-info.component.html',
  styles: []
})
export class RunListInfoComponent implements OnInit {
  public testPlan: TestPlan;

  constructor(
    @Inject(MAT_DIALOG_DATA) public options: { execution: TestPlan }) {
    this.testPlan = options.execution;
  }

  ngOnInit() {
  }
}
