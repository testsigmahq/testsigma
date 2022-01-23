/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {BaseComponent} from "../../shared/components/base.component";
import {TestStepResult} from "../../models/test-step-result.model";
import {TestStepResultService} from "../../services/test-step-result.service";
import {TestCaseService} from "../../services/test-case.service";

@Component({
  selector: 'app-test-step-result-details',
  templateUrl: './test-step-result-details.component.html',
  styles: [],
  host: {'class': 'd-flex ts-col-100'}
})
export class TestStepResultDetailsComponent extends BaseComponent implements OnInit {
  public testStepResultId: Number;
  public testStepResult: TestStepResult;
  public isFetchingCompleted: boolean = false;

  constructor(
              public route: ActivatedRoute,
              public testStepResultService: TestStepResultService,
              public testCaseService: TestCaseService,
              public router: Router) {
    super();
  }

  ngOnInit() {
    if(this.router.url.includes('step_results'))//TODO need to fix route[JAYAVEL]
    this.route.params.subscribe((params: Params) => {
      this.route.parent.params.subscribe((parentParams: Params) => {
        this.testStepResultId = params.resultId;
        let newData = Object.assign({testResultId: parentParams.resultId, resultId: this.testStepResultId}, params)
        this.pushToParent(this.route, newData);
        this.fetchTestStepResult();
      })
    });
  }

  fetchTestStepResult() {
    if(this.router.url.includes('step_results'))//TODO need to fix route[JAYAVEL]
    this.testStepResultService.show(this.testStepResultId).subscribe(res => {
      this.testCaseService.show(res?.testCaseId).subscribe(testcase => {
        res.testCase = testcase;
        if (res.isForLoop || res.isConditionalElse) {
          this.testStepResultService.findAll("parentResultId:" + res.id).subscribe(steps => {
            if (steps.content.length > 0)
              this.navigate(res.testCaseResultId, steps.content.find(st => st.isFailed || st.isAborted || st.isNotExecuted) || steps.content[0]);
            this.testStepResult = res;
          });
        } else if (res.isStepGroup) {
          this.testStepResultService.findAll("groupResultId:" + res.id).subscribe(steps => {
            if (steps.content.length > 0)
              this.navigate(res.testCaseResultId, steps.content.find(st => st.isFailed || st.isAborted || st.isNotExecuted) || steps.content[0]);
            this.testStepResult = res;
          })
        } else {
          this.testStepResult = res;
        }
        this.isFetchingCompleted = true;
      })
    });
  }

   navigate(testCaseResultId: Number, testStepResult: TestStepResult) {
    this.router.navigate(['/td/test_case_results', testCaseResultId, 'step_results', testStepResult.id]);
  }

}
