import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TestPlan} from "../../../models/test-plan.model";

@Component({
  selector: 'app-prerequisite-change',
  templateUrl: './prerequisite-change.component.html',
  styles: [
  ]
})
export class PrerequisiteChangeComponent{

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: {
      description: string,
      executions: TestPlan[],
    }
  ) {
  }


}
