import {Component, Inject, OnInit} from '@angular/core';
import {TestStep} from "../../models/test-step.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {TestStepPriority} from "../../enums/test-step-priority.enum";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TestStepConditionType} from "../../enums/test-step-condition-type.enum";
import {MatCheckboxChange} from '@angular/material/checkbox';
import {ResultConstant} from "../../enums/result-constant.enum";
import {MatSelectChange} from '@angular/material/select';

@Component({
  selector: 'app-test-step-more-action-form',
  templateUrl: './test-step-more-action-form.component.html',
  styles: []
})
export class TestStepMoreActionFormComponent implements OnInit {
  public prerequisiteList: TestStep[];
  public form: FormGroup;
  public formSubmitted: Boolean;
  public conditionalIf: boolean;
  public testStep: TestStep;
  public conditionIf : ResultConstant[] = [ResultConstant.SUCCESS, ResultConstant.FAILURE];
  public dataMapGroup: FormGroup;
  public priorityControl: FormControl;

  constructor(
    @Inject(MAT_DIALOG_DATA) public options: {
      testStep: TestStep,
      form: FormGroup,
      formSubmitted: Boolean,
      steps: TestStep[],
      parentDisabled: Boolean,
    },
    private matDialog: MatDialogRef<TestStepMoreActionFormComponent>) {
    this.form = this.options.form;
    this.formSubmitted = this.options.formSubmitted;
    this.testStep = this.options.testStep;
  }

  ngOnInit(): void {
    delete this.form.controls['ignoreStepResult'];
    this.addFormControls();
    this.addPriorityControl();
    if(this.testStep?.isConditionalElseIf || this.testStep?.isConditionalIf) {
      this.conditionalIf = true;
      this.form.controls['conditionType'].setValue(this.testStep?.isConditionalElseIf? TestStepConditionType.CONDITION_ELSE_IF : TestStepConditionType.CONDITION_IF);
      this.form.controls['conditionIf'].value.push(...this.testStep.conditionIf);
    }
    if (this.testStep?.conditionType) {
      this.form.controls['ignoreStepResult'].setValue(true);
      this.testStep.ignoreStepResult = true;
    }

    this.prerequisiteList = this.options.steps.filter(step => step.position < this.testStep.position);
  }

  addPriorityControl() {
    let priorityValue = this.form.controls.priority.value;
    this.priorityControl = new FormControl( priorityValue==TestStepPriority.MAJOR);
    this.priorityControl.valueChanges.subscribe(res => {
      if(res){
        this.form.controls.priority.setValue(TestStepPriority.MAJOR)
        this.form.controls.ignoreStepResult.setValue(undefined);
      } else{
        this.form.controls.priority.setValue(TestStepPriority.MINOR)
      }
    });
  }

  addFormControls() {
    this.form.addControl('waitTime', new FormControl(this.options.testStep.waitTime, [Validators.max(126)]));
    this.form.addControl('priority', new FormControl(this.options.testStep.priority, []));
    this.form.addControl('preRequisiteStepId', new FormControl(this.options.testStep.preRequisiteStepId, []));
    this.form.addControl('conditionType', new FormControl(this.options.testStep.conditionType, []));
    this.form.addControl('disabled', new FormControl((Boolean(this.options?.testStep?.parentStep?.disabled)||this.canAllowDisableStep? true :this.options.testStep.disabled), []));
    this.form.addControl("conditionIf", new FormControl(this.options.testStep.conditionIf,[]));
    this.form.addControl('ignoreStepResult', new FormControl(this.options.testStep.ignoreStepResult, []));
    this.dataMapGroup = new FormGroup({
      conditionIf: new FormControl(this.options.testStep.conditionIf, [])
    });
    this.form.addControl('dataMap', this.dataMapGroup);
  }


  get stepPriority() {
    return Object.values(TestStepPriority);
  }

  setTestConditionType($event: MatCheckboxChange) {
    if($event.checked) {
      this.form.controls['conditionType'].setValue(TestStepConditionType.CONDITION_IF);
      let conditionIf = this.testStep.conditionIf;
      if(!this.testStep?.id)
        conditionIf = [ResultConstant.SUCCESS]
      this.form.controls['conditionIf'].setValue(conditionIf);
      this.form.get('priority').setValue(TestStepPriority.MINOR);
    } else {
      this.form.controls['conditionType'].setValue(undefined);
      this.form.controls['conditionIf'].setValue([]);
      this.form.get('priority').setValue(this.testStep.priority);
    }
    if (this.form.controls['ignoreStepResult'].value) {
      this.testStep.ignoreStepResult = this.form.controls['ignoreStepResult'].value;
      this.form.controls['priority'].setValue(TestStepPriority.MINOR);
    }
  }

  removeCondition(result: ResultConstant) {
    if(this.form.controls['conditionIf'].value.length == 1)
      return;
    this.form.controls['conditionIf'].value.splice(this.form.controls['conditionIf'].value.indexOf(result), 1);
  }

  addCondition(result: ResultConstant) {
    if(!this.form.controls['preRequisiteStepId']?.value && result == ResultConstant.FAILURE)
      return;
    this.form.controls['conditionIf'].value.push(result);
  }

  resetOnPreRequisiteNone($event: MatSelectChange) {
    this.options.testStep.preRequisiteStepId = $event.value;
    this.form.controls['preRequisiteStepId'].setValue($event.value)
    if(!$event.value && this.dataMapGroup.controls['conditionIf'].value.indexOf(ResultConstant.FAILURE) > -1) {
      if(this.dataMapGroup.controls['conditionIf'].value.length == 1) {
        this.dataMapGroup.controls['conditionIf'].value.push(ResultConstant.SUCCESS);
        this.removeCondition(ResultConstant.FAILURE);
      }
    }
  }

  maxTimeValidate() {
    if(this.form.get('waitTime').value >121 || this.form.get('waitTime').value < 1) {
      this.formSubmitted = true;
    } else {
      this.matDialog.close()
    }
  }

  ngOnDestroy(){
    this.form.controls['dataMap'] = this.dataMapGroup;
  }

  changePriorityOverIgnoreResult($event: MatCheckboxChange) {
    if ($event.checked){
      this.form.controls.priority.setValue(TestStepPriority.MINOR);
      this.priorityControl.setValue(undefined);
    }
  }

  get canAllowDisableStep() {
    return this.testStep?.isConditionalWhileLoop ? this.options?.testStep?.parentStep?.parentStep ?
      this.options?.testStep?.parentStep?.parentStep?.disabled : false : this.options?.parentDisabled;
  }
}
