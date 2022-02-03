import { Component, OnInit, ViewChild, Input, Output, EventEmitter } from '@angular/core';
import { CdkConnectedOverlay } from '@angular/cdk/overlay';

@Component({
  selector: 'app-sort-by-button',
  templateUrl: './sort-by-button.component.html',
  styles: [
  ]
})
export class SortByButtonComponent implements OnInit {
  @Input('sortByColumns') sortByColumns: String[];
  @Input('sortedBy') sortedBy: String;
  @Input('direction') direction: String;
  @Input('translatePreFix') translatePreFix: String;
  @Output('sortAction') sortAction = new EventEmitter<{sortBy: String, direction: String}>();

  openSort: boolean;
  @ViewChild('sortByOptions') overlayDir: CdkConnectedOverlay;

  constructor() { }

  ngOnInit(): void {
  }

  openSortByOptions() {
    this.openSort = true;
    setTimeout(() => {
      this.overlayDir.overlayRef._outsidePointerEvents.subscribe(res => {
        this.overlayDir.overlayRef.detach();
        this.openSort = false;
      });
    }, 200);
  }

  sortBy(sortByValue: String, direction: String) {
    this.sortedBy = sortByValue;
    this.direction = direction;
    this.sortAction.emit({
      sortBy: sortByValue,
      direction: direction
    });
  }
}
