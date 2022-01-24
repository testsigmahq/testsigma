import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TestCase} from "../../models/test-case.model";
import {TestCaseTagService} from "../../services/test-case-tag.service";
import {TestCasePrioritiesService} from "../../services/test-case-priorities.service";
import {TestCaseTypesService} from "../../services/test-case-types.service";
import {BaseComponent} from "../../shared/components/base.component";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";
import { NotificationsService } from 'angular2-notifications';
import {TranslateService} from '@ngx-translate/core';
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-test-case-summary',
  templateUrl: './test-case-summary.component.html',
  styles: []
})
export class TestCaseSummaryComponent extends BaseComponent implements OnInit {
  public testCase: TestCase;
  constructor(
    public authGuard: AuthenticationGuard,
    public notificationsService: NotificationsService,
    public translate: TranslateService,
    public toastrService: ToastrService,
    @Inject(MAT_DIALOG_DATA) public options: { testCase: TestCase },
    public testCaseTagService: TestCaseTagService,
    private testCasePriorityService: TestCasePrioritiesService,
    private testCaseTypeService: TestCaseTypesService
  ) {
    super(authGuard, notificationsService, translate, toastrService)
    this.testCase = this.options.testCase;
  }

  ngOnInit(): void {
    this.testCasePriorityService.show(this.testCase.priorityId).subscribe(res => {
      this.testCase.testCasePriority = res;
    })
    this.testCaseTypeService.show(this.testCase.type).subscribe(res => {
      this.testCase.testCaseType = res;
    })
  }
}
