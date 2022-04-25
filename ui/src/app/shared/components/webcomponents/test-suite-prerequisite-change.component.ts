import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TestPlan} from "../../../models/test-plan.model";

@Component({
  selector: 'app-prerequisite-change',
  templateUrl: './test-suite-prerequisite-change.component.html',
  styles: [
  ]
})
export class TestSuitePrerequisiteChangeComponent {

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: {
      description: string,
      executions: TestPlan[],
    }
  ) {
  }


  openLinkedEntity(id: number) {
    let entityUrl = "/ui/td/plans/"+ id +"/suites";
    window.open(window.location.origin + entityUrl, "_blank");
  }
}
