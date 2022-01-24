/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Page} from "../../shared/models/page";
import {TestStepResult} from "../../models/test-step-result.model";

@Component({
  selector: 'app-visual-comparison-list',
  templateUrl: './visual-comparison-list.component.html',
  styles: []
})
export class VisualComparisonListComponent implements OnInit {
  @Input('filteredTestStepResult') filteredTestStepResult: Page<any>;
  @Input('currentComparison') currentComparison: Number;
  @Output('selectedScreenComparison') selectedScreenComparison = new EventEmitter<any>();
  public previousResult: TestStepResult;
  public nextResult: TestStepResult;
  public showItems: boolean = false;

  constructor() {
  }

  ngOnInit() {
    this.setPaginationData()
  }

  setPaginationData() {
    if (this.filteredTestStepResult && this.filteredTestStepResult.content && this.filteredTestStepResult.content.length > 1) {
      let condition = (compareItem) => compareItem.stepResultScreenshotComparison && compareItem.stepResultScreenshotComparison.id == this.currentComparison;
      let currentIndex = this.filteredTestStepResult.content.findIndex(condition);
      if (this.filteredTestStepResult.content[currentIndex - 1]) {
        this.previousResult = this.filteredTestStepResult.content[currentIndex - 1];
      } else {
        this.previousResult = undefined;
      }
      if (this.filteredTestStepResult.content[currentIndex + 1]) {
        this.nextResult = this.filteredTestStepResult.content[currentIndex + 1];
      } else {
        this.nextResult = undefined;
      }
    } else {
      this.previousResult = undefined;
      this.nextResult = undefined;
    }
  }

  selectList(id) {
    this.currentComparison = id;
    this.selectedScreenComparison.emit({id: id, stepResultList: this.filteredTestStepResult});
    this.setPaginationData()
  }
}
