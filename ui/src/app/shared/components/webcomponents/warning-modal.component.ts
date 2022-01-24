import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-warning-modal',
  templateUrl: './warning-modal.component.html',
  host: {'style': 'padding:0 !important'}
})
export class WarningModalComponent{

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
  }

}
