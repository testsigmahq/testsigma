import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Page} from "../../models/page";
import {PageObject} from "../../models/page-object";
import {FormControl, FormGroup} from '@angular/forms';
import {debounceTime} from 'rxjs/operators';
import {Base} from "../../models/base.model";
import {TestPlanLabType} from '../../../enums/test-plan-lab-type.enum';

@Component({
  selector: 'app-auto-complete',
  template: `
    <mat-form-field
      class="w-100"
      [class.inline-no-border]="inline"
      [class.mat-select-custom]="inline"
      [class.no-bg]="inline"
      [class.sm]="inline"
      [formGroup]="formGroup"
      [appearance]="inline ? 'fill': 'outline'" (click)="groupTrigger.openPanel();focusOnMatSelectSearch()">
      <div>
      <i
        [class.icon-top]="isNativeIcons"
        class="switcher-icon fa-{{this.projectIcon}}"  *ngIf="hasProjectIcon"></i>
      <i
        [class.icon-top]="isNativeIcons"
        class="switcher-icon z-in-10 fa-{{this.applicationIcon}}" *ngIf="hasApplicationIcon"
      [style]="'top:-4px;left:-5px'">
      </i>
      <input
        type="text" readonly [class.pl-20]="hasProjectIcon || hasApplicationIcon"
             [value]="noneValue ? 'None' : value?.name"
             class="autocomplete-placeholder text-truncate z-in-2 py-10 my-n10 pl-10 ml-n8">
      <i class="fa-down-arrow-filled z-in-2 px-14 mx-n14 py-15 my-n15" [class.fz-12]="inline"></i>
      </div>
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
          <input (keydown)="$event.stopPropagation()"
            type="text" [formControl]="searchAutoComplete" autocomplete="off">
        </mat-option>
        <mat-option
          [disabled]="isDisabled"
          *ngIf="hasNone && !searchAutoComplete?.value?.length"
          [value]="-1"
          (click)="setNoneValue()"
          [textContent]="'message.common.none' | translate"></mat-option>
        <mat-option
          [class.px-10]="hasProjectIcon || hasApplicationIcon"
          [disabled]="isDisabled"
          *ngFor="let item of activeItems"
          [value]="item.id"
          [class.is_icon]="hasProjectIcon || hasApplicationIcon"
          (click)="setValue(item)">
          <i class="lh-1 mr-6 d-inline-block" *ngIf="hasProjectIcon && this.item.hasMultipleApps"
             [class.fa-all-items]="this.item.hasMultipleApps"
          ></i>
            <i class="lh-1 mr-6 d-inline-block"
               *ngIf="hasProjectIcon && !this.item.hasMultipleApps"
               [class.fa-project-website]="this.item.projectType == 'WebApplication'"
               [class.fa-project-ios]="this.item.projectType == 'IOSNative'"
               [class.fa-project-andriod]="this.item.projectType == 'AndroidNative'"
               [class.fa-project-mobile]="this.item.projectType == 'MobileWeb'"
               [class.fa-project-api]="this.item.projectType == 'Rest'"
            ></i>
          <i class="lh-1 mr-6 d-inline-block"
             *ngIf="hasApplicationIcon"
             [class.fa-project-website]="this.item.workspaceType == 'WebApplication'"
             [class.fa-project-ios]="this.item.workspaceType == 'IOSNative'"
             [class.fa-project-andriod]="this.item.workspaceType == 'AndroidNative'"
             [class.fa-project-mobile]="this.item.workspaceType == 'MobileWeb'"
             [class.fa-project-api]="this.item.workspaceType == 'Rest'"
          ></i>
            <span [class.set-inline-width]="hasProjectIcon || hasApplicationIcon" [textContent]="item.name + (item?.suffix ? item?.suffix : '')"></span>
        </mat-option>
        <mat-option
          *ngFor="let item of disabledItems"
          [value]="item.id"
          [disabled]="true"
          class="disabled text-t-secondary">
          <span
            style="height: 4px;line-height: 1"
            [matTooltip]="item?.suffixNext ? ('agents.list_view.version_out_of_sync' |
                    translate: {agentVersion: item?.agentVersion, latestVersion:
                    item?.currentAgentVersion}) : ''">
            <i
              *ngIf="item?.suffixNext"
              class="fa-exclamation-triangle-solid text-dark pr-6"></i>
            <span
              [textContent]="item.name +  (item?.suffixNext ? ' (' + ('agents.list_view.out_of_sync'|translate) + ')' : '')"></span>
          </span>
        </mat-option>
        <mat-option
          *ngIf="items?.content?.length == 0 && !!searchAutoComplete.value"
          [disabled]="true" [textContent]="'select.search.notfound'|translate"></mat-option>
      </mat-autocomplete>
    </mat-form-field>
  `,
  styles: []
})
export class AutoCompleteComponent implements OnInit {
  @Input('items') public items: Page<PageObject>;
  @Input('formCtrlName') public formControlName: FormControl;
  @Input('formGroup') public formGroup: FormGroup;
  @Input('value') public value?: Base;
  @Input('hasNone') hasNone: Boolean;
  @Input('hasProjectIcon') hasProjectIcon: Boolean;
  @Input('hasApplicationIcon') hasApplicationIcon: Boolean;
  @Input('isDisabled') isDisabled: Boolean;
  @Input('testPlanLabType') testPlanLabType: TestPlanLabType;
  @Input('inline') public inline?: Boolean;
  @Output('onSearch') onSearch = new EventEmitter<String>();
  @Output('onValueChange') onValueChange = new EventEmitter<Base>();

  public searchAutoComplete = new FormControl();
  public loadingSearch: boolean = false;
  public noneValue: Boolean = false;
  public projectIcon;
  public applicationIcon;

  constructor() {
  }

  ngOnChanges() {
    this.loadingSearch = false;
    //this.setIsDisabled();
    this.formControlName?.enable();
    if(this.value){
      this.hasIconsCheck()
    }
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
    this.onSearch.emit("");
  }

  setValue(item) {
    this.noneValue = false;
    this.value = item;
    if(this.value) {
      this.hasIconsCheck();
    }
    if(this.items) {
      let isContains = false;
      this.items.content.forEach(currentItem => {
        if (currentItem['id'] == this.value.id) {
          isContains = true;
        }
      })
      if(!isContains)
        this.items.content.push(item)
    }
    this.onValueChange.emit(item);
  }

  hasIconsCheck() {
    if(this.hasProjectIcon){
      this.projectIcon = this.setProjectIcon(this.value);
    } else if(this.hasApplicationIcon){
      this.applicationIcon =this.setIcons(this.value['workspaceType']);
    }
  }

  setProjectIcon(selectProject) {
    if(selectProject?.hasMultipleApps){
      return 'all-items';
    }
    return this.setIcons(selectProject?.projectType);
  }

  setIcons(workspaceType) {
    let iconSelect;
    switch(workspaceType) {
      case 'WebApplication': {
        iconSelect = 'project-website';
        break;
      }
      case 'IOSNative': {
        iconSelect = 'project-ios'
        break;
      }
      case 'AndroidNative': {
        iconSelect = 'project-andriod';
        break;
      }
      case 'Rest': {
        iconSelect = 'project-api';
        break;
      }
      case 'MobileWeb': {
        iconSelect = 'project-mobile'
        break;
      }
      default: {
        iconSelect = 'project-website';
        break;
      }
    }
    return iconSelect;
  }

  setNoneValue() {
    this.noneValue = true;
    this.onValueChange.emit(null);
  }

  get disabledItems(): PageObject[] {
    return this.items?.content?.filter((item: PageObject) => item['isDisabled']);
  }

  get activeItems(): PageObject[] {
    return this.items?.content?.filter((item: PageObject) => !item['isDisabled']);
  }

  setIsDisabled() {
    this.formControlName?.disable()
  }

  ngOnDestroy() {
    this.setIsDisabled();
  }
  get isNativeIcons() {
   return  this.applicationIcon == 'project-ios' || this.applicationIcon =='project-andriod';
  }
}
