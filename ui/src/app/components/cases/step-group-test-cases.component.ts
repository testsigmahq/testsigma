/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, OnInit} from '@angular/core';
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {TestCaseService} from "../../services/test-case.service";
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-test-step-test-cases',
  templateUrl: './step-group-test-cases.component.html',
  styles: []
})
export class StepGroupTestCasesComponent implements OnInit {
  public testCases: InfiniteScrollableDataSource;
  public testCaseId: number;

  constructor(
    private route: ActivatedRoute,
    private testCaseService: TestCaseService) {
  }

  ngOnInit(): void {
    this.testCaseId = this.route.parent.snapshot.params.testCaseId;
    this.fetchTestCases();
  }

  fetchTestCases() {
    this.testCases = new InfiniteScrollableDataSource(this.testCaseService, "deleted:false,stepGroupId:" + this.testCaseId);
    // let items = [];
    // this.testCases?.cachedItems.forEach(testcase => {
    //   if(items.forEach(item=> {
    //     item.id != testcase.id
    //   }))
    //   items.push(testcase)
    // })
  }
}
