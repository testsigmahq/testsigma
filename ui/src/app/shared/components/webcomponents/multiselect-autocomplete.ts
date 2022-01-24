import {Component, ViewChild, ElementRef, Input, Output, EventEmitter} from '@angular/core';
import { FormControl } from '@angular/forms';
import {debounceTime, map, startWith} from 'rxjs/operators';
import {Page} from "../../models/page";
import {PageObject} from "../../models/page-object";

@Component({
  selector: 'app-multiselect-autocomplete',
    templateUrl: 'multiselect-autocomplete.html'
})

export class MultiSelectAutocomplete {

  @ViewChild('search') searchTextBox: ElementRef;
  @Input('items') public items: Page<PageObject>;
  @Input('value') public value?: any;
  @Output('onSearch') onSearch = new EventEmitter<String>();
  @Output('onValueChange') onValueChange = new EventEmitter<any>();
  selectFormControl = new FormControl();
  searchTextboxControl = new FormControl();
  selectedValues = [];
  filteredOptions: PageObject[];
  public loadingSearch: boolean;
  public selectedOptions: any[]=[];
  @Input('selectedPlaceholder') public selectedPlaceholder: PageObject[];

  ngOnInit() {
    this.filteredOptions=this.items.content;
    if(this.value) {
      this.selectFormControl = this.value;
    }
    this.searchTextboxControl.valueChanges.subscribe(() => this.loadingSearch = true);
    this.searchTextboxControl.valueChanges.pipe(debounceTime(1000)).subscribe((term) => {
      this.onSearch.emit(term)
    })
    this.setSelectedValues();
  }

  ngOnChanges() {
    this.loadingSearch = false;
    this.filteredOptions=this.items.content;
    //this.onValueChange.emit(this.selectFormControl.value);
  }


  selectionChange(event) {
    this.selectedValues = [...event.source.value];
    this.onValueChange.emit(this.selectedValues);
  }

  openedChange(e) {

    this.searchTextboxControl.patchValue('');
    this.onSearch.emit("");
    // Focus to search textbox while clicking on selectbox
    if (e == true) {
      this.searchTextBox.nativeElement.focus();
    }
  }

  clearSearch(event) {
    event.stopPropagation();
    this.searchTextboxControl.patchValue('');
    this.onSearch.emit(event);
  }

  setSelectedValues() {
    this.value=this.selectedValues;
    if (this.selectFormControl.value && this.selectFormControl.value.length > 0) {
      this.selectFormControl.value.forEach((e) => {
        if (this.selectedValues.indexOf(e) == -1) {
          this.selectedValues.push(e);
        }
      });
    }
    this.onValueChange.emit(this.selectedValues);
  }

  clearSelection(){
    this.selectFormControl.reset();
  }

}
