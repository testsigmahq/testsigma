import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-duplicate-locator-warning',
  templateUrl: './duplicate-locator-warning.component.html',
})
export class DuplicateLocatorWarningComponent{

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: {
                                                description: string,
                                                elements: Element[],
                                                isRecorder: boolean,
                                                isUpdate: boolean,
                                               }
  ) {
  }

}
