import { Component, OnInit, Input } from '@angular/core';
import {InfiniteScrollableDataSource} from "../../data-sources/infinite-scrollable-data-source";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestCaseService} from "../../services/test-case.service";
import {OnBoarding} from '../../enums/onboarding.enum';
import {Router} from "@angular/router";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import {UserPreferenceService} from "../../services/user-preference.service";
import {UserPreference} from "../../models/user-preference.model";

@Component({
  selector: 'app-test-case-coverage-count',
  template: `
      <div class="coverage-summary" (click)="navigateToTestCase('all')">
        <div class="align-items-center d-flex fa-test-cases-alt icon justify-content-around result-status-text-0"></div>
        <div class="align-items-baseline d-flex flex-column justify-content-center ts-col-50">
          <div class="dashboard-secondary-title" [translate]="'dashboard.summary.total'"></div>
          <div>
            <ng9-odometer class="fz-24"  [number]="0" *ngIf="totalTestCases?.totalElements < 10"></ng9-odometer>
            <ng9-odometer class="fz-24"  [number]="totalTestCases?.totalElements"></ng9-odometer>
          </div>
        </div>
      </div>
      <div class="coverage-summary" (click)="navigateToTestCase('passed')">
        <div class="align-items-center d-flex justify-content-around icon fa-result-0 result-status-text-0"></div>
        <div class="align-items-baseline d-flex flex-column justify-content-center ts-col-50">
          <div  class="dashboard-secondary-title" [translate]="'dashboard.summary.passed'"></div>
          <div>
            <ng9-odometer class="fz-24"  [number]="0" *ngIf="passedTestCases?.totalElements < 10"></ng9-odometer>
            <ng9-odometer class="fz-24"  [number]="passedTestCases?.totalElements"></ng9-odometer>
          </div>
        </div>
      </div>
      <div class="coverage-summary" (click)="navigateToTestCase('failed')">
        <div class="align-items-center d-flex justify-content-around icon fa-result-1-dashboard fa-result-1"></div>
        <div class="align-items-baseline d-flex flex-column justify-content-center ts-col-50">
          <div  class="dashboard-secondary-title" [translate]="'dashboard.summary.failed'"></div>
          <div>
            <ng9-odometer class="fz-24"  [number]="0" *ngIf="failedTestCases?.totalElements < 10"></ng9-odometer>
            <ng9-odometer class="fz-24"  [number]="failedTestCases?.totalElements"></ng9-odometer>
          </div>
        </div>
      </div>
      <div class="coverage-summary" (click)="navigateToTestCase('notExecuted')">
        <div class="align-items-center d-flex justify-content-around icon fa-result-3"></div>
        <div class="align-items-baseline d-flex flex-column justify-content-center ts-col-50">
          <div class="dashboard-secondary-title" [translate]="'dashboard.summary.not_executed'"></div>
          <div>
            <ng9-odometer class="fz-24"  [number]="0" *ngIf="failedTestCases?.totalElements < 10"></ng9-odometer>
            <ng9-odometer class="fz-24"  [number]="notExecutedTestCases?.totalElements"></ng9-odometer>
          </div>
        </div>
      </div>
  `,
  styles: [
  ]
})
export class TestCaseCoverageCountComponent implements OnInit {
  @Input('version') version: WorkspaceVersion;
  public totalTestCases: InfiniteScrollableDataSource;
  public passedTestCases: InfiniteScrollableDataSource;
  public failedTestCases: InfiniteScrollableDataSource;
  public notExecutedTestCases: InfiniteScrollableDataSource;
  public userPreference: UserPreference;

  constructor(
    public router: Router,
    public authGuard: AuthenticationGuard,
    private userPreferenceService: UserPreferenceService,
    private testCaseService: TestCaseService) { }

  ngOnInit(): void {
    this.fetchUserPreference();
    this.totalTestCases = new InfiniteScrollableDataSource(this.testCaseService, "deleted:false,isStepGroup:false,workspaceVersionId:"+this.version.id, undefined, 1);
    this.passedTestCases = new InfiniteScrollableDataSource(this.testCaseService, "deleted:false,isStepGroup:false,result:SUCCESS,workspaceVersionId:"+this.version.id, undefined, 1);
    this.failedTestCases = new InfiniteScrollableDataSource(this.testCaseService, "deleted:false,isStepGroup:false,result@FAILURE#ABORTED,workspaceVersionId:"+this.version.id, undefined, 1);
    this.notExecutedTestCases = new InfiniteScrollableDataSource(this.testCaseService, "deleted:false,isStepGroup:false,result@ QUEUED#STOPPED#NOT_EXECUTED#null,workspaceVersionId:"+this.version.id, undefined, 1);
  }

  fetchUserPreference() {
    if(this.authGuard) {
      this.userPreferenceService.show().subscribe(res => {
        this.userPreference = res;
      })
    } else {
      setTimeout( () => this.fetchUserPreference(), 300)
    }
  }

  navigateToTestCase(result?) {
    this.router.navigate(['/td', this.version.id, 'cases', 'filter', this.userPreference?.testCaseFilterId], { queryParams: {result:result}});
  }

}
