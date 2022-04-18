import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TestSuite} from "../../models/test-suite.model";

@Component({
  selector: 'app-test-case-prerequisite-change',
  templateUrl: './test-case-prerequisite-change.component.html'
})
export class TestCasePrerequisiteChangeComponent{

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: {
      description: string,
      testSuites: TestSuite[],
    }
  ) {
  }

  openLinkedEntity(id: number) {
    let entityUrl = "/ui/td/suites/"+ id +"/cases";
    window.open(window.location.origin + entityUrl, "_blank");
  }
}
