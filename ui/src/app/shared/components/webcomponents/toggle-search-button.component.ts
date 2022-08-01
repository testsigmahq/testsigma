import {Component, OnInit, Output, EventEmitter, ViewChild, ElementRef, Input} from '@angular/core';
import { fromEvent } from 'rxjs';
import { filter, debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';

@Component({
  selector: 'app-toggle-search-button',
  templateUrl: './toggle-search-button.component.html',
  styles: [
  ]
})
export class ToggleSearchButtonComponent implements OnInit {
  @Output('searchAction') searchAction = new EventEmitter<string>();
  @ViewChild('searchInput', {static: false}) searchInput: ElementRef;
  @Input('hasClose') hasClose = false;

  public showSearch: boolean = false;
  inputValue: any;

  constructor() { }

  ngOnInit(): void {
  }

  toggleSearch() {
    this.showSearch=(!this.showSearch);
    if(this.showSearch)
      this.attachSearchEvents()
    else
      this.clearSearch();
  }
  clearSearch() {
    this.searchInput.nativeElement.value = null;
    this.searchAction.emit('');
    this.inputValue = null;
  }

  attachSearchEvents() {
    if(this.searchInput?.nativeElement){
      this.attachDebounceEvent();
      setTimeout(() => this.searchInput?.nativeElement.focus(), 10);
    }
    else
      setTimeout(()=> this.attachSearchEvents(), 100);
  }

  attachDebounceEvent() {
    fromEvent(this.searchInput.nativeElement, 'keyup')
      .pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((_event: KeyboardEvent) => {
          let value;
          if (this.searchInput.nativeElement.value) {
            value = this.searchInput.nativeElement.value;
          }
          this.searchAction.emit(value);
        })
      )
      .subscribe();
  }
}
