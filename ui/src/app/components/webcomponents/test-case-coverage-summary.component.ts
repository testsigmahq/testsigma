import {Component, Input, OnInit} from '@angular/core';
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCaseService} from "../../services/test-case.service";
import {TestCaseCoverageSummary} from "../../models/test-case-coverage-summary.model";
import {OnBoarding} from "../../enums/onboarding.enum";

@Component({
  selector: 'app-test-case-coverage-summary',
  templateUrl: './test-case-coverage-summary.component.html',
  styles: [
  ]
})
export class TestCaseCoverageSummaryComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  public coverageSummary: TestCaseCoverageSummary;
  public automatedIsPending: boolean = false;
  public automatePercent: number;

  constructor(private testCaseService: TestCaseService) { }

  ngOnInit(): void {
    this.fetchCoverageSummary();
  }

  fetchCoverageSummary() {
    this.testCaseService.coverageSummary(this.version.id).subscribe(res => {
      this.coverageSummary = res;
      this.automationPending();
    });
  }

  automationPending() {
    let percent = (this.coverageSummary.automatedCount)*100;
    this.automatedIsPending = percent < 100;
    this.automatePercent = percent;
  }

  format(value) {
    console.log(value)
    return value;
  }

}
