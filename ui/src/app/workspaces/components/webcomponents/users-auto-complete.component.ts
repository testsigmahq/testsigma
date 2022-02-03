import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { debounceTime } from 'rxjs/operators';
import {PageObject} from "../../../shared/models/page-object";
import {Page} from "../../../shared/models/page";
import {Base} from "../../../shared/models/base.model";

@Component({
  selector: 'users-auto-complete',
  template: `
    <mat-form-field
      class="w-100 mat-select-custom"
      [class.mat-inline-autocomplete-no-border]="inline"
      [formGroup]="formGroup"
      appearance="outline" (click)="groupTrigger.openPanel();focusOnMatSelectSearch()">
      <input type="text" readonly
             [value]="noneValue ? 'None' : formGroup.get('user').value.email "
             class="autocomplete-placeholder">
      <i class="fa-down-arrow-filled"></i>
      <input
        type="text" matInput
        [formControl]="formControlName"
        #groupTrigger="matAutocompleteTrigger"
        [matAutocomplete]="autoComplete" hidden>
      <mat-autocomplete
        #autoComplete="matAutocomplete" disableOptionCentering>
        <mat-option class="p-4" [disabled]="true">
          <mat-progress-spinner
            class="search-spinner" mode="indeterminate" diameter="15" *ngIf="loadingSearch">
          </mat-progress-spinner>
          <input
            (keyup.space)="$event.stopImmediatePropagation()"
            (keydown.space)="$event.stopImmediatePropagation()"
            type="text" [formControl]="searchAutoComplete" autocomplete="off">
        </mat-option>
        <mat-option
          *ngIf="hasNone"
          [value]="-1"
          (click)="setNoneValue()"
          [textContent]="'message.common.none' | translate"></mat-option>
        <mat-option
          *ngFor="let item of items?.content"
          [value]="item"
          (click)="setValue(item)"
          [textContent]="item.email + (item?.suffix ? item?.suffix : '')">
        </mat-option>
        <mat-option
          *ngIf="items?.content?.length == 0 && !!searchAutoComplete.value"
          [disabled]="true" [textContent]="'select.search.notfound'|translate"></mat-option>
      </mat-autocomplete>
    </mat-form-field>
  `,
  styles: []
})
export class UserEmailsAutoCompleteComponent implements OnInit {

  @Input('items') public items: Page<PageObject>;
  @Input('formCtrlName') public formControlName: FormControl;
  @Input('formGroup') public formGroup: FormGroup;
  @Input('value') public value?: Base;
  @Input('hasNone') hasNone: Boolean;
  @Input('inline') public inline?: Boolean;
  @Output('onSearch') onSearch = new EventEmitter<String>();
  @Output('onValueChange') onValueChange = new EventEmitter<Base>();


  public searchAutoComplete = new FormControl();
  public loadingSearch: boolean = false;
  public noneValue: Boolean = false;

  constructor() {
  }

  ngOnChanges() {
    this.loadingSearch = false;
    if (this.hasNone) {
      if (!this.value) {
        this.setNoneValue();
      } else if (this.value) {
        this.setValue(this.value);
      }
    }
  }

  ngOnInit(): void {
    this.searchAutoComplete.valueChanges.subscribe(() => this.loadingSearch = true);
    this.searchAutoComplete.valueChanges.pipe(debounceTime(1000)).subscribe((term) => {
      this.onSearch.emit(term)
    })
  }

  focusOnMatSelectSearch() {
    let input = document.querySelector('.mat-select-panel-wrap input');
    input = input ? input : document.querySelector('.mat-autocomplete-panel input');
    if (input) {
      input.setAttribute("id", "matSearch");
      document.getElementById("matSearch")['value'] = "";
      document.getElementById("matSearch").focus();
    }
  }

  setValue(item) {
    this.noneValue = false;
    this.value = item;
    this.onValueChange.emit(item);
  }

  setNoneValue() {
    this.noneValue = true;
    this.onValueChange.emit(null);
  }


}
