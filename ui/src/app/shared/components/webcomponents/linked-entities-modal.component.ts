import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TestPlan} from "../../../models/test-plan.model";
import {TestSuite} from "../../../models/test-suite.model";
import {TestStep} from "../../../models/test-step.model";
import {TestStepType} from "../../../enums/test-step-type.enum";
import {TestStepConditionType} from "../../../enums/test-step-condition-type.enum";

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

  /**
   * Returns a boolean value true when linkedentiry list is an instance of Teststeps
   * @returns boolean
   */
  get isTestStep(){
    if (this.modalData.linkedEntityList['cachedItems'][0] instanceof TestStep){
      return true
    }
    return false
  }
  public getTextContent(linkedEntity:any){
    if(this.isTestStep){
      if (linkedEntity.action){
        return linkedEntity.action
      }
      else {
        switch (linkedEntity.type) {
          case TestStepType.FOR_LOOP:
            return "For Loop"
          case TestStepType.STEP_GROUP:
            return linkedEntity?.stepGroup?.name
        }
      }
    }
    return linkedEntity.name
  }

  public stepsIcons(linkedEntity:any){
    switch (linkedEntity.type) {
      case TestStepType.REST_STEP:
        return "fa-rest-new text-warning"
      case TestStepType.STEP_GROUP:
        return "fa-plus-square-solid"
    }
    switch (linkedEntity.conditionType) {
      case TestStepConditionType.LOOP_WHILE:
        return "fa-while-loop text-warning"
      case TestStepConditionType.LOOP_FOR:
        return "fa-power-loop text-warning"
      case TestStepConditionType.CONDITION_IF:
        return "fa-conditional-if text-warning"
      case TestStepConditionType.CONDITION_ELSE_IF:
        return "fa-conditional-if text-warning"
    }
  }
}
