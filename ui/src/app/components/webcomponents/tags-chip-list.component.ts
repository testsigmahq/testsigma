import { Component, OnInit, ElementRef, ViewChild, Input, Output, EventEmitter } from '@angular/core';
import {TestCaseTag} from "../../models/test-case-tag.model";
import {ElementTag} from "../../models/element-tag.model";
import { FormControl } from '@angular/forms';
import { ENTER, COMMA } from '@angular/cdk/keycodes';
import { debounceTime } from 'rxjs/operators';
import {TestCaseTagService} from "../../services/test-case-tag.service";
import {ElementTagService} from "../../services/element-tag.service";

@Component({
  selector: 'app-tags-chip-list',
  template: `
    <mat-form-field class="w-100 mat-custom-chip-container">
      <mat-chip-list #tagList class="mat-custom-chip-list">
        <mat-chip *ngFor="let tag of editTags" [selectable]="false"
                  [removable]="true" (removed)="removeTag(tag)">
          <span [textContent]="tag.name"></span>
          <mat-icon matChipRemove class="fa-close-large fz-10 mat-icon"></mat-icon>
        </mat-chip>
        <input #searchTag
               [formControl]="searchAutoComplete"
               [matChipInputFor]="tagList"
               [matChipInputSeparatorKeyCodes]="separatorKeysCodes"
               (matChipInputTokenEnd)="addNewTag(searchAutoComplete.value)"
               [matAutocomplete]="auto">
        <mat-autocomplete #auto="matAutocomplete">
          <mat-option *ngFor="let tag of filteredList"
                      [textContent]="tag.name" [value]="tag" (click)="addExistingTag(tag)"></mat-option>
          <mat-option *ngIf="searchAutoComplete.value && !searchAutoComplete.value?.name && isNotAdded(searchAutoComplete.value)"
                      class="pointer" [value]="searchAutoComplete.value"
                      (click)="addNewTag(searchAutoComplete.value)">
            <div>{{searchAutoComplete.value}}<span class="new-option-pill" [translate]="'btn.common.new'"></span></div>
          </mat-option>
          <mat-option *ngIf="!searchAutoComplete.value && !filteredList?.length"
                      [disabled]="true" [textContent]="'labels.select.no_tags_msg' | translate"></mat-option>
        </mat-autocomplete>
      </mat-chip-list>
    </mat-form-field>
  `,
  styles: [
  ]
})
export class TagsChipListComponent implements OnInit {
  @Input('entityId') entityId: number;
  @Input('service') tagService: TestCaseTagService | ElementTagService;
  @Output('onValueChange') onValueChange = new EventEmitter<String[]>();
  public tags: TestCaseTag[] | ElementTag[] = [];
  public tagsList: TestCaseTag[] | ElementTag[];
  public filteredList: TestCaseTag[] | ElementTag[];
  public searchAutoComplete = new FormControl();
  public editTags: TestCaseTag[] | ElementTag[] = [];
  public separatorKeysCodes: number[] = [ENTER, COMMA];
  public saving: boolean;
  public isDirty: boolean;
  @ViewChild('searchTag') searchTag: ElementRef;
  constructor() { }

  ngOnInit(): void {
    if(this.entityId)
      this.fetchAssociatedTags();
    else
      this.edit();
  }

  fetchAssociatedTags(): void {
    this.tagService.find(this.entityId).subscribe(res => {
      this.tags = res;
      this.edit();
    });
  }

  attachEvents() {
    this.searchAutoComplete = new FormControl();
    this.searchAutoComplete.valueChanges.pipe(debounceTime(200)).subscribe((term) => {
      if (term && typeof term == 'string')
        this.filteredList = this.filteredList.filter(tag => tag.name.toUpperCase().indexOf(term.toUpperCase()) > -1);
      else
        this.filteredList = this.tagsList.filter(tag => {
          return !this.editTags.find(res => res.id == tag.id || res.name.toUpperCase() == tag.name.toUpperCase());
        });
    });
    setTimeout(() => {
      this.searchTag.nativeElement.focus();
    }, 100);
  }


  removeTag(tag: TestCaseTag): void {
    this.editTags.splice(this.tags.indexOf(tag), 1);
    this.setValue();
    this.searchTag.nativeElement.blur();
    setTimeout(() => this.searchTag.nativeElement.focus(), 10);
    this.isDirty = true;
  }

  isNotAdded(name: string):boolean{
    return this.searchAutoComplete.value && !this.editTags.find(tag => tag.name == name);
  }

  addNewTag(name: string): void {
    if ((name || '').trim() && this.isNotAdded(name)) {
      let tag = new TestCaseTag();
      tag.name = name.trim();
      this.editTags.push(tag);
    }
    this.setValue();
    this.searchTag.nativeElement.value = '';
    this.searchAutoComplete.setValue('');
    this.searchTag.nativeElement.blur();
    setTimeout(() => this.searchTag.nativeElement.focus(), 10);
    this.isDirty = true;
  }

  edit() {
    this.editTags = [];
    if(this.tags)
      this.editTags = [...this.tags];
    this.setValue();
    this.tagService.findAll().subscribe(res => {
      this.tagsList = res;
      console.log("result"+res);
      this.filteredList = this.tagsList.filter(tag => !this.tags.find(res => res.id == tag.id));
      this.attachEvents();
    });
  }

  addExistingTag(tag: TestCaseTag | ElementTag) {
    this.editTags.push(tag);
    this.setValue();
    this.searchTag.nativeElement.value = '';
    this.searchTag.nativeElement.blur();
    setTimeout(() => this.searchTag.nativeElement.focus(), 10);
    this.isDirty = true;
  }

  setValue() {
    let tags = [];
    this.editTags.filter(tag => { tags.push(tag.name)})
    this.onValueChange.emit(tags)
  }
}
