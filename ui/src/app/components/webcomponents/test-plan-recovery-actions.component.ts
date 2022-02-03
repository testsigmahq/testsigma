import { Component, OnInit, Input } from '@angular/core';
import {RecoverAction} from "../../enums/recover-action.enum";
import {OnAbortedAction} from "../../enums/on-aborted-action.enum";
import {PreRequisiteAction} from "../../enums/pre-requisite-action.enum";
import { FormGroup } from '@angular/forms';
import {ReRunType} from "../../enums/re-run-type.enum";

@Component({
  selector: 'app-test-plan-recovery-actions',
  templateUrl: './test-plan-recovery-actions.component.html',
  styles: [
  ]
})
export class TestPlanRecoveryActionsComponent implements OnInit {
  @Input('formGroup') testPlanFormGroup: FormGroup;
  public panelOpenState: Boolean = false;

  constructor() { }

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
  }



}
