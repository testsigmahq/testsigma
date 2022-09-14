import { Component, OnInit, Input } from '@angular/core';
import {RecoverAction} from "../../enums/recover-action.enum";
import {OnAbortedAction} from "../../enums/on-aborted-action.enum";
import {PreRequisiteAction} from "../../enums/pre-requisite-action.enum";
import { FormGroup } from '@angular/forms';
import {ReRunType} from "../../enums/re-run-type.enum";
import {TestSuiteService} from "../../services/test-suite.service";

@Component({
  selector: 'app-test-plan-recovery-actions',
  templateUrl: './test-plan-recovery-actions.component.html',
  styles: [
  ]
})
export class TestPlanRecoveryActionsComponent implements OnInit {
  @Input('formGroup') testPlanFormGroup: FormGroup;
  public panelOpenState: Boolean = false;
  @Input('executionId') public executionId: number;
  public isDataDriven: Boolean;

  constructor(private testSuiteService: TestSuiteService) { }

  get recoverActions() {
    return Object.keys(RecoverAction);
  }

  get abortActions() {
    return Object.keys(OnAbortedAction);
  }

  get preRequisiteActions() {
    return Object.keys(PreRequisiteAction);
  }

  get reRunType() {
    return Object.keys(ReRunType);
  }

  ngOnInit(): void {
    this.testPlanFormGroup.valueChanges.subscribe(()=> {
      let testSuites = this.getTestSuites;
      if (testSuites.length > 0) {
        this.testSuiteService.findAll(`id@${testSuites.join('#')}`+ ",hasDataDrivenCases:"+true).subscribe(res => {
          if(res.content.length > 0)
            this.isDataDriven = true;
          else
            this.isDataDriven = false;
        })
      }
      else
        this.isDataDriven = false;
    });
  }

  reRunTypeChange(reRunType: ReRunType){
    this.testPlanFormGroup.controls['reRunType'].setValue(reRunType);
  }

  get getEnvironments(){
    return this.testPlanFormGroup.controls['testDevices'];
  }

  get getTestSuites(){
    let testSuites = [];
    this.getEnvironments?.value?.forEach(res => {testSuites.push(...res?.suiteIds)});
    let uniTestSuites = testSuites.filter((c, index) => {
      return testSuites.indexOf(c) === index;
    });
    return uniTestSuites;
  }
}
