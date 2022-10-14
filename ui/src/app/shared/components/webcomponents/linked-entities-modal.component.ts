import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TestPlan} from "../../../models/test-plan.model";
import {TestSuite} from "../../../models/test-suite.model";
import {TestStep} from "../../../models/test-step.model";

@Component({
  selector: 'app-linked-entities-modal',
  templateUrl: './linked-entities-modal.component.html',
})
export class LinkedEntitiesModalComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
  }

  ngOnInit(): void {
  }

  openLinkedEntity(id) {
    let entityUrl;
    if(this.modalData.linkedEntityList['cachedItems'][0] instanceof TestPlan )
      entityUrl = "/ui/td/plans/"+ id +"/details";
    else if (this.modalData.linkedEntityList['cachedItems'][0] instanceof TestSuite)
      entityUrl = "/ui/td/suites/" + id+"/cases";
    else if(this.modalData.linkedEntityList['cachedItems'][0] instanceof TestSuite)
      entityUrl = "/ui/td/cases/" + id;
    else
      entityUrl = "/ui/td/cases/" + id + "/steps";
    window.open(window.location.origin + entityUrl, "_blank");
  }

  get isTestStep(){
    if (this.modalData.linkedEntityList['cachedItems'][0] instanceof TestStep){
      return true
    }
    return false
  }
  public getTextContent(linkedEntity:any){
    return this.isTestStep ? linkedEntity.action : linkedEntity.name
  }
}
