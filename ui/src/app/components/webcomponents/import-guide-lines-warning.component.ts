import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-import-guide-lines',
  templateUrl: './import-guide-lines-warning.component.html',
})
export class ImportGuideLinesWarningComponent{

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: { description: string}
  ) {
  }

}
