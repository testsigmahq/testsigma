import {Component, Inject, OnInit} from '@angular/core';
import {TestCaseResultService} from "../../services/test-case-result.service";
import {Page} from "../../shared/models/page";
import {TestCaseResult} from "../../models/test-case-result.model";
import {ResultConstant} from "../../enums/result-constant.enum";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-test-case-result-list',
  templateUrl: './test-case-result-list.component.html',
  styles: []
})
export class TestCaseResultListComponent implements OnInit {
  public executionResultId: number;
  public testCaseResults: Page<TestCaseResult>;

  constructor(private testCaseResultService: TestCaseResultService,
              @Inject(MAT_DIALOG_DATA) public options: { executionResultId: number }) {
    this.executionResultId = options.executionResultId;
  }

  ngOnInit() {
    this.filter("");
  }


  filter(query: string) {
    query += ",testPlanResultId:" + this.executionResultId;
    this.testCaseResultService.findAll(query, undefined, undefined).subscribe(res => this.testCaseResults = res);
  }

  indexOfResult(constant: any) {
    return Object.keys(ResultConstant).indexOf(constant);
  }
}
