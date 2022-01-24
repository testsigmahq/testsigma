import {Component, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {HttpHeaderNames} from "../../enums/http-header-names.enum";
import {RestStepEntity} from "../../models/rest-step-entity.model";

@Component({
  selector: 'app-rest-step-headers',
  template: `
    <form [formGroup]="headerForm">
      <ul class="ts-form p-0 m-0" #listJson>
        <li class="d-flex align-items-center pb-5"
            [formGroup]="headerControl"
            *ngFor="let headerControl of headerControls().controls; let isLast= last;let index = index;">

          <mat-form-field class="mat-custom-autocomplete ts-col-50 mr-10">
            <input type="text"
                   [placeholder]="(controlName == 'responseBodyJson' ? 'rest.step.placeholder.JSON_PATH' : (controlName == 'requestHeaders' || controlName == 'responseHeaders')?
                   'rest.step.placeholder.header_name' :'rest.step.placeholder.variable_name') | translate"
                   aria-label="Number"
                   (blur)="andEmptyRowIfMissing();"
                   class="form-control"
                   matInput
                   formControlName="key"
                   [matAutocomplete]="header">
            <mat-autocomplete #header="matAutocomplete">
              <mat-option *ngFor="let option of httpHeaderNames" [value]="option">
                {{option}}
              </mat-option>
            </mat-autocomplete>
          </mat-form-field>
<!--          <input-->
<!--            type="text"-->
<!--            [placeholder]="'common.key' | translate"-->
<!--            formControlName="key"-->
<!--            (blur)="andEmptyRowIfMissing();"-->
<!--            class="form-control mr-10"-->
<!--          >-->
          <input
            type="text"
            class="form-control ts-col-50"
            [placeholder]="(controlName == 'responseBodyJson' ? 'rest.step.placeholder.expected_value' : controlName == 'bodyRuntimeData'? 'rest.step.placeholder.JSON_PATH':
            (controlName == 'requestHeaders' || controlName == 'responseHeaders')?'rest.step.placeholder.header_value':
            'rest.step.placeholder.header_name')  | translate"
            formControlName="value"
            (blur)="andEmptyRowIfMissing();">
          <i
            *ngIf="!isLast"
            (click)="remove(index)"
            [matTooltip]="'btn.common.remove' | translate"
            class="fa-trash-thin pl-10 fz-16 pointer"></i>
          <i
            *ngIf="isLast"
            class="fa-trash-thin pl-10 fz-16 visibility-hidden"></i>
        </li>
      </ul>
    </form>
  `,
  styles: []
})
export class RestStepHeadersComponent implements OnInit {
  @Input('headers') headers: any;
  @Input('form') headerForm?: FormGroup;
  @Input('controlName') controlName: string;

  constructor(private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    if (this.headerForm.controls[this.controlName]) {
      (this.headerForm.controls[this.controlName]['controls']).forEach(header => {
        let key = header['controls'].key.value;
        let value = header['controls'].value.value;
        if (key != '' && value != '')
          this.headers[key] = value;
      });
      this.headerForm.removeControl(this.controlName)
    }
    this.headerForm.addControl(this.controlName, this.formBuilder.array([]));
    if (!this.headers || Object.keys(this.headers).length == 0)
      this.add();
    else
      this.populateControls();
  }

  populateControls() {
    Object.keys(typeof this.headers.response == 'string' ? new RestStepEntity().convertToJSONObject(this.headers.trim()): this.headers).forEach((key: string) => {
      this.headerControls().push(this.formBuilder.group({
        key: key,
        value: this.headers[key]
      }));
    });
    this.add();
  }

  headerControls(): FormArray {
    return this.headerForm.get(this.controlName) as FormArray;
  }

  newHeaderControl(): FormGroup {
    return this.formBuilder.group({
      key: '',
      value: ''
    })
  }

  add() {
    let empty = this.headerForm.value[this.controlName].find(header => header.key == '' && header.value == '');
    if (!empty)
      this.headerControls().push(this.newHeaderControl());
  }

  remove(i: number) {
    this.headerControls().removeAt(i);
  }

  andEmptyRowIfMissing() {
    let empty = this.headerForm.value[this.controlName].find(header => header.key == '' && header.value == '');
    if (!empty)
      this.add();
  }

  get httpHeaderNames() {
    return Object.keys(HttpHeaderNames);
  }
}
