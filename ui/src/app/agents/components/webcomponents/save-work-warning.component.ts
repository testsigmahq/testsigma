import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-save-work-warning',
  templateUrl: './save-work-warning.component.html',
})
export class SaveWorkWarningComponent{
  constructor(
    @Inject(MAT_DIALOG_DATA) public data:{number: number},
  ) {
  }
}
