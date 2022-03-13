import {Component, Input, OnInit, EventEmitter, Output} from '@angular/core';
import {Environment} from "../../models/environment.model";
import {AbstractControl, FormArray, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-environments-table',
  templateUrl: './environments-table.component.html'
})
export class EnvironmentsTableComponent implements OnInit {
  @Input('environment') environment: Environment;
  @Input('formGroup') environmentForm : FormGroup;
  @Input('edit') edit: Boolean = false;
  @Input('formSubmitted') formSubmitted;
  public selectAll: Boolean;
  public selectedList = [];
  constructor(
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    this.environmentForm.removeControl('parameters');
    this.environmentForm.addControl('parameters', this.formBuilder.array([]));
    this.populateControls();
  }

  populateControls() {
    Object.keys(this.environment.parameters).forEach((key: string) => {
      if(key?.length || this.environment.parameters[key].length)
      this.rowControls().push(this.formBuilder.group({
        key: key,
        value: this.environment.parameters[key],
        selected: false,
      }));
    });

    this.addRowControl();
  }

  addRowControl() {
    this.rowControls().push(this.rowControl());
  }

  rowControl(): FormGroup {
    return this.formBuilder.group({
      key: '',
      value: '',
      selected: false
    })
  }

  removeRowControl(index: number): void {
    this.rowControls().removeAt(index);
  }

  rowControls(): FormArray {
    return this.environmentForm.get('parameters') as FormArray;
  }

  andEmptyRowIfMissing() {
    let empty = this.environmentForm.value['parameters'].find(header => header.key == '' && header.value == '');
    if (!empty)
      this.addRowControl();
  }

  public addOrRemoveFromSelectList() {
    this.rowControls().controls.forEach((parameter, index) => {
      if (parameter.value.selected && !this.selectedList.includes(index))
        this.selectedList.push(index);
      else if (!parameter.value.selected && this.selectedList.includes(index))
        this.selectedList.splice(index, 1);
    });
    this.selectAll = this.selectedList.length == this.rowControls().controls.length;
  }

  public removeMultipleParameters(): void {
    let selectedRows = this.selectedList.reverse();
    for (let i of selectedRows ) this.removeRowControl(i);
  }

  public selectAllToggle(selectAll: Boolean): void {
    this.selectedList = []
    this.rowControls().controls.forEach((control, index) => {
      control.value.selected = selectAll;
      if (selectAll) {
        this.selectedList.push(index);
      }
    });
  }

  isDuplicateParameters(key: String) {
    if(!this.formSubmitted)
      return false;
    let count = 0;
    this.environmentForm.value['parameters'].find( parameter => {
      if(parameter.key.trim() == key.trim()){
        count++;
      }
    })
    return count > 1;
  }

  isEmptyValue(parametersControl) {
    return parametersControl.value.value.trim().length == 0 && this.formSubmitted;
  }

}
