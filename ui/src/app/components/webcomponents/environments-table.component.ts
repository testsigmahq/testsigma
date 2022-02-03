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
  @Input('encryptedNames') encryptedNames: String[] = [];
  @Output('onEncryptedNames')onEncryptedNames = new EventEmitter<String[]>();
  @Input('formSubmitted') formSubmitted;
  public selectAll: Boolean;
  public selectedList = [];
  constructor(
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
    this.environmentForm.removeControl('parameters');
    this.environmentForm.addControl('parameters', this.formBuilder.array([]));
    this.environmentForm.addControl('encryptedNames', new FormControl(this.encryptedNames, []));
    this.populateControls();
  }

  populateControls() {
    Object.keys(this.environment.parameters).forEach((key: string) => {
      if(key?.length || this.environment.parameters[key].length)
      this.rowControls().push(this.formBuilder.group({
        key: key,
        value: this.environment.parameters[key],
        selected: false,
        encrypted: this.environment.passwords?.includes(key)
      }));
    });

    this.addRowControl();
    this.setEncryptedNames();
  }

  addRowControl() {
    this.rowControls().push(this.rowControl());
  }

  rowControl(): FormGroup {
    return this.formBuilder.group({
      key: '',
      value: '',
      selected: false,
      encrypted: false
    })
  }

  removeRowControl(index: number): void {
    this.rowControls().removeAt(index);
    this.setEncryptedNames();
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
    this.setEncryptedNames();
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

  encrypt(parametersControl: AbstractControl): void {
    parametersControl.setValue({...parametersControl.value,...{encrypted: true}});
    this.setEncryptedNames();
  }

  decrypt(parametersControl: AbstractControl): void {
    parametersControl.setValue({...parametersControl.value,...{encrypted: false}});
    this.setEncryptedNames();
  }

  isParameterEncrypted(parametersControl: AbstractControl): boolean {
    return parametersControl.value.encrypted;
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

  setEncryptedNames() {
    this.encryptedNames = this.rowControls().getRawValue().filter(parameter => parameter.encrypted && parameter.key?.length)
                                                          .map(parameter => parameter.key);
    this.environmentForm.controls.encryptedNames.setValue(this.encryptedNames);
  }
}
