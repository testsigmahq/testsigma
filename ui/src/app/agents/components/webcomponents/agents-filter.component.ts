import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {collapse} from "../../../shared/animations/animations";
import {CdkConnectedOverlay} from "@angular/cdk/overlay";

@Component({
  selector: 'agents-filter',
  templateUrl: './agents-filter.component.html',
  animations: [collapse]
})
export class AgentsFilterComponent implements OnInit {
  @Input('isEmpty') isEmpty?: Boolean;
  @Input('filterByColumns') filterByColumns: String[];
  @Input('filteredBy') filteredBy: string;
  @Input('translatePreFix') translatePreFix: string;
  @Output('filterAction') filterAction = new EventEmitter<{filterBy: String}>();

  openFilter: boolean;
  @ViewChild('sortByOptions') overlayDir: CdkConnectedOverlay;
  private clickedOnFilterToClose: boolean;

  constructor() { }

  ngOnInit(): void {
  }

  openFilterByOptions() {
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

  filterBy(filterByValue: string) {
    this.filteredBy = filterByValue;
    this.filterAction.emit({
      filterBy: filterByValue
    });
  }

  private eventFromFilterButton(res) {
    return res.path.find(path => path.nodeName == "AGENTS-FILTER")
  }
}
