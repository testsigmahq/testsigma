import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {MobileElementRect} from "../../models/mobile-element-rect.model";
import {MobileElement} from "../../models/mobile-element.model";
import {fromEvent} from "rxjs";
import {debounceTime, distinctUntilChanged, filter, tap} from "rxjs/operators";

@Component({
  selector: 'app-source',
  templateUrl: './app-source-container.component.html'
})
export class AppSourceContainerComponent implements OnInit{
  @Input("isNativeAppEnabled") isNativeAppEnabled: boolean;
  @Input("dataSource") dataSource: any;
  @Input("inspectedElement") inspectedElement: MobileElementRect;
  @Input() searchedElements: MobileElementRect[];
  @Input() searchedElement: MobileElementRect;
  @Input("loading") loading: boolean
  @Output("highlightCanvas") highlightCanvas= new EventEmitter<MobileElement>()
  @Output() highlightCanvasOnHover= new EventEmitter<MobileElement>();
  @Output() mouseOutFromAppSource= new EventEmitter<void>();
  inputValue: string;
  @Output('searchAction') searchAction = new EventEmitter<string>();
  @ViewChild('searchInput', {static: false}) searchInput: ElementRef;
  public nativeAttributes = ["name","text","contentDesc","resourceId","value", "password"];
  public currentSearchIndex: number = -1;
  public closeSearch: boolean = true;
  get searchCount(): number{
    return document.getElementsByClassName('theme-yellow')?.length;
  };

  ngOnInit() {
    this.attachSearchEvents();
  }

  highLightCanvas(element: MobileElement) {
    this.highlightCanvas.emit(element);
  }

  highLightCanvasOnHover(element: MobileElement) {
    this.highlightCanvasOnHover.emit(element);
  }

  mouseoutFromAppSource() {
    this.mouseOutFromAppSource.emit();
  }

  searchAndScrollToFirstOccurrence(){
    this.currentSearchIndex = -1;
    this.findNextInAppSource()
  }

  findNextInAppSource(){
    if(this.currentSearchIndex >= 0)
      document.getElementsByClassName('theme-yellow')[this.currentSearchIndex].classList.remove('theme-emerald-green');
    this.currentSearchIndex++;
    document.getElementsByClassName('theme-yellow')[this.currentSearchIndex].scrollIntoView();
    document.getElementsByClassName('theme-yellow')[this.currentSearchIndex].classList.add('theme-emerald-green')
  }

  findPreviousInAppSource(){
    if(this.currentSearchIndex >= 0)
      document.getElementsByClassName('theme-yellow')[this.currentSearchIndex].classList.remove('theme-emerald-green');
    this.currentSearchIndex--;
    document.getElementsByClassName('theme-yellow')[this.currentSearchIndex].scrollIntoView();
    document.getElementsByClassName('theme-yellow')[this.currentSearchIndex].classList.add('theme-emerald-green')
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

  private attachDebounceEvent() {
    fromEvent(this.searchInput.nativeElement, 'keyup')
      .pipe(
        filter(Boolean),
        debounceTime(500),
        distinctUntilChanged(),
        tap((_event: KeyboardEvent) => {
          let value;
          if (this.searchInput.nativeElement.value) {
            value = this.searchInput.nativeElement.value;
            this.searchAndScrollToFirstOccurrence();
          }
          this.searchAction.emit(value);
        })
      )
      .subscribe();
  }
}
