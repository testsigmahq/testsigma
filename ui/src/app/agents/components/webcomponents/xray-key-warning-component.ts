import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-xray-key-warning',
  templateUrl: './xray-key-warning.component.html',
})
export class XrayKeyWarningComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data:{entityType: String},
  ) {
  }

  get testName(){
    switch (this.data.entityType){
      case "TEST_CASE":
        return "Test Case";
      case "TEST_SUITE":
        return "Test Set";
      case "TEST_PLAN":
        return "Test Plan";
      default:
        return "Test";
    }
  }
}
