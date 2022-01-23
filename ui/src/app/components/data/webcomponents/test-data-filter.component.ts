import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {collapse} from "../../../shared/animations/animations";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";

@Component({
  selector: 'test-data-filter',
  templateUrl: './test-data-filter.component.html',
  animations: [collapse]
})
export class TestDataFilterComponent implements OnInit {
  @Input() filterByEnumList: string[];
  @Input() filterByValue: string;
  @Input() translatePrefix: string;
  @Output() filterAction = new EventEmitter<{filterBy: string,filterByStr:string}>();

  openFilter: boolean;
  @ViewChild('sortByOptions') overlayDir: CdkConnectedOverlay;
  private clickedOnFilterToClose: boolean;

  constructor() {}

  ngOnInit(): void {}

  openFilterByOptions(event:Event) {
    // event.preventDefault();
    // event.stopPropagation();
    if (this.clickedOnFilterToClose){
      this.clickedOnFilterToClose = false;
      return;
    }
    this.openFilter = true;
    setTimeout(() => {
      this.overlayDir.overlayRef._outsidePointerEvents.subscribe(res => {
        this.overlayDir.overlayRef.detach();
        if (this.eventFromFilterButton(res))
          this.clickedOnFilterToClose = true;
        else
          this.clickedOnFilterToClose = false;
        this.openFilter = false;
      });
    }, 200);
  }

  filter(filterByValue: string) {
    let filtersMap = new Map<string, string>([['all',undefined],['used','true'],['unused','false']]);
    this.filterByValue = filterByValue;
    // this.filteredByNum = filtersMap.get(filterByValue);
    this.filterAction.emit({
      filterBy: filterByValue,
      filterByStr: filtersMap.get(filterByValue)
    });
  }

  private eventFromFilterButton(res) {
    return res.path.find(path => path.nodeName == "TEST-DATA-FILTER")
  }
}
