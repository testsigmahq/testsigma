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
  public visualDiffEnabledStepResult:any[];
  public previousResult: TestStepResult;
  public nextResult: TestStepResult;
  public previousResultRoute: string;
  public nextResultRoute: string;
  public showItems: boolean = false;

  constructor() {
  }

  ngOnInit() {
    this.getVisualEnabledStepResult();
    this.setPaginationData();
  }

  getVisualEnabledStepResult(){
    if( this.filteredTestStepResult?.content?.length ){
      this.visualDiffEnabledStepResult = this.filteredTestStepResult.content.filter(( testStepResult )=>{
        return testStepResult.visualEnabled && testStepResult.stepResultScreenshotComparison?.id;
      })
    }else {
      this.visualDiffEnabledStepResult = [];
    }
  }
  setPaginationData() {
    if( this.visualDiffEnabledStepResult.length ){
      if( this.visualDiffEnabledStepResult.length === 1 ){
        this.previousResult = undefined;
        this.nextResult = undefined;
      }
      else{
        let condition = (compareItem) => compareItem.stepResultScreenshotComparison && compareItem.stepResultScreenshotComparison.id == this.currentComparison;
        let selectedDiffIndex = this.visualDiffEnabledStepResult.findIndex(condition);
        if( selectedDiffIndex !== 0 ){
          this.previousResult = this.visualDiffEnabledStepResult[selectedDiffIndex-1];
          this.previousResultRoute = this.getRouteFromResult(this.previousResult);
        }else{
          this.previousResult = undefined;
        };
        if( selectedDiffIndex < this.visualDiffEnabledStepResult.length-1 ){
          this.nextResult = this.visualDiffEnabledStepResult[selectedDiffIndex + 1];
          this.nextResultRoute = this.getRouteFromResult(this.nextResult);
        }else{
          this.nextResult = undefined;
        }
      }
    }
  }

  getRouteFromResult(result: TestStepResult): string {
    if (result)
    return result.constructor.name === "TestStepResult" ? `/td/test_case_results/${result?.testCaseResultId}/step_results/${result?.id}` : `/td/dry_test_case_results/${result?.testCaseResultId}/step_results/${result?.id}`;
  else return null;
  }

  selectList(id) {
    this.currentComparison = id;
    this.selectedScreenComparison.emit({id: id, stepResultList: this.filteredTestStepResult});
    this.setPaginationData()
  }
}
