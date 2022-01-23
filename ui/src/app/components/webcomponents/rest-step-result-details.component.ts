import {Component, Input, OnInit} from '@angular/core';
import {TestStepResultMetadata} from "../../models/test-step-result-metadata.model";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-rest-step-result-details',
  templateUrl: './rest-step-result-details.component.html',
  styles: []
})
export class RestStepResultDetailsComponent implements OnInit {
  @Input('metaData') metaData: TestStepResultMetadata;
  public requestDetailsShow: boolean = false;
  public resBodyShow: boolean = false;
  public resHeaderShow: boolean = false;
  public requestDetails: any;
  public responseStatus: number;
  public headers: any;
  public data: any;

  constructor(private matDialog: MatDialog) {
  }

  ngOnInit() {
    this.data = this.metaData?.restResult?.jsonResponse;
  }

  ngOnChanges(){
    this.data = this.metaData?.restResult?.jsonResponse;
  }

}
